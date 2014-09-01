package com.zevatech.staffing.dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public abstract class AbstractDao {

	private Properties properties;
	
	protected Connection connection;
	
	public AbstractDao() throws IOException {
		this(null);
	}
	
	public AbstractDao(String propertyFilePath) throws IOException {
		properties = new Properties();
		if (propertyFilePath == null || propertyFilePath.trim().equals("")) {			
			properties.load(getClass().getResourceAsStream("db.properties"));
		}
		else {
			properties.load(new FileInputStream(propertyFilePath));
		}
		
		System.out.println(properties);
	}
	
	public void setUp() throws Exception {
		Class.forName(properties.getProperty("jdbcDriver"));  
		connection = DriverManager.getConnection(properties.getProperty("jdbcUrl"), "", "");
	}
	
	public void tearDown() throws Exception {
		if (connection != null && !connection.isClosed()) {
			connection.close();
		}
	}
	
	public void commit() throws Exception {
		connection.commit();
	}
}
