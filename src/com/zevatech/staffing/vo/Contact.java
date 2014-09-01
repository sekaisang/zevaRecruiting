package com.zevatech.staffing.vo;

import java.util.StringTokenizer;


public final class Contact {

	private String firstName;
	private String lastName;
	private String email;
	
	public Contact() {
	}
	
	public Contact(String name, String email) {
		setName(name);
		setEmail(email);		
	}

	/**
	 * Decompose the email string in following format
	 * "John Doe"<john.doe@mail.com>
	 * or
	 * <john.doe@mail.com>
	 * or
	 * john.doe@mail.com
	 * @param emailStr
	 */
	public Contact(String emailStr) {
		if (emailStr.startsWith("\"")) {
			emailStr = emailStr.substring(1);
			setName(emailStr.substring(0, emailStr.indexOf("\"")).trim());
			setEmail(emailStr.substring(emailStr.lastIndexOf("\"") + 1).trim());
		}
		else {
			setEmail(emailStr);
		}
	}
	
	public void setName(String name) {
		if (name != null && !name.trim().equals("")) {
			StringTokenizer st = new StringTokenizer(name, " ");
			firstName = ((String) st.nextElement()).trim();
			if (st.hasMoreElements()) {
				lastName = ((String) st.nextElement()).trim();
			}
		}
	}

	public void setEmail(String email) {
		this.email = email;
		if (this.email != null && this.email.startsWith("<")) {
			this.email = this.email.substring(1, this.email.length() - 1).trim();
		}
	}

	public String getName() {
		if (firstName == null) {
			return null;
		}
		
		return firstName + " " + lastName;
	}

	public String getEmail() {
		return email;
	}
	
	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public int hashCode() {
		if (email != null) {
			return email.hashCode();
		}
		
		return 0;
	}
	
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		
		if (object == null || object.getClass() != this.getClass()) {
			return false;
		}
		
		Contact contact = (Contact) object;
		return this.getEmail().equals(contact.getEmail());
	}
	
	public String toString() {
		if (firstName != null) {
			return "\"" + firstName + (lastName == null ? "" : " " + lastName) + "\"<" + email + ">";
		}
		else if (email != null || !email.trim().equals("")){
			return email;
		}
		else {
			return "";
		}
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}
