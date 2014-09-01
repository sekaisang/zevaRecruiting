package com.zeva.recruiting.dto;



public class SearchFormDTO {
	
	private String company;
	private String username;
    private String password;
    private int maxCandidates;
    private int candidateIndex;
    private String afterDate;
    private String agent;
    private char skipViewed;
    private char sortByDate;
    private char sendEmail;
    private char skipNoRelocation;
    
    private String emailTemplate;
    private String emailSubject;
	private String fromPerson;
    private String fromEmail;
    
    public SearchFormDTO(){};//default constructor
    
    public SearchFormDTO(String company, String username,String password, int maxCandidates,int candidateIndex, String afterDate, String agent,
    		 char skipViewed, char sortByDate, char sendEmail, char skipNoRelocation, String emailTemplate){
    	this.company= company;
    	this.username = username;
    	this.password = password;
    	this.maxCandidates = maxCandidates;
    	this.candidateIndex= candidateIndex;
    	this.afterDate = afterDate;
    	this.agent= agent;
    	this.skipViewed=skipViewed;
    	this.sortByDate=sortByDate;
    	this.sendEmail=sendEmail;
    	this.skipNoRelocation=skipNoRelocation;
    	this.emailTemplate= emailTemplate;
    }//constructor
    
    //all the getters and setters
    public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getMaxCandidates() {
		return maxCandidates;
	}
	public void setMaxCandidates(int maxCandidates) {
		this.maxCandidates = maxCandidates;
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
	public String getAgent() {
		return agent;
	}
	public void setAgent(String agent) {
		this.agent = agent;
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
	public String getEmailTemplate(){
		return emailTemplate;
	}
	public void setEmailTemplate(String emailTemplate){
		this.emailTemplate=emailTemplate;
	}
	public String getEmailSubject() {
		return emailSubject;
	}

	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}

	public String getFromPerson() {
		return fromPerson;
	}

	public void setFromPerson(String fromPerson) {
		this.fromPerson = fromPerson;
	}

	public String getFromEmail() {
		return fromEmail;
	}

	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}

}
