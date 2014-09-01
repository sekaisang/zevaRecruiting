package com.zevatech.staffing.email;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.testng.reporters.Files;

import com.zevatech.staffing.vo.Contact;

public class EmailSender {
	
	private Properties properties;
	
	private String firstLineInSession;
	private String emailContentInSession;
	private String emailSubject;
	private String fromPerson;
	private String fromEmail;
	
	private EmailImpl emailImplInSession;
	
	private ExecutorService executor;
	
	public EmailSender() throws IOException {
		this(null);
	}
	
	public EmailSender(String propertyFilePath) throws IOException {
		properties = new Properties();
		if (propertyFilePath == null || propertyFilePath.trim().equals("")) {			
			properties.load(getClass().getResourceAsStream("email.properties"));
			String emailConfig = properties.getProperty("email.config");
			properties.load(getClass().getResourceAsStream(emailConfig + ".email.properties"));
		}
		else {
			properties.load(new FileInputStream(propertyFilePath));
		}
		
		emailSubject = properties.getProperty("mail.subject");
		fromPerson = properties.getProperty("mail.fromPerson");
		fromEmail = properties.getProperty("mail.fromAddress");
		
		System.getProperties().putAll(properties);
		
		System.out.println(properties);
	}
	
	/**
	 * Open the email server session and load the email content in memory
	 * @throws EmailException
	 * @throws IOException
	 */
	public void openEmailSession() throws EmailException, IOException {
		emailImplInSession = new EmailImpl();
		
		String emailContentPath = properties.getProperty("mail.contentPath");
		
		if (emailContentPath != null) {
			StringBuffer content = new StringBuffer();
			firstLineInSession = loadFile(emailContentPath, content);
			emailContentInSession = content.toString();
		}
		
		executor = Executors.newFixedThreadPool(10);
	}
	
	/**
	 * Close the email server session and clear the email content in memory
	 * @throws EmailException
	 */
	public void closeEmailSession() throws EmailException {
		// This will make the executor accept no new threads
	    // and finish all existing threads in the queue
	    executor.shutdown();
	    // Wait until all threads are finished
	    while (!executor.isTerminated()) {
	    }
	    
		firstLineInSession = null;
		emailContentInSession = null;
		emailImplInSession.closeTransport();
	}
	
	/**
	 * Send the bcc email to each email address specified in the mail.recipientEmailsPath,
	 * email content is specified in the mail.contentPath.
	 * It opens and closes the mail session in the method.
	 */
	public void sendBccEmails() throws EmailException, IOException {
		String recipientEmailsPath = properties.getProperty("mail.recipientEmailsPath");
		String emailList = loadFile(recipientEmailsPath).toString();
		sendBccEmails(emailList);
	}
	
	/**
	 * Send the bcc email to each email address specified in the emailList param,
	 * email content is specified in the mail.contentPath.
	 * It opens and closes the mail session in the method.
	 * @param emailList
	 * @throws EmailException
	 * @throws IOException
	 */
	public void sendBccEmails(String emailList) throws EmailException, IOException {	
		EmailImpl emailImpl = new EmailImpl();
		
		String emailContentPath = properties.getProperty("mail.contentPath");
		String emailContent = loadFile(emailContentPath);
		
		String toAddress = properties.getProperty("mail.toAddress");
		
		EmailMessageBean emailBean = new EmailMessageBean();
		emailBean.setToAddressList(toAddress);
		emailBean.setBCCAddressList(emailList);
		emailBean.setBody(emailContent, EmailMessageBean.TEXT_CONTENT);
		emailBean.setSubject(emailSubject);		
		emailBean.setFromAddress(fromPerson, fromEmail);
		
		emailImpl.sendMail(emailBean);
		
		emailImpl.closeTransport();		
	}
	
	/**
	 * Send email to each individual email address specified in the mail.recipientEmailsPath,
	 * email content is specified in the mail.contentPath.
	 * Recipient name will be addressed in the email content if it is included in the email address.
	 * It opens and closes the mail session in the method.
	 * @throws EmailException
	 * @throws IOException
	 */
	public void sendToEmails() throws EmailException, IOException {
		String recipientEmailsPath = properties.getProperty("mail.recipientEmailsPath");
		String emailList = loadFile(recipientEmailsPath).toString();
		sendToEmails(emailList);
	}
	
	/**
	 * Send individual emails to the given emailList.
	 * Recipient name will be addressed in the email content if it is included in the email address.
	 * It opens and closes the mail session in the method. 
	 * @param emails
	 * @throws EmailException
	 * @throws IOException
	 */
	public void sendToEmails(String emails) throws EmailException, IOException {
		sendToEmails(emails, emailSubject, fromPerson, fromEmail);
	}
	
	public void sendToEmails(String emails, String subject, String fromPerson, String fromEmailAddress) 
			throws EmailException, IOException {	
		String emailContentPath = properties.getProperty("mail.contentPath");
		StringBuffer content = new StringBuffer();
		String firstLine = loadFile(emailContentPath, content);
		String emailContent = content.toString();
		
		Set<Contact> contacts = toUniqueNameEmails(emails);
		
		EmailImpl emailImpl = new EmailImpl();
		
		for(Contact contact : contacts) {
			sendToEmail(contact, firstLine, emailContent, subject, fromPerson, fromEmailAddress, emailImpl);
		}
		
		emailImpl.closeTransport();		
	}
	
	private void sendToEmail(Contact contact, String firstLine, String emailContent, String subject, 
				String fromPerson, String fromEmailAddress, EmailImpl emailImpl) 
			throws EmailException, IOException {

		EmailMessageBean emailBean = new EmailMessageBean();
		emailBean.setToAddressList(contact.toString());
		
		String targetedEailContent = getTargetedEmailContent(firstLine, emailContent, contact.getFirstName());
		emailBean.setBody(targetedEailContent, EmailMessageBean.TEXT_CONTENT);
		
		emailBean.setSubject(subject);
		emailBean.setFromAddress(fromPerson, fromEmailAddress);
		emailImpl.sendMail(emailBean);
	}
	
	/**
	 * Send email to the given nameEmail in a separate thread. 
	 * @param contact
	 */
	public void sendToEmailInSeparateThread(Contact contact) {
		Runnable worker = new EmailRunnable(this, contact);
		executor.execute(worker);
	}
	
	/**
	 * Send a email to the recipient specified by the nameEmail parameter.
	 * This method assumes the caller has opened the email server before.
	 * Sending the email in a separate thread will not allow other threads to run the same 
	 * method until it is done
	 * @param contact
	 * @throws EmailException
	 * @throws IOException
	 */
	public synchronized void sendToEmail(Contact contact) 
			throws EmailException, IOException {
		sendToEmail(contact, firstLineInSession, emailContentInSession, null, null, null, emailImplInSession);
	}
	
	/**
	 * Return a new email content with the first name to the email.
	 * @param firstLine assumes it is "Hi,"
	 * @param emailContent email content without the first line
	 * @param firstName recipient's first name (i.e. "John")
	 * @return targeted email content with the first line like "Hi John,"
	 */
	private String getTargetedEmailContent(String firstLine, String emailContent, String firstName) {
		StringBuffer targetedFirstLine = new StringBuffer(firstLine);
		if (firstName != null) {
			return targetedFirstLine.insert(firstLine.indexOf(","), " " + firstName) + "\n" + emailContent;
		}
		else {
			return firstLine + "\n" + emailContent;
		}
	}
	
	/**
	 * Store the content of the file (from second line onwards) in the given content parameter and return the first line
	 * of the file
	 * @param filePath
	 * @param content
	 * @return first non empty line of the file
	 * @throws IOException
	 */
	private String loadFile(String filePath, StringBuffer content) throws IOException {
		String firstLine = null;
		
		String file = Files.readFile(new File(filePath));
		int fistLineIndex = file.indexOf("\n");
		firstLine = file.substring(0, fistLineIndex);
		content.append(file.substring(fistLineIndex + 1));
		
		/*
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(filePath)));
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				break;
			}
			
			if (line.trim().equals("")) {
				continue;
			}
			
			if (firstLine == null) {
				firstLine = line;
			}
			else {
				content.append(line + "\n");
			}
		}
		
		reader.close();*/
		
		return firstLine;
	}
	
	/**
	 * Load the content specified in the filePath to StringBuffer
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	private String loadFile(String filePath) throws IOException {
		return Files.readFile(new File(filePath));
		
	}
	
	/**
	 * Get the unique set of the emails from the input file
	 * @param emailsFilePath
	 * @return email strings that may contain email address as well name
	 * "John Doe"<john.doe@email.com> or <john.doe@email.com> or john.doe@email.com
	 * @throws IOException
	 */
	private Set<Contact> getUniqueNameEmails(String emailsFilePath) throws IOException {
		String emails = loadFile(emailsFilePath);
			
		return toUniqueNameEmails(emails);
	}
	
	/**
	 * Convert the given emails to a set of unique NameEmail pairs.
	 * If the emails contains duplicate email addresses, the first entry will precede. 
	 * The order of the email addresses in the given emails list is kept.
	 * @param emails
	 * @return
	 * @throws IOException
	 */
	private Set<Contact> toUniqueNameEmails(String emails) throws IOException {
		Set<Contact> nameEmailSet = new LinkedHashSet<Contact>();
		
		StringTokenizer st = new StringTokenizer(emails, "\n");
		while (st.hasMoreElements()) {
			String emailLine = ((String) st.nextElement()).trim();
			StringTokenizer st1 = new StringTokenizer(emailLine, ",");
			while (st1.hasMoreElements()) {
				String element = ((String) st1.nextElement()).trim();
				StringTokenizer st2 = new StringTokenizer(element, ";");				
				while (st2.hasMoreElements()) {
					String emailStr = ((String) st2.nextElement()).trim();
					
					if (emailStr.length() == 0) {
						continue;
					}
					
					nameEmailSet.add(new Contact(emailStr));
				}
			}
		}

		return nameEmailSet;
	}
	
	public String getEmailContent() {
		return firstLineInSession + "\n" + emailContentInSession;
	}
	
	public boolean isTest() {
		return Boolean.parseBoolean(properties.getProperty("mail.test"));
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		EmailSender sender = new EmailSender();
		
		if (sender.isTest()) {
			sender.sendToEmails("\"Jason Liu\"<jason.liu@zevatechnology.com>");
			/*
			sender.openEmailSession();
			sender.sendToEmailInSeparateThread(new NameEmail("Jason Liu", "jason.liu@zevatechnology.com"));
			sender.sendToEmailInSeparateThread(new NameEmail("Confidential", "jason.xiaojian.liu@gmail.com"));
			sender.sendToEmailInSeparateThread(new NameEmail("Test", "linglin88@gmail.com"));
			System.out.println("Emails fired");
			sender.closeEmailSession();
			*/
		}
		else {
			sender.sendToEmails();
			//sender.sendBccEmails();
		}
		System.out.println("Emails completed");
	}

	public String getFirstLineInSession() {
		return firstLineInSession;
	}

	public void setFirstLineInSession(String firstLineInSession) {
		this.firstLineInSession = firstLineInSession;
	}

	public String getEmailContentInSession() {
		return emailContentInSession;
	}

	public void setEmailContentInSession(String emailContentInSession) {
		this.emailContentInSession = emailContentInSession;
	}
	
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public void setFromPerson(String fromPerson) {
		this.fromPerson = fromPerson;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

}
