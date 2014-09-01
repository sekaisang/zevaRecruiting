package com.zevatech.staffing.dao;

import java.io.IOException;
import java.sql.PreparedStatement;

public class SystemUserConfigDao extends AbstractDao {

	private PreparedStatement selectStatement;
	
	public SystemUserConfigDao() throws IOException {
		this(null);
	}

	public SystemUserConfigDao(String propertyFilePath) throws IOException {
		super(propertyFilePath);
	}
	
	public void prepareSelectStatement() throws Exception {
		selectStatement = connection.prepareStatement("select Property_Name, Property_Value from SYSTEM_USER_CONFIG_TBL where System_User_ID = ?");
	}
	
	public void closeSelectStatement() throws Exception {
		selectStatement.close();
	}
}
