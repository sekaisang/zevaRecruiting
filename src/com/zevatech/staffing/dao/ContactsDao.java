package com.zevatech.staffing.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.zevatech.staffing.vo.Contact;

public class ContactsDao extends AbstractDao {

	private PreparedStatement selectStatement;
	private PreparedStatement insertStatement;
	
	public ContactsDao() throws IOException {
		this(null);
	}
	
	public ContactsDao(String propertyFilePath) throws IOException {
		super(propertyFilePath);
	}
	
	public void prepareInsertStatement() throws Exception {
		selectStatement = connection.prepareStatement("select EMAIL from CONTACTS_TBL where EMAIL = ?");
		insertStatement = connection.prepareStatement("insert into CONTACTS_TBL (FIRST_NAME, LAST_NAME, EMAIL) values (?, ?, ?)");
	}
	
	public void closeInsertStatement() throws Exception {
		selectStatement.close();
		insertStatement.close();
	}
	
	public boolean insert(Contact contact) throws Exception {		
		selectStatement.setString(1, contact.getEmail());
		ResultSet rs = selectStatement.executeQuery();
		if (rs.next()) {
			rs.close();
			return false;
		}
		
		insertStatement.setString(1, contact.getFirstName());
		insertStatement.setString(2, contact.getLastName());
		insertStatement.setString(3, contact.getEmail());
		
		insertStatement.execute();
		
		return true;
	}
	
}
