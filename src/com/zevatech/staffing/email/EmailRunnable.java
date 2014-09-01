package com.zevatech.staffing.email;

import com.zevatech.staffing.vo.Contact;

public class EmailRunnable implements Runnable {
	
	private final EmailSender emailSender;
	private final Contact contact;
	
	public EmailRunnable(EmailSender emailSender, Contact contact) {
		this.emailSender = emailSender;
		this.contact = contact;
	}

	/**
	 * 
	 */
	@Override
	public void run() {
		try {
			emailSender.sendToEmail(contact);
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}	
	}
}
