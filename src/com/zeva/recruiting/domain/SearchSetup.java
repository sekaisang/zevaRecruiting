package com.zeva.recruiting.domain;

public class SearchSetup {
	
	private int userId;
    private int guid;
    private int max;
    private int candidateIndex;
    private String afterDate;
    private String searchAgent;
    private char skipViewed;
    private char sortByDate;
    private char sendEmail;
    private char skipNoRelocation;
    private String emailTemplate;
    private char activeFlag = 'Y';
    
    
    
	


	public SearchSetup() {
	
	}
	
	
	
	public SearchSetup(int userId, int max, int candidateIndex,
			String afterDate, String searchAgent, char skipViewed,
			char sortByDate, char sendEmail, char skipNoRelocation,
			String emailTemplate) {
		super();
		this.userId = userId;
		this.max = max;
		this.candidateIndex = candidateIndex;
		this.afterDate = afterDate;
		this.searchAgent = searchAgent;
		this.skipViewed = skipViewed;
		this.sortByDate = sortByDate;
		this.sendEmail = sendEmail;
		this.skipNoRelocation = skipNoRelocation;
		this.emailTemplate = emailTemplate;
	}



	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getGuid() {
		return guid;
	}
	public void setGuid(int guid) {
		this.guid = guid;
	}
	public int getMax() {
		return max;
	}
	public void setMax(int max) {
		this.max = max;
	}
	public int getCandidateIndex() {
		return candidateIndex;
	}
	public void setCandidateIndex(int candidateIndex) {
		this.candidateIndex = candidateIndex;
	}
	public String getAfterDate() {
		return afterDate;
	}
	public void setAfterDate(String afterDate) {
		this.afterDate = afterDate;
	}
	public String getSearchAgent() {
		return searchAgent;
	}
	public void setSearchAgent(String searchAgent) {
		this.searchAgent = searchAgent;
	}
	public char getSkipViewed() {
		return skipViewed;
	}
	public void setSkipViewed(char skipViewed) {
		this.skipViewed = skipViewed;
	}
	public char getSortByDate() {
		return sortByDate;
	}
	public void setSortByDate(char sortByDate) {
		this.sortByDate = sortByDate;
	}
	public char getSendEmail() {
		return sendEmail;
	}
	public void setSendEmail(char sendEmail) {
		this.sendEmail = sendEmail;
	}
	public char getSkipNoRelocation() {
		return skipNoRelocation;
	}
	public void setSkipNoRelocation(char skipNoRelocation) {
		this.skipNoRelocation = skipNoRelocation;
	}
	public String getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(String emailTemplate) {
		this.emailTemplate = emailTemplate;
	}
	public char getActiveFlag() {
		return activeFlag;
	}

	public void setActiveFlag(char activeFlag) {
		this.activeFlag = activeFlag;
	}

    
}
