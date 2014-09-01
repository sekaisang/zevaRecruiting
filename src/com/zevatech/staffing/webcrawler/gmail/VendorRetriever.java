package com.zevatech.staffing.webcrawler.gmail;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.zevatech.staffing.dao.ContactsDao;
import com.zevatech.staffing.vo.Contact;
import com.zevatech.staffing.webcrawler.WebCrawler;

public class VendorRetriever extends WebCrawler {

	private ContactsDao dao;
	
	public VendorRetriever() throws IOException {
		super("gmail");
		dao = new ContactsDao();
	}

	public void run() throws Exception {
		try {
			login();
			dao.setUp();
			dao.prepareInsertStatement();
			processSpamFolder();
			dao.closeInsertStatement();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			dao.tearDown();
			logout();
			driver.close();
			driver.quit();
		}
		
	}
	
	private void login() throws Exception {
		driver.get("https://accounts.google.com/ServiceLogin?service=mail&passive=true&rm=false&continue=https://mail.google.com/mail/&ss=1&scc=1&ltmpl=default&ltmplcache=2");		
		driver.findElement(By.id("Email")).sendKeys("resumes@zevatechnology.com");
		driver.findElement(By.id("Passwd")).sendKeys("zeva123hire");
		driver.findElement(By.id("signIn")).click();
		waitForMailPage();
	}
	
	private void logout() throws Exception {
		WebElement dropdownElement = driver.findElement(By.id("gbg4"));
		dropdownElement.click();
		WebElement logoutElement = driver.findElement(By.id("gb_71"));
		logoutElement.click();
	}
	
	private void waitForMailPage() {
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.id(":as")) != null;
			}
		});
	}
	
	private void waitForArialLabel() {
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				WebElement element = webDriver.findElement(By.xpath("//div[@id=':ro']//[@aria-label='Older']"));
				if (element != null && element.isDisplayed()) {
					return true;
				}
				
				return false;
			}
		});
	}
	
	private void processSpamFolder() throws Exception {
		WebElement asElement = driver.findElement(By.id(":as"));
		WebElement spamFolderElement = asElement.findElement(By.className("J-Ke"));
		spamFolderElement.click();
		Thread.sleep(5000);
		
		processSinglePage();
		
		//driver.get("https://mail.google.com/mail/u/0/?shva=1#spam");
		//Thread.sleep(3000);
		
		while (true) {
			System.out.println("Waiting... " + new Date());
			waitForArialLabel();
			System.out.println("Done " + new Date());
			
			WebElement nextElement = null;
			//WebElement pagingElement = driver.findElement(By.id(":ro"));
			//List<WebElement> divElementList = pagingElement.findElements(By.xpath(".//div[@aria-label='Older']"));
			nextElement = driver.findElement(By.xpath("//div[@id=':ro']//[@aria-label='Older']"));
			//nextElement = driver.findElement(By.xpath("//div[@id=':y8']"));
			//nextElement = driver.findElement(By.xpath("//div[@aria-disabled='true']"));
			/*
			String label = nextElement.getAttribute("aria-label");
			String role = nextElement.getAttribute("role");
			String tooltip = nextElement.getAttribute("data-tooltip");
			boolean enabled = nextElement.isEnabled();
			boolean displayed = nextElement.isDisplayed();*/
			
			/*
			for (WebElement divElement : divElementList) {
				String ariaLabel = divElement.getAttribute("aria-label");
				System.out.println(divElement.getAttribute("id"));
				if (ariaLabel != null && ariaLabel.equalsIgnoreCase("Older")) {
					nextElement = divElement;
					break;
				}
			}*/
			
			String disableNext = nextElement.getAttribute("aria-disabled");
			if (disableNext != null && disableNext.equalsIgnoreCase("true")) {
				break;
			}
			
			nextElement.click();
			Thread.sleep(10000);
			processSinglePage();
		}
	}
	
	private void processSinglePage() throws Exception {
		WebElement divElement = driver.findElement(By.id(":rr"));
		List<WebElement> bltHkeElementList = divElement.findElements(By.className("BltHke"));
		WebElement tableElement = null;
		for (WebElement bltHkeElement : bltHkeElementList) {
			String role = bltHkeElement.getAttribute("role");
			if (role != null && role.equalsIgnoreCase("main")) {
				WebElement uiElement = bltHkeElement.findElement(By.className("UI"));
				tableElement = uiElement.findElement(By.tagName("tbody"));
				break;
			}
		}
		
		List<WebElement> trElementList = tableElement.findElements(By.tagName("tr"));
		for (WebElement trElement : trElementList) {
			List<WebElement> spanElementList = trElement.findElements(By.tagName("span"));
			for (WebElement spanElement : spanElementList) {
				String email = spanElement.getAttribute("email");
				if (email != null) {
					String name = spanElement.getAttribute("name");
					Contact contact = new Contact(name, email);
					dao.insert(contact);
					break;
				}
			}
			
		}	
		
		dao.commit();
	}
	
	public static void main(String[] args) throws Exception {
		VendorRetriever gmail = new VendorRetriever();
		//gmail.startChrome();
		gmail.startFirefox();
		gmail.run();
	}
	
}
