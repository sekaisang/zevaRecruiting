package com.zevatech.staffing.webcrawler.intuit;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.zevatech.staffing.vo.Contact;
import com.zevatech.staffing.webcrawler.CandidateSearch;

public class QuickbooksSearch extends CandidateSearch {

	public static void main(String[] args) throws Exception {
		QuickbooksSearch qb = new QuickbooksSearch();
		qb.startChrome();
		//qb.startFirefox();
		qb.run();
	}
	
	public QuickbooksSearch() throws Exception {
		super("quickbooks");
	}
	
	protected void perform(OutputStream os) throws Exception {
		driver.get("http://proadvisor.intuit.com/acm/alpha/search-criteria-v2.html?latitude=032.995&longitude=-096.785&zipCode=75217&_requestid=220552");	

		try {
			while (true) {
				waitForSearchResultPage();
				
				processResults();
				
				int pageNumber = Integer.parseInt(driver.findElement(By.name("pageNumberDisplay")).getText());
				int pageCount = Integer.parseInt(driver.findElement(By.name("pageCountDisplay")).getText());
				if (pageNumber == pageCount) {
					break;
				}
				
				System.out.println("Page " + pageNumber + " is completed");
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {	
			printEmails(os);
		}
	}
	
	private void waitForSearchResultPage() throws Exception {
		forever.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.className("checkboxAreaChecked")) != null && webDriver.findElement(By.name("pageCountDisplay")) != null
					&& webDriver.findElement(By.name("searchResult")) != null;
			}
		});
		
		Thread.sleep(10000);
	}
	
	private void processResults() throws Exception {
		WebElement nameElement = null;
		WebElement emailElement = null;
		
		List<WebElement> resultElements = driver.findElement(By.name("searchResults")).findElements(By.name("searchResult"));
		for (int i = 1; i < resultElements.size(); i++) {//skip the first empty searchResult
			WebElement resultElement = resultElements.get(i);
			//emailElement = resultElement.findElement(By.name("email")).findElement(By.xpath("a"));
			try {
				emailElement = resultElement.findElement(By.xpath("div[@class='col15']/span[@name='email']/a"));
			}
			catch (Exception ex) {
				continue;
			}
			if (emailElement != null) {
				String email = emailElement.getText();
				//nameElement = resultElement.findElement(By.name("name"));
				nameElement = resultElement.findElement(By.xpath("div[@class='col13']/a[@name='viewProfileLink']"));
				String name = nameElement.getText();
				if (nameElement != null) {
					name = replaceDelimiter(nameElement.getText());
				}
				if (email != null) {
					email = replaceDelimiter(email);
					contactList.add(new Contact(name, email));
				}
			}
		}
	}
}
