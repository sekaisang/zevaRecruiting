package com.zevatech.staffing.webcrawler.monster;

import java.io.IOException;

import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;

import com.zevatech.staffing.exception.UnavailableException;
import com.zevatech.staffing.vo.Contact;
import com.zevatech.staffing.webcrawler.CandidateSearch;

public class MonsterSearch extends CandidateSearch {

	private static int SLEEP_TIME = 15000;
	
	private String prevResumeID = null;
	
	private SimpleDateFormat formatter = null;
	
	public static void main(String[] args) throws Exception {
		MonsterSearch monster = new MonsterSearch();
		monster.startChrome();
		//monster.startFirefox();
		monster.run();
	}
	
	public MonsterSearch() throws IOException, ParseException {
		super("monster");
		this.formatter = new SimpleDateFormat("M/d/yyyy");
	}
	
	protected void perform(OutputStream os) throws Exception {
		try {
			login();
			loadSearchAgent();
			sortByResumeDateIfRequired();
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
		driver.get("https://hiring.monster.com/Login.aspx?redirect=http%3a%2f%2fhiring.monster.com%2fdefault.aspx%3fHasUserAccount%3d2");		
		//driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_cphBody_LoginUser_txtUserName")).sendKeys("zevatech");
		driver.findElement(By.id("bx8uf3en")).sendKeys(userName);
		//driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_cphBody_LoginUser_txtPassWord")).sendKeys("zevahire123");
		driver.findElement(By.id("k84p12ow")).sendKeys(password);
		//driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_cphBody_LoginUser_SubmitImage")).click();
		driver.findElement(By.id("9a9swe1i")).click();
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_cphHomeBody_msgWelcomeBack")) != null;
			}
		});
	}
	
	private void logout() throws Exception {
		driver.findElement(By.id("ctl00_ctl00_cphHeader_navHeader_loginBar_lnkLogout")).click();
	}
	
	private void loadSearchAgent() throws Exception {
		driver.get("http://hiring.monster.com/jcm/resumesearch/listagents.aspx");
		wait.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.linkText(searchAgent)) != null;
			}
		});
		
		driver.findElement(By.linkText(searchAgent)).click();
		waitForSearchResultPage();
	}
	
	private void waitForSearchResultPage() {
		forever.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				return webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_controlResumeBottom_footerCandidates")) != null;
			}
		});
	}
	
	private void waitForSearchResultPage(final int currentPage) throws Exception {
		forever.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				String currPageStr = webDriver.findElement(By.className("currPage")).getText();
				return Integer.parseInt(currPageStr) == currentPage;
			}
		});
		
		waitForSearchResultPage();
		
		Thread.sleep(SLEEP_TIME);
	}
	
	private void waitForResumePage() {
		forever.until(new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver webDriver) {
				WebElement emailElement = null;
				try {
					emailElement = webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlDetailTop_linkEmail"));
				}
				catch (Exception ignore) {
				}
				return emailElement != null && emailElement.getAttribute("href") != null;
			}
		});
	}
	
	private void sortByResumeDateIfRequired() throws Exception {
		if (sortByDate == true) {
			WebElement selectElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl01_ctl05_Arrow"));
			selectElement.click();
			Thread.sleep(SLEEP_TIME);
			
			WebElement divElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl01_ctl05_DropDown"));
			List<WebElement> sortByElements = divElement.findElements(By.tagName("li"));
			WebElement sortByElement = sortByElements.get(2); //assume the 3rd option is "Resume Updated"
			sortByElement.click();

			Thread.sleep(SLEEP_TIME);
			//driver.findElement(By.className("completeSortButton")).click();
			//Thread.sleep(5000);
			
			if (ascending == true) {
				WebElement selectAscElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl01_ctl06_Arrow"));
				selectAscElement.click();
				Thread.sleep(SLEEP_TIME);
				
				WebElement ascDivElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_ctl01_ctl06_DropDown"));
				List<WebElement> ascElements = ascDivElement.findElements(By.tagName("li"));
				WebElement ascElement = ascElements.get(0); //assume the 1st option is "Ascending"
				ascElement.click();
				
				WebElement aElement = driver.findElement(By.xpath("//a[@class='completeSortButton']"));
				aElement.click();
				Thread.sleep(SLEEP_TIME);
			}

			System.out.println("Resumes are sorted by date");
		}
	}
	
	private int scanNextPageIfFoundLoop(int currentPage) throws Exception {
		int nextPage = 0;
		while (true) {
			nextPage = scanNextPageIfFound(currentPage);
			if (nextPage > currentPage) {
				currentPage = nextPage;
			}
			else {
				break;
			}
		}
		
		return nextPage;
	}
	
	private void scanCandidates() throws Exception {
		System.out.println("Scanning page 1");
		
		scanCandidatesOnPage();
		
		int lastPage = scanNextPageIfFoundLoop(1);		
		
		scanNextPageGroupIfFound(lastPage);
		
		/*
		for (int pageNum = 1; pageNum <= numOfPages; pageNum++) {
			driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_CndTop_pagingHeader_currPage")).sendKeys(pageNum + "");
			driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_CndTop_pagingHeader_pagerGoBtn")).click();
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					return webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_controlResumeBottom_pagingFooter_pagerGoBtn")) != null;
				}
			});
			
			if (emailList.size() >= MAX_CANDIDATES) {
				return;
			}
			
			scanCandidatesOnPage();
		}*/
	}
	
	private int scanNextPageIfFound(int currentPage) throws Exception {
		List<WebElement> pageElementList = driver.findElements(By.className("page"));
		if (pageElementList != null) {
			for (WebElement pageElement : pageElementList) {
				if (contactList.size() >= maxCandidates) {
					break;
				}
				
				int page = Integer.parseInt(pageElement.getText().trim());
				if (page > currentPage) {
					currentPage = page;
					
					pageElement.click();
					waitForSearchResultPage(currentPage);
					
					System.out.println("Scanning page " + currentPage);
					
					scanCandidatesOnPage();
					
					break;
				}
			}	
		}
		
		return currentPage;
	}
	
	private void scanNextPageGroupIfFound(int lastPage) throws Exception {
		WebElement pgrHeaderElement = driver.findElement(By.id("pgrHeader")); 
		if (pgrHeaderElement == null) {
			return;
		}
		
		String pgrHeader = pgrHeaderElement.getText();
		if (pgrHeader == null) {
			return;
		}
		
		//String numOfPagesStr = pgrHeader.substring("Browse Candidates (".length(), pgrHeader.indexOf("Page")).trim();
		//int numOfPages = Integer.parseInt(numOfPagesStr);
		
		WebElement nextElement = null;
		try {
			nextElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_CndTop_pagingHeader_pagerNext"));
		}
		catch (Exception ex) {
			return;
		}
		
		if (nextElement == null) {
			return;
		}
			
		if (nextElement.isDisplayed() == true) {
			nextElement.click();
			waitForSearchResultPage();
			
			System.out.println("Scanning page " + (lastPage + 1));
			
			scanCandidatesOnPage();
			
			int currentPage = 0;
			try {
				currentPage = Integer.parseInt(driver.findElement(By.className("currPage")).getText().trim());
			}
			catch (Exception ex) {
				//set the current page to 5 when the currPage element is not found (Monster bug)
				currentPage = 5;
			}
			scanNextPageIfFoundLoop(currentPage);
		}
	}
	
	private void scanCandidatesOnPage() throws Exception {
		Thread.sleep(5000);
		
		List<WebElement> candidateList = driver.findElements(By.className("row"));
		//List<WebElement> candidateList = driver.findElements(By.className("geColResumeTitle"));
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
		
		for (; i < numOfCandidatesOnPage; i ++) {
			if (contactList.size() >= maxCandidates) {
				return;
			}
			
			WebElement candidate = candidateList.get(i);
			
			try {
				if (afterDate != null) {
					String dateModifiedStr = candidate.findElement(By.id("dateModified")).getText();
					Date dateModified = formatter.parse(dateModifiedStr);
					if (afterDate.after(dateModified)) {
						//do not scan the candidate list any more if the afterDate is after the candidate's dateModified.
						//if the afterDate is on the same day of the dataModified, the candidate will still be processed.
						return;
					}
				}
				
				scanOneCandidate(candidate);
			}
			catch (Exception ex) {
				if (ex instanceof UnavailableException) {
					System.err.println(ex.getMessage());
				}
				else {
					System.err.println("Element Not Found");
					ex.printStackTrace();
				}
				
				WebElement backToSearchResult = null;
				try {
					//try to click the back to search result link on the page when a resume is removed from database
					backToSearchResult = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_backLink"));
					backToSearchResult.click();
				}
				catch (Exception exx) {
				}
				
				waitForSearchResultPage();
				sortByResumeDateIfRequired();
				
				//reload the candidate list
				//candidateList = driver.findElements(By.className("geColResumeTitle"));
				candidateList = driver.findElements(By.className("row"));
				continue;
			}
			
		}
	}
	
	private void scanOneCandidate(WebElement candidateElement) throws Exception {
		if (checkSkipViewedFlag(candidateElement) && checkSkipNoRelocation(candidateElement)) {
			openResume(candidateElement.findElement(By.id("linkResumeTitle")));
		}
	}
	
	/**
	 * 
	 * @param candidateElement
	 * @return true when
	 * - candidate has not been viewed if the skipViewed flag is true
	 * or 
	 * - skipViewed flag is false 
	 * @throws Exception
	 */
	private boolean checkSkipViewedFlag(WebElement candidateElement) throws Exception {
		if (skipViewed == false) {
			return true;
		}
		
		WebElement viewedElement = candidateElement.findElement(By.id("ViewedLabel"));
		
		if (viewedElement.getAttribute("class") == null) {
			System.err.println("Unable to read ViewedLabel class value");
			return true;
		}
		
		String viewed = viewedElement.getText();
		if (viewed != null && viewed.trim().equals("Viewed")) {
			System.out.println("Skip Viewed: " + candidateElement.findElement(By.id("linkResumeTitle")).getText()); 
			return false;
		}
		
		return true;
	}
	
	private boolean checkSkipNoRelocation(WebElement candidateElement) throws Exception {
		if (skipNoRelocation == false) {
			return true;
		}
		
		String relocation = candidateElement.findElement(By.id("expRelocate")).getText();
		if (relocation != null && relocation.equals("Won't Relocate")) {
			System.out.println("Skip Won't Relocate: " + candidateElement.findElement(By.id("linkResumeTitle")).getText()); 
			return false;
		}
		
		return true;
	}
	
	private void openResume(int i) throws Exception {
		openResume(driver.findElement(By.xpath("//tr[contains(@gridrowindex, '" + i + "')]//a[@id='linkView']")));
	}
	
	private void openResume(WebElement linkResumeTitleElement) throws Exception {
		if (linkResumeTitleElement.isDisplayed() == false) {
			return;
		}
		
		//String resumeTitle = linkResumeTitleElement.getText();
		//final String candidateTitle = resumeTitle.substring(resumeTitle.indexOf("�") + "�".length()).trim();
		
		linkResumeTitleElement.click();
		try {
			wait.until(new ExpectedCondition<Boolean>() {
				public Boolean apply(WebDriver webDriver) {
					//resume detail is present
					//WebElement titleElement = null;
					WebElement resumeIDElement = null;
					//resume is unavailable
					WebElement unavailableElement = null;
					//image check
					WebElement imageCheck = null;
					try {
						//titleElement = webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlResumeTab_lblTitle"));
						resumeIDElement = webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlResumeTab_lblResumeIdCrs"));
					}
					catch (Exception ignore) {
					}
					try {
						unavailableElement = webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_backLink"));
					}
					catch (Exception ignore) {
					}
					try {
						imageCheck = webDriver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctlCaptcha_linkNonReadableCaptcha"));
					}
					catch (Exception ignore) {
					}
					//return (emailElement != null && emailElement.getAttribute("href") != null) || unavailableElement != null || imageCheck != null;
					//return (titleElement != null && titleElement.getText() != null && titleElement.getText().trim().equals(candidateTitle))
					return (resumeIDElement != null && resumeIDElement.getText() != null && !resumeIDElement.getText().trim().equals("") && !resumeIDElement.getText().trim().equals(prevResumeID))
						|| unavailableElement != null || imageCheck != null;
				}
			});
		}
		catch (Throwable ignore) {
			
		}
		
		WebElement backToSearchResult = null;
		try {
			backToSearchResult = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_backLink"));
		}
		catch (Exception ignore) {
		}
		if (backToSearchResult != null) {
			throw new UnavailableException("Resume is unavailable");
		}
		
		WebElement imageCheck = null;
		try {
			imageCheck = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolder1_ctlCaptcha_linkNonReadableCaptcha"));
		}
		catch (Exception ignore) {
		}
		if (imageCheck != null) {
			waitForResumePage();
		}
		
		WebElement emailElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlDetailTop_linkEmail"));
		if (emailElement != null) {
			String email = emailElement.getAttribute("href");
			if (email != null) {
				email = email.substring(7);
				email = replaceDelimiter(email);
			}
			
			WebElement nameElement = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlDetailTop_lbName"));
			String name = null;
			if (nameElement != null) {
				name = replaceDelimiter(nameElement.getText());
				if (name.trim().equals("")) {
					name = null;
				}
			}
			
			Contact contact = new Contact(name, email);
			contactList.add(contact);
			System.out.println(contact);
			
			sendEmailIfDesired(contact);
		}

		candidateCounter++;
		
		prevResumeID = driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderRight_lld_Detail_controlResumeTab_lblResumeIdCrs")).getText();
		
		driver.findElement(By.id("ctl00_ctl00_ContentPlaceHolderBase_ContentPlaceHolderLeft_msgBackToSearch")).click();
		waitForSearchResultPage();
	}
	
}
