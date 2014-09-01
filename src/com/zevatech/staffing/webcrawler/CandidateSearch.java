package com.zevatech.staffing.webcrawler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.zevatech.staffing.email.EmailException;
import com.zevatech.staffing.email.EmailSender;
import com.zevatech.staffing.vo.Contact;


public abstract class CandidateSearch extends WebCrawler implements IConstants {

	protected int maxCandidates = 200;
	protected int candidateStart = 0;
	protected Date afterDate = null;
	protected String searchAgent = null;
	protected boolean skipViewed = false;
	protected boolean skipNoRelocation = false;
	protected boolean skipConfidential = false;
	protected boolean sortByDate = false;
	protected boolean ascending = false;
	protected boolean sendEmail = false;
	protected String outputPath = null;
	protected String userName = null;
	protected String password = null;
	
	protected EmailSender emailSender;
	
	protected List<Contact> contactList;
	protected int candidateCounter = 0;
	
	public CandidateSearch(String toolId) throws IOException, ParseException {
		super(toolId);
		this.setProperties(new Properties());
	}
	
	public void setProperties(Properties properties) {
		super.setProperties(properties);
		
		searchAgent = properties.getProperty(SEARCH_AGENT);
		skipViewed = Boolean.parseBoolean(properties.getProperty(SKIP_VIEWED));
		skipNoRelocation = Boolean.parseBoolean(properties.getProperty(SKIP_NO_RELOCATION));
		skipConfidential = Boolean.parseBoolean(properties.getProperty(SKIP_CONFIDENTIAL ));
		sortByDate = Boolean.parseBoolean(properties.getProperty(SORT_BY_DATE));
		ascending = Boolean.parseBoolean(properties.getProperty(ASCENDING));
		sendEmail = Boolean.parseBoolean(properties.getProperty(SEND_EMAIL));
		String maxCandidatesStr = properties.getProperty(MAX_CANDIDATES);
		if (maxCandidatesStr != null) {
			maxCandidates = Integer.parseInt(maxCandidatesStr);
		}
		String candidateStartStr = properties.getProperty(CANDIDATE_START);
		if (candidateStartStr != null) {
			candidateStart = Integer.parseInt(candidateStartStr);
		}
		
		String afterDateStr = properties.getProperty(AFTER_DATE);
		if (afterDateStr != null && !afterDateStr.trim().equals("")) {
			SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
			try {
				afterDate = formatter.parse(afterDateStr);
			}
			catch (ParseException ex) {
				throw new RuntimeException(ex);
			}
		}
		
		outputPath = properties.getProperty(OUTPUT_PATH);
		
		userName = properties.getProperty(USERNAME);
		
		password = properties.getProperty(PASSWORD);
		
		System.out.println(properties);
	}
	
	public void run() throws Exception {
		perform(null);
	}
	
	public String execute() throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		perform(bos);
		bos.close();
		return bos.toString();
	}
	
	protected abstract void perform(OutputStream os) throws Exception;
	
	protected void setUp() throws Exception {
		super.setUp();
		contactList = new ArrayList<Contact>();
		
		if (sendEmail == true) {
			
			emailSender = new EmailSender();
			emailSender.openEmailSession();
		}
	}
	
	protected void tearDown() {
		if (emailSender != null) {
			try {
				emailSender.closeEmailSession();
			}
			catch (Exception ignore) {
				System.err.println("Ignored: Unable to shut down the email session");
				ignore.printStackTrace();
			}
		}
	}
	
	protected void sendEmailIfDesired(Contact contact) throws EmailException, IOException {
		if (sendEmail == true) {
			emailSender.sendToEmailInSeparateThread(contact);
		}
	}
	
	protected String replaceDelimiter(String string) {
		if (string.indexOf(",") < 0) {
			return string;
		}
		
		return string.replace(',', '_');
	}
	
	protected void printEmails() throws IOException {
		File outputFile = new File(outputPath);
		String outputFileFolder = outputPath.substring(0, outputPath.lastIndexOf(File.separator));
		String outputFileName = outputFile.getName();
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date currentTime = new Date();
		String timestamp = formatter.format(currentTime);
		
		outputFileName = outputFileName + "." + toolId + "." + timestamp + ".txt";
		outputFile = new File(outputFileFolder + File.separator + outputFileName);
		
		OutputStream ps = new PrintStream(outputFile);
		printEmailsToStream(ps);				
		ps.close();
	}
	
	protected void printEmails(OutputStream ps) throws IOException {
		if (ps == null) {
			printEmails();
		}
		else {
			printEmailsToStream(ps);
		}
	}
	
	protected void printEmailsToStream(OutputStream ps) throws IOException {
		System.out.println("Total candidates matched: " + candidateCounter);
		System.out.println("Total candidates viewed: " + contactList.size());
		
		int groupIndex = 0;
		StringBuffer line = null;
		
		while (true) {
			if (contactList.size() > groupIndex) {
				int max = contactList.size() > groupIndex + 99 ? groupIndex + 99 : contactList.size();	
				line = new StringBuffer();
				
				for (int i = groupIndex; i < max; i ++) {
					Contact contact = contactList.get(i);
					
					String name = contact.getName();
					if (name != null) {
						line.append("\"");
						line.append(name);
						line.append("\"");
					}
					line.append("<");
					line.append(contact.getEmail() + ">,"); 
				}
				
				System.out.println(line);
				ps.write(line.toString().getBytes());
				ps.write("\\n".getBytes());
				
				System.out.println();
				ps.write("\\n".getBytes());
				
				groupIndex += 99;
			}
			else {
				break;
			}
		}
	}
	
	private void writeToFile() throws IOException {
		PrintStream ps = new PrintStream(outputPath);
		StringBuffer line = null;
		for (Contact contact : contactList) {
			line = new StringBuffer();
			line.append("\"");
			String name = contact.getName();
			if (name != null) {
				line.append(name);
			}
			line.append("\"<");
			line.append(contact.getEmail() + ">");
			ps.println(line);
		}
		ps.close();
	}

	public int getMaxCandidates() {
		return maxCandidates;
	}

	public void setMaxCandidates(int maxCandidates) {
		this.maxCandidates = maxCandidates;
	}

	public int getCandidateStart() {
		return candidateStart;
	}

	public void setCandidateStart(int candidateStart) {
		this.candidateStart = candidateStart;
	}

	public Date getAfterDate() {
		return afterDate;
	}

	public void setAfterDate(Date afterDate) {
		this.afterDate = afterDate;
	}

	public String getSearchAgent() {
		return searchAgent;
	}

	public void setSearchAgent(String searchAgent) {
		this.searchAgent = searchAgent;
	}

	public boolean isSkipViewed() {
		return skipViewed;
	}

	public void setSkipViewed(boolean skipViewed) {
		this.skipViewed = skipViewed;
	}

	public boolean isSkipNoRelocation() {
		return skipNoRelocation;
	}

	public void setSkipNoRelocation(boolean skipNoRelocation) {
		this.skipNoRelocation = skipNoRelocation;
	}

	public boolean isSkipConfidential() {
		return skipConfidential;
	}

	public void setSkipConfidential(boolean skipConfidential) {
		this.skipConfidential = skipConfidential;
	}

	public boolean isSortByDate() {
		return sortByDate;
	}

	public void setSortByDate(boolean sortByDate) {
		this.sortByDate = sortByDate;
	}

	public boolean isAscending() {
		return ascending;
	}

	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isSendEmail() {
		return sendEmail;
	}

	public void setSendEmail(boolean sendEmail) {
		this.sendEmail = sendEmail;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public int getCandidateCounter() {
		return candidateCounter;
	}

	public void setCandidateCounter(int candidateCounter) {
		this.candidateCounter = candidateCounter;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	public boolean getSkipViewed(){
		return skipViewed;
	}
	
	public EmailSender getEmailSender() {
		return emailSender;
	}
}
