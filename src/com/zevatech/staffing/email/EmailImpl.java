package com.zevatech.staffing.email;

import javax.mail.Session;


import javax.mail.Transport;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.NoSuchProviderException;
import javax.mail.AuthenticationFailedException;
import javax.mail.IllegalWriteException;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import org.apache.velocity.app.VelocityEngine;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;

import com.sun.mail.smtp.SMTPTransport;

import java.io.StringWriter;

import java.util.Map;
import java.util.Properties;

import java.util.Date;

/**
 * This class implements SmtpApi interface which is used for sending emails
 * using the SMTP Protocol.  Other implementations could follow as this concept
 * matures sucha as a POP3 api.
 * 
 */
class EmailImpl implements ISmtpApi {
	public static final String SUBJECT = "subject";
		
	private Properties props;

	private EmailMessageBean emailBean;

	private Session emailSession;

	private MimeMessage email;

	private SMTPTransport tp;

	private String smtpServer;

	private String popServer;

	private boolean authRequired;;

	private String mailUser;

	private String mailPassword;
	
	private boolean useTemplate;


	/**
	 * Creates an EmailImpl object which the host server is atuomatically discovered 
	 * from the application.properties configuration.  The user is not repsonsible for 
	 * identifying and supplying the Host server name. 
	 * 
	 * @throws EmailException
	 *           General intialization errors.
	 */
	public EmailImpl() throws EmailException {
		this.props = new Properties();
		// Set Outgoing Server
		this.smtpServer = System.getProperty("mail.smtp.host");
		this.props.put("mail.smtp.host", this.smtpServer);
		this.props.put("mail.smtp.port", System.getProperty("mail.smtp.port"));
		// Perform other initialization requirements
		this.initApi();
	}

	/**
	 * Creates an EmailImpl object which the identification of the host server's 
	 * name is supplied by the user.  Obtains other service information that is needed 
	 * to establish a valid connection such authentication requirements, and user 
	 * id/password.
	 * 
	 * @throws EmailException
	 *           General intialization errors.
	 */
	public EmailImpl(String hostName) throws EmailException {
		this.props = new Properties();
		this.smtpServer = hostName;
		// Perform other initialization requirements
		this.initApi();
	}

	/**
	 * Performs the bulk of instance initialization.  To date, the SMTP name is 
	 * obtained from the System properties collection and any authentication 
	 * information that may be needed to establish a valid service connection.
	 * respnsible for creating the email session object.
	 * 
	 * @throws EmailException
	 *           Problem obtaining the SMTP host server name from System properties. 
	 */
	protected void initApi() throws EmailException {
		this.emailSession = null;
		this.email = null;
		this.tp = null;
		this.authRequired = false;
		
		// Get Authentication value
		String temp = System.getProperty("mail.smtp.auth");
		this.authRequired = temp == null ? false : new Boolean(temp).booleanValue();

		// Get user name and password for mail server if authentication is required
		if (this.authRequired) {
			// Get User Name
			this.mailUser = System.getProperty("mail.server.userId");
			// Get Password
			this.mailPassword = System.getProperty("mail.server.password");
			// Enable SMTP authentication
			props.put("mail.smtp.auth", String.valueOf(authRequired));
		}

		props.put("mail.smtp.starttls.enable", System.getProperty("mail.smtp.starttls.enable"));
		
		// Initialize E-Mail Session
		this.emailSession = Session.getDefaultInstance(props, null);
		// Create a MIME style email message
		this.email = new MimeMessage(this.emailSession);
		// Set template usage indicator
		this.useTemplate = false;
		
		System.out.println("Email Session is opened");
	}

	/**
	 * Closes the this service and terminates its connection.  Also, member 
	 * variales are reinitialized to null.
	 * 
	 * @throws EMailException
	 *           for errors occurring during the closing process.
	 */
	public void closeTransport() throws EmailException {
		this.emailSession = null;
		this.email = null;
		this.smtpServer = null;
		this.popServer = null;

		try {
			if (this.tp != null && this.tp.isConnected()) {
				this.tp.close();
			}
		} 
		catch (MessagingException e) {
			throw new EmailException(e);
		} 
		finally {
			this.tp = null;
			
			System.out.println("Email Session is closed");
		}
	} // End close

	/**
	 * Creates and sends an email message using the data contained in <i>emailData</i>.
	 * 
	 * @param emailData
	 *          An instance of {@link EmailMessageBean EmailMessageBean}
	 *          containing data representing the components that comprises an email structure: 
	 *          'From', 'To', 'Subject', 'Body', and any attachments.
	 * @return int
	 *           Always returns 1.
	 * @throws EMailException
	 *           SMTP server is invalid or not named, validation errors, invalid 
	 *           assignment of data values to the email message, or email transmission 
	 *           errors.
	 */
	public int sendMail(EmailMessageBean emailData) throws EmailException {
		this.emailBean = emailData;
		this.validate();
		this.setupMessage();
		this.transportMessage();
		return 1;
	}

	
	
	/**
     * Creates and sends an email message using the concepts of "Mail Merge" using 
     * the template engine, Velocity, to build dynamic content.  <i>tempRootName</i> 
     * is the targeted template document containing the place holder variables that 
     * will be substituted by embedded dynamic content.
     * 
     * @param emailData
     *          An instance of {@link EmailMessageBean EmailMessageBean}
	 *          containing data representing the components that comprises an email structure: 
	 *          'From', 'To', 'Subject', and any attachments.  Content for the body component 
	 *          is not reuqired since this method will be dynamically managing it.
     * @param tempData 
     *          A Map containing the data that will replace the template's place holder 
     *          variables.
     * @param tempRootName
     *          The root filename of the template that is to be processed.   Do not include the 
     *          file extension since this process requires the template extension to exist as 
     *          ".vm".
     * @return int
     *          Always return 1.
     * @throws EmailException
     *          When the email template resource cannot be found, tempalte parsing failed due to 
     *          a syntax error, the invocation of the template failed, or a general SMTP error(s). 
     */
	public int sendMail(EmailMessageBean emailData, Map<Object, Object> tempData, String tempRootName) throws EmailException {
		
		this.useTemplate = true;
		this.emailBean = emailData;
		this.validate();
		//this.setupMessage();

		// Manage the velocity template
		VelocityContext context = null;
		Template template = null;
		String tempName = null;
		try	{
			// Create an instance of the Velocity engine
			VelocityEngine engine = this.createTemplateEngine();
			
			// Identify the template we want to work with
			tempName = tempRootName;
			template = engine.getTemplate(tempName);
						
			// Apply dynamic data values to email template.
			context = this.createTemplateContext(tempData, tempRootName);
			StringWriter writer = new StringWriter();
			template.merge(context, writer);			
			writer.close();
			
			// Assign results of template merge to the body of the actual mime message
			//String subject = context.get(SUBJECT).toString();
			//String emailType = System.getProperty("email.type");
			//if (emailType != null && !emailType.trim().equals("")) subject = "<<"+emailType+">> " + subject;
			//this.email.setSubject(subject);

			this.setupMessage();
			this.emailBean.setBody(writer.getBuffer().toString(), EmailMessageBean.TEXT_CONTENT);
			this.email.setContent(this.emailBean.assembleBody());
		}
		catch(ResourceNotFoundException e) {
			throw new EmailException("Email template: " + tempName + ", could not be found", e);
		}
		catch(ParseErrorException e) {
			throw new EmailException("Syntax error occurred in email template, " + template + ".  Problem parsing the template");
		}
		catch(MethodInvocationException e) {
			throw new EmailException("The invocation of email template, " + template + ", threw an exception");
		}
		catch(Exception e) {
			throw new EmailException("A general SMTP error occurred for email template: " + template, e);
		}

		// Send email
		this.transportMessage();
		return 1;
	}

	/**
	 * Creates an instance of the Velocity runtime engine which employs the 'Classpath' 
	 * resource loader to manage templates.
	 * 
	 * @return VelocityEngine
	 * @throws EmailException
	 *           engine failed to be initialized.
	 */
	private VelocityEngine createTemplateEngine() throws EmailException {
		// Identify and configure resource loader properties
		String resourceType = System.getProperty("mail.resourcetype");
		Properties props = new Properties();
		String resourceLoader = "resource.loader";
		String loaderName = null;
		String implClass = null;
		String path = ".resource.loader.path";
		
		if (resourceType.equalsIgnoreCase("file")) {
			// Setup file resource loader
			loaderName = "filesystem";
			implClass = loaderName + ".resource.loader.class";
			String filePath = System.getProperty("mail.templatepath");
			path = loaderName + path;
			props.setProperty(resourceLoader, loaderName);
			props.setProperty(implClass, FileResourceLoader.class.getName());
			props.setProperty(path, filePath);
		}
		else if (resourceType.equalsIgnoreCase("class")) {
			// Setup class resource loader
			loaderName = "classpath";
			implClass = loaderName + ".resource.loader.class";
			props.setProperty(resourceLoader, loaderName);
			props.setProperty(implClass, ClasspathResourceLoader.class.getName());
		}
		
		// Initialize velocity engine.
		VelocityEngine engine = new VelocityEngine();
		try {
			engine.init(props);
			return engine;
		} 
		catch (Exception e) {
			throw new EmailException(e);
		}
	}
	/**
	 * Creates the Velocity context instance using a Map of template data 
	 * values and the root name of the template file that will be processed.
	 * 
	 * @param data
	 *          The data that is to be applied to the template.
	 * @param tempName
	 *          The name of the template file without the file extension.
	 * @return {@link VelocityContext}
	 */
	private VelocityContext createTemplateContext(Map<Object, Object> data, String tempName) {
		VelocityContext context = new VelocityContext();
		context.put(tempName, data);
		return context;
	}
	
	/**
	 * Validates the EMailBean Object by ensuring that the object is
	 * instantiated, the To Address has at least on email address, and the From
	 * Address is populated with an email address.
	 * 
	 * @throws EMailException
	 *             if the email bean is null, the From-Address and/or the
	 *             To-Address is null, or the SMTP server is not recognized.
	 */
	private void validate() throws EmailException {
		if (this.smtpServer == null) {
			throw new EmailException("SMTP host name is invalid or has not been discovered");
		}
		if (this.emailBean == null) {
			throw new EmailException("Email bean object is not properly intialized");
		}
		if (this.emailBean.getFromAddress() == null) {
			throw new EmailException("Email From-Address is required");
		}
		if (this.emailBean.getToAddressList() == null) {
			throw new EmailException("Email To-Address is required");
		}
	}

	/**
	 * Assigns EmailMessageBean values to the MimeMessage component. Various Exceptions are
	 * caught based on a given issue.
	 * 
	 * @throws EMailException
	 *             When modifications are applied to an email bean component 
	 *             that is flagged as not modifyable or the occurrence of a 
	 *             general messaging error.
	 */
	private void setupMessage() throws EmailException {
		InternetAddress addr[];
		String component = null;

		// Begin initializing E-Mail Components
		try {
			component = this.emailBean.getFromAddress().toString();
			this.email.setFrom(this.emailBean.getFromAddress());
			
			//component = this.emailBean.getFromAddress().toString();
			//this.email.setReplyTo(new InternetAddress[] {this.emailBean.getFromAddress()});

			// Add To addresses
			component = InternetAddress.toString(this.emailBean.getToAddressList());
			component = "To Recipients";
			this.email.setRecipients(MimeMessage.RecipientType.TO, this.emailBean.getToAddressList());

			// Check if we need to add CC Recipients
			component = "CC Recipients";
			addr = this.emailBean.getCCAddressList();
			if (addr != null && addr.length > 0) {
				this.email.setRecipients(MimeMessage.RecipientType.CC, addr);
			}

			// Check if we need to add BCC Recipients
			component = "BCC Recipients";
			addr = this.emailBean.getBCCAddressList();
			if (addr != null && addr.length > 0) {
				this.email.setRecipients(MimeMessage.RecipientType.BCC, addr);
			}

			if (this.emailBean.getSubject() != null) {
				component = "Subject Line";
				this.email.setSubject(this.emailBean.getSubject());				
			}

			component = "Sent Date";
			this.email.setSentDate(new Date());
			component = "Header Line";
			this.email.setHeader("X-Mailer", "MailFormJava");

			// Do not manipulate body content if we are processing the body via Velocity
			if (!this.useTemplate) {
				component = "Body Content";
				this.email.setContent(this.emailBean.assembleBody());	
			}
		} 
		catch (IllegalWriteException e) {
			throw new EmailException("The following email bean componenet can not be modified: " + component);
		} 
		catch (IllegalStateException e) {
			throw new EmailException("The following email bean componenet exist in a read-only folder: " + component);
		} 
		catch (MessagingException e) {
			throw new EmailException("A generic messaging error occurred for email bean component: " + component);
		}
	}

	/**
	 * Performs the actual routing of message. If authentication is required,
	 * then a transport object is instantiated in order to email the message.
	 * Otherwise, the message is sent using a static call to "send" of the
	 * Transport object. Various Exceptions are caught based on a given issue.
	 * 
	 * @throws EmailException
	 */
	private void transportMessage() throws EmailException {
		Address addr[] = this.emailBean.getToAddressList();
		String msg = null;
		try {
			if (this.authRequired) {
				if (this.tp == null || !this.tp.isConnected()) {
					addr = this.emailBean.getToAddressList();
					this.tp = (SMTPTransport) this.emailSession.getTransport(addr[0]);
					this.tp.connect(this.smtpServer, this.mailUser,	this.mailPassword);
				}

				if (this.tp.isConnected()) {
					this.email.saveChanges();
					// Must use sendMessage to transport email since
					// authentication is required.
					this.tp.sendMessage(this.email, this.email.getAllRecipients());
					this.tp.close();
				}
			} else {
				// Send message the simple way
				Transport.send(this.email);
			}
			System.out.println("Email was sent successfully to " + this.emailBean.getRecipients());
		} 
		catch (NoSuchProviderException e) {
			throw new EmailException("No Such SMTP Provider Exist", e);
		} 
		catch (AuthenticationFailedException e) {
			throw new EmailException("Authentication Failed", e);
		} 
		catch (IllegalStateException e) {
			msg = "Illegal State error occurred for recipient(s): "	+ this.emailBean.getRecipients() + ".  Additional details: " + e.getMessage();
			throw new EmailException(msg, e);
		} 
		catch (SendFailedException e) {
			msg = "Email send failed error occurred for recipient(s): "	+ this.emailBean.getRecipients() + ".  Additional details: " + e.getMessage();
			throw new EmailException(msg, e);
		} 
		catch (MessagingException e) {
			msg = "General Messaging error occurred for recipient(s): "	+ this.emailBean.getRecipients();
			throw new EmailException(msg, e);
		}
	}

	/**
	 * Responsible for setting up the java MimeMessage object which is manipulate 
	 * from the email session. 
	 * 
	 * @param emailData
	 *          {@link EmailMessageBean EmailMessageBean}
	 */
	public void setEmailMessageBean(EmailMessageBean emailData) {
		this.emailBean = emailData;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getSmtpServer() {
		return this.smtpServer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSmtpServer(String value) {
		this.smtpServer = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getPopServer() {
		return this.popServer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setPopServer(String value) {
		this.popServer = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMailUser() {
		return this.mailUser;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMailUser(String value) {
		this.mailUser = value;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMailPassword() {
		return this.mailPassword;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMailPassword(String value) {
		this.mailPassword = value;
	}
	
	public boolean isAuthRequired() {
		return this.authRequired;
	}
	

}
