package com.zevatech.staffing.webcrawler;

import java.io.IOException;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class WebCrawler {

	protected WebDriver driver;
	protected Wait<WebDriver> wait;
	protected Wait<WebDriver> forever;
	
	protected Properties properties;
	public Properties getProperties() {
		return properties;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	protected String toolId = null;
	
	public WebCrawler(String toolId) throws IOException {
		this.toolId = toolId;
		
		properties = new Properties();		
		properties.load(getClass().getResourceAsStream(this.toolId + ".properties"));
		System.getProperties().putAll(properties);
	}
	
	public void startChrome() throws Exception {
		driver = new ChromeDriver();
		driver.manage().timeouts().pageLoadTimeout(120, TimeUnit.SECONDS);
		setUp();
	}

	public void startFirefox() throws Exception {
		driver = new FirefoxDriver();
		setUp();
	}
	
	public void startIE() throws Exception {
		driver = new InternetExplorerDriver();
		setUp();
	}
	
	protected void setUp() throws Exception {
		wait = new WebDriverWait(driver, 60);
		forever = new WebDriverWait(driver, 600);
	}
}
