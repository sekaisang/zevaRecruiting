package com.zevatech.staffing.email;

public interface ICommonMailApi {

	/**
	 * Gets the user id.
	 * 
	 * @return String
	 */
	String getMailUser();
	
	/**
	 * Sets the user id.
	 * 
	 * @param userId String
	 */
	void setMailUser(String userId);
	
	/**
	 * Gets the password.
	 *  
	 * @return String
	 */
	String getMailPassword();
	
	/**
	 * Sets the mail password.
	 * 
	 * @param password String
	 */
	void setMailPassword(String password);
	
	   /**
     * Sets the email bean.
     * 
     * @param msg
     */
	void setEmailMessageBean(EmailMessageBean msg);
	
	   /**
     * Closes the Transport object.
     * 
     * @throws EmailException
     */
    void closeTransport() throws EmailException;

}
