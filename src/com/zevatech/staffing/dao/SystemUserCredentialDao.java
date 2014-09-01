package com.zevatech.staffing.dao;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.zevatech.staffing.vo.Credential;

public class SystemUserCredentialDao extends AbstractDao {

	private PreparedStatement selectStatement;
	
	public SystemUserCredentialDao() throws IOException {
		this(null);
	}
	
	public SystemUserCredentialDao(String propertyFilePath) throws IOException {
		super(propertyFilePath);
	}
	
	public void prepareSelectStatement() throws Exception {
		selectStatement = connection.prepareStatement("select User_ID, Password from SYSTEM_USER_CREDENTIAL_TBL where System_Name = ? and Zeva_ID = ?");
	}
	
	public void closeSelectStatement() throws Exception {
		selectStatement.close();
	}
	
	public Credential retrieveCredential(String systemID) throws Exception {
		Credential credential = null;
		
		selectStatement.setString(1, systemID);
		selectStatement.setInt(2, 1); //hardcode Zeva_ID for now
		ResultSet rs = selectStatement.executeQuery();
		if (rs.next()) {
			credential = new Credential();
			credential.setUserID(rs.getString("User_ID"));
			credential.setPassword(rs.getString("Password"));
			rs.close();
		}
		
		return credential;
	}
}
