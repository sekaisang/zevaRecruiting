package com.zevatech.staffing.webcrawler.dice;

import java.io.IOException;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.zevatech.staffing.email.EmailException;
import com.zevatech.staffing.vo.Contact;
import com.zevatech.staffing.webcrawler.CandidateSearch;

public class DiceSearch extends CandidateSearch {
	
	private SimpleDateFormat formatter = null;
	
	public static void main(String[] args) throws Exception {
		DiceSearch dice = new DiceSearch();
		dice.startChrome();
		//dice.startFirefox();
		dice.run();
	}
	
	public DiceSearch() throws IOException, ParseException {
		super("dice");
		this.formatter = new SimpleDateFormat("MM/dd/yyyy");
	}

	protected void perform(OutputStream os) throws Exception {
		try {
			login();
			continueLoginIfRequired();
			loadSearchAgent();
			Thread.sleep(10000);
			scanCandidates();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		finally {
			printEmails(os);
			tearDown();
			logout();
			driver.close();
			driver.quit();
		}
	}
	
	private void login() throws Exception {
		driver.get("http://employer.dice.com/daf/servlet/DAFctrl?op=1201");		
		driver.findElement(By.name("USERNAME")).sendKeys(userName);
		driver.findElement(By.name("PASSWORD")).sendKeys(password);
		driver.findElement(By.id("login-button")).click();
		/*
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				WebElement searchAgentElement = null;
				WebElement continueLoginElement = null;
				try {
					searchAgentElement = webDriver.findElement(By.id("yui-gen11"));
				}
				catch (Exception ignore) {
					try {
						continueLoginElement = webDriver.findElement(By.id("CONTINUE-button"));
					}
					catch (Exception ignore2) {
					}
				}
				
				return searchAgentElement != null || continueLoginElement != null;
			}
		});*/
	}
	
	private void continueLoginIfRequired() throws Exception {
		WebElement continueLoginElement = null;
		try {
			continueLoginElement = driver.findElement(By.id("CONTINUE-button"));
			continueLoginElement.click();
		}
		catch (Exception ignore2) {
		}
		/*
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.id("yui-gen11")) != null;
			}
		});*/
	}
	
	private void logout() throws Exception {
		driver.findElement(By.linkText("Logout")).click();
	}
	
	private void loadSearchAgent() throws Exception {
		driver.get("http://employer.dice.com/talentmatch/servlet/TalentmatchSearch?op=1018");
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return getSearchAgentElement(webDriver) != null;
			}
		});
		
		getSearchAgentElement(driver).click();
		//driver.findElement(By.id("EXECUTE_BUTTON-button")).click();
		
		try {
			WebElement noPopupElement = driver.findElement(By.id("openViewDontShow"));
			noPopupElement.click();
		}
		catch (Exception ignore) {
		}
		
		waitForSearchResultPage();
	}
	
	private WebElement getSearchAgentElement(WebDriver webDriver) {
		List<WebElement> agentElements = webDriver.findElements(By.className("padRow"));
		for (WebElement agentElement : agentElements) {
			String agent = agentElement.findElement(By.className("saName")).getText().trim();
			agent = agent.substring(0, agent.indexOf("(")).trim();
			if (agent.equals(searchAgent)) {
				return agentElement.findElement(By.className("runSA"));
			}
		}
		
		return null;
	}
	
	private void waitForSearchResultPage() {
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.id("candidates")) != null;
			}
		});
	}
	
	private void scanCandidates() throws Exception {
		System.out.println("Scanning page 1");
		
		scanCandidatesOnPage(1);
		
		scanNextPageIfFound(1);		
	}
	
	private int scanNextPageIfFound(int currentPage) throws Exception {
		if (contactList.size() >= maxCandidates) {
			return currentPage;
		}
		
		WebElement nextElement = null;
		try {
			nextElement = driver.findElement(By.className("pageProg"));
			nextElement.findElement(By.className("nextLink")).click();
		}
		catch (Exception ex) {
			return currentPage;
		}
		
		waitForSearchResultPage();		
		
		currentPage ++;
		System.out.println("Scanning page " + currentPage);
		
		scanCandidatesOnPage(currentPage);

		return scanNextPageIfFound(currentPage);
	}
	
	private void scanCandidatesOnPage(int currentPage) throws Exception {
		
		List<WebElement> candidateList = driver.findElements(By.className("candidateContent"));
		int numOfCandidatesOnPage = candidateList.size();
		int i = 0;
		
		int startIndexOnPage = candidateStart - candidateCounter;
		if (startIndexOnPage > 0) {
			if (startIndexOnPage >= numOfCandidatesOnPage) {
				candidateCounter += numOfCandidatesOnPage;
				return;
			}
			else {
				i = startIndexOnPage;
				candidateCounter += startIndexOnPage;
			}
		}
		
		while (i < numOfCandidatesOnPage) {
			if (contactList.size() >= maxCandidates) {
				return;
			}
			
			if (afterDate != null) {
				String dateModifiedStr = driver.findElement(By.className("padRow")).getText();
				Date dateModified = formatter.parse(dateModifiedStr);
				if (afterDate.after(dateModified)) {
					//do not scan the candidate list any more if the afterDate is after the candidate's dateModified.
					//if the afterDate is on the same day of the dataModified, the candidate will still be processed.
					return;
				}
			}
			
			WebElement candidate = candidateList.get(i);
				
			scanOneCandidate(candidate, i);
			
			candidateList = driver.findElements(By.className("candidateContent"));
			numOfCandidatesOnPage = candidateList.size();
			
			i++;
			candidateCounter++;
		}
	}
	
	private void scanOneCandidate(WebElement candidate, int i) throws Exception {
		i = i + 1;//xpath is 1 based
		
		if (skipViewed == true) {
			System.out.println("here1");
			WebElement notViewed = null;
			try {				
				notViewed = candidate.findElement(By.xpath("//div[@class='candidateContent'][" + i + "]//img[@src='/assets/images/eyeball_inactive.gif']"));
			}
			catch (Exception ex) {
				return;
			}
			
			if (notViewed != null) {
				try {
					//openResume(candidate.findElement(By.xpath("//div[@class='candidateContent'][" + i + "]//a[@class='candidateName']")));
					openProfile(candidate.findElement(By.xpath("//div[@class='candidateContent'][" + i + "]//a[@class='TMresHDname']")));
				}
				catch (Exception ex) {
					System.out.println("When exception happens, i=" + i);
					ex.printStackTrace();
				}
			}
		}
		else {
			try {
				//openResume(candidate.findElement(By.xpath("//div[@class='candidateContent'][" + i + "]//a[@class='candidateName']")));
				openProfile(candidate.findElement(By.xpath("//div[@class='candidateContent'][" + i + "]//a[@class='TMresHDname']")));
			}
			catch (Exception ex) {
				System.out.println("When exception happens, i=" + i);
				ex.printStackTrace();
			}
		}
	}
	
	private void openProfile(int i) throws Exception {		
		openProfile(driver.findElement(By.xpath("//div[@class='candidateContent'][" + i + "]//a[@class='candidateName']")));
	}
	
	private void closeProfilePopupIfNecessary() {
		try {
			WebElement okButton = driver.findElement(By.xpath("//div[@class='modal-footer']/button[@class='btn btn-primary']"));
			if (okButton != null && okButton.isDisplayed()) {
				okButton.click();
			}
		}
		catch (NoSuchElementException ignore) {
			//popup window is not displayed
		}
	}
	
	private void openProfile(WebElement webElement) throws IOException, EmailException {
		
		webElement.click();
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				WebElement nameElement = null;
				WebElement contactElement = null;
				try {
					nameElement = webDriver.findElement(By.id("tmCandidateName"));
				}
				catch (Exception ex) {
				}
				try {
					contactElement = webDriver.findElement(By.id("contactConfCandidate-button"));
				}
				catch (Exception ex) {
				}
				
				return nameElement != null || contactElement != null;
			}
		});
		
		closeProfilePopupIfNecessary();
				
		WebElement nameElement = null;
		WebElement contactElement = null;
		
		try {
			contactElement = driver.findElement(By.id("contactConfCandidate-button"));
			
			//Confidential profile
			if (sendEmail == true && skipConfidential == false) {
				contactElement.click();
				driver.findElement(By.id("email")).sendKeys("resumes@zevatechnology.com");
				String emailContent = emailSender.getEmailContent();
				driver.findElement(By.id("note")).sendKeys(emailContent);
				WebElement sendElement = null;
				try {
					sendElement = driver.findElement(By.id("send-button"));
					if (sendElement.isDisplayed() && sendElement.isEnabled()) {
						sendElement.click();
					}
				}
				catch (Exception ignore) {
				}
				wait.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver webDriver) {
						WebElement emailSentElement = webDriver.findElement(By.className("info"));
						return emailSentElement != null && emailSentElement.getText().equals("Contact seeker email has been sent.");
					}
				});
			}
			
			driver.findElement(By.id("closewindow2")).click();
		}
		catch (NoSuchElementException ignore2) {	
			//regular profile
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					WebElement connectElement = webDriver.findElement(By.id("contact_1"));
					try {
						connectElement.click();
						return true;
					}
					catch (Exception ex) {
						return false;
					}
				}
			});
			
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					WebElement emailElement = webDriver.findElement(By.id("jrEmailTo"));
					String email = emailElement.getText();
					if (email == null || email.equals("")) {
						return false;
					}
					else {
						return true;
					}
				}
			});
						
			String email = driver.findElement(By.id("jrEmailTo")).getText();
			email = email.substring(4);
			nameElement = driver.findElement(By.xpath("//div[@id='tmCandidateName']//h1"));
			String name = null;
			if (nameElement != null) {
				name = replaceDelimiter(nameElement.getText());
			}
			
			if (email != null) {
				email = replaceDelimiter(email);
				Contact contact = new Contact(name, email);
				contactList.add(contact);
				System.out.println(contact);
				
				sendEmailIfDesired(contact);
			}
			
			driver.findElement(By.xpath("//div[@class='OWSprofileHDutils']//a")).click();
		}

		
		waitForSearchResultPage();
	}
	
	private void openResumeOld(WebElement webElement) {
		webElement.click();
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.id("closewindow2")) != null;
			}
		});
		
		WebElement emailElement = null;
		WebElement nameElement = null;
		try {
			emailElement = driver.findElement(By.xpath("//div[@class='detailGroup1']//a"));
			if (emailElement != null) {
				String email = emailElement.getText();
				nameElement = driver.findElement(By.xpath("//div[@class='yui-u first']//h1[@class='candidateName']"));
				String name = null;
				if (nameElement != null) {
					name = replaceDelimiter(nameElement.getText());
				}
				
				if (email != null) {
					email = replaceDelimiter(email);
					Contact contact = new Contact(name, email);
					contactList.add(contact);
					System.out.println(contact);
					
					sendEmailIfDesired(contact);
				}
			}
		}
		catch (Exception ex) {
			if (sendEmail == true && skipConfidential == false) {
				driver.findElement(By.id("contactConfCandidate-button")).click();
				driver.findElement(By.id("email")).sendKeys("resumes@zevatechnology.com");
				String emailContent = emailSender.getEmailContent();
				driver.findElement(By.id("note")).sendKeys(emailContent);
				WebElement sendElement = null;
				try {
					sendElement = driver.findElement(By.id("send-button"));
					if (sendElement.isDisplayed() && sendElement.isEnabled()) {
						sendElement.click();
					}
				}
				catch (Exception ignore) {
				}
				wait.until(new ExpectedCondition<Boolean>() {
					public Boolean apply(WebDriver webDriver) {
						WebElement emailSentElement = webDriver.findElement(By.className("info"));
						return emailSentElement != null && emailSentElement.getText().equals("Contact seeker email has been sent.");
					}
				});
			}
		}

		driver.findElement(By.id("closewindow2")).click();
		waitForSearchResultPage();
	}

}
