package com.zeva.recruiting.dto;

public class DefaultProfile {
   
	private int userId;
	private String diceUsername;
    private String dicePassword;
    private int diceMaxCandidates;
    private int diceCandidateIndex;
    private String diceAfterDate;
    private String diceAgent;
    private char diceSkipViewed;
    private char diceSortByDate;
    private char diceSendEmail;
    private char diceSkipNoRelocation;
    private String diceEmailTemplate;
    private String monsterUsername;
    private String monsterPassword;
    private int monsterMaxCandidates;
    private int monsterCandidateIndex;
    private String monsterAfterDate;
    private String monsterAgent;
    private char monsterSkipViewed;
    private char monsterSortByDate;
    private char monsterSendEmail;
    private char monsterSkipNoRelocation;
    private String monsterEmailTemplate;
    
    public DefaultProfile(){};
    
    
    public DefaultProfile(int userId, String diceUsername, String dicePassword,
			int diceMaxCandidates, int diceCandidateIndex,
			String diceAfterDate, String diceAgent, char diceSkipViewed,
			char diceSortByDate, char diceSendEmail, char diceSkipNoRelocation,
			String diceEmailTemplate, String monsterUsername,
			String monsterPassword, int monsterMaxCandidates,
			int monsterCandidateIndex, String monsterAfterDate,
			String monsterAgent, char monsterSkipViewed,
			char monsterSortByDate, char monsterSendEmail,
			char monsterSkipNoRelocation, String monsterEmailTemplate) {
		this.userId = userId;
		this.diceUsername = diceUsername;
		this.dicePassword = dicePassword;
		this.diceMaxCandidates = diceMaxCandidates;
		this.diceCandidateIndex = diceCandidateIndex;
		this.diceAfterDate = diceAfterDate;
		this.diceAgent = diceAgent;
		this.diceSkipViewed = diceSkipViewed;
		this.diceSortByDate = diceSortByDate;
		this.diceSendEmail = diceSendEmail;
		this.diceSkipNoRelocation = diceSkipNoRelocation;
		this.diceEmailTemplate = diceEmailTemplate;
		this.monsterUsername = monsterUsername;
		this.monsterPassword = monsterPassword;
		this.monsterMaxCandidates = monsterMaxCandidates;
		this.monsterCandidateIndex = monsterCandidateIndex;
		this.monsterAfterDate = monsterAfterDate;
		this.monsterAgent = monsterAgent;
		this.monsterSkipViewed = monsterSkipViewed;
		this.monsterSortByDate = monsterSortByDate;
		this.monsterSendEmail = monsterSendEmail;
		this.monsterSkipNoRelocation = monsterSkipNoRelocation;
		this.monsterEmailTemplate = monsterEmailTemplate;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getDiceUsername() {
		return diceUsername;
	}
	public void setDiceUsername(String diceUsername) {
		this.diceUsername = diceUsername;
	}
	public String getDicePassword() {
		return dicePassword;
	}
	public void setDicePassword(String dicePassword) {
		this.dicePassword = dicePassword;
	}
	public int getDiceMaxCandidates() {
		return diceMaxCandidates;
	}
	public void setDiceMaxCandidates(int diceMaxCandidates) {
		this.diceMaxCandidates = diceMaxCandidates;
	}
	public int getDiceCandidateIndex() {
		return diceCandidateIndex;
	}
	public void setDiceCandidateIndex(int diceCandidateIndex) {
		this.diceCandidateIndex = diceCandidateIndex;
	}
	public String getDiceAfterDate() {
		return diceAfterDate;
	}
	public void setDiceAfterDate(String diceAfterDate) {
		this.diceAfterDate = diceAfterDate;
	}
	public String getDiceAgent() {
		return diceAgent;
	}
	public void setDiceAgent(String diceAgent) {
		this.diceAgent = diceAgent;
	}
	public char getDiceSkipViewed() {
		return diceSkipViewed;
	}
	public void setDiceSkipViewed(char diceSkipViewed) {
		this.diceSkipViewed = diceSkipViewed;
	}
	public char getDiceSortByDate() {
		return diceSortByDate;
	}
	public void setDiceSortByDate(char diceSortByDate) {
		this.diceSortByDate = diceSortByDate;
	}
	public char getDiceSendEmail() {
		return diceSendEmail;
	}
	public void setDiceSendEmail(char diceSendEmail) {
		this.diceSendEmail = diceSendEmail;
	}
	public char getDiceSkipNoRelocation() {
		return diceSkipNoRelocation;
	}
	public void setDiceSkipNoRelocation(char diceSkipNoRelocation) {
		this.diceSkipNoRelocation = diceSkipNoRelocation;
	}
	public String getDiceEmailTemplate() {
		return diceEmailTemplate;
	}
	public void setDiceEmailTemplate(String diceEmailTemplate) {
		this.diceEmailTemplate = diceEmailTemplate;
	}
	public String getMonsterUsername() {
		return monsterUsername;
	}
	public void setMonsterUsername(String monsterUsername) {
		this.monsterUsername = monsterUsername;
	}
	public String getMonsterPassword() {
		return monsterPassword;
	}
	public void setMonsterPassword(String monsterPassword) {
		this.monsterPassword = monsterPassword;
	}
	public int getMonsterMaxCandidates() {
		return monsterMaxCandidates;
	}
	public void setMonsterMaxCandidates(int monsterMaxCandidates) {
		this.monsterMaxCandidates = monsterMaxCandidates;
	}
	public int getMonsterCandidateIndex() {
		return monsterCandidateIndex;
	}
	public void setMonsterCandidateIndex(int monsterCandidateIndex) {
		this.monsterCandidateIndex = monsterCandidateIndex;
	}
	public String getMonsterAfterDate() {
		return monsterAfterDate;
	}
	public void setMonsterAfterDate(String monsterAfterDate) {
		this.monsterAfterDate = monsterAfterDate;
	}
	public String getMonsterAgent() {
		return monsterAgent;
	}
	public void setMonsterAgent(String monsterAgent) {
		this.monsterAgent = monsterAgent;
	}
	public char getMonsterSkipViewed() {
		return monsterSkipViewed;
	}
	public void setMonsterSkipViewed(char monsterSkipViewed) {
		this.monsterSkipViewed = monsterSkipViewed;
	}
	public char getMonsterSortByDate() {
		return monsterSortByDate;
	}
	public void setMonsterSortByDate(char monsterSortByDate) {
		this.monsterSortByDate = monsterSortByDate;
	}
	public char getMonsterSendEmail() {
		return monsterSendEmail;
	}
	public void setMonsterSendEmail(char monsterSendEmail) {
		this.monsterSendEmail = monsterSendEmail;
	}
	public char getMonsterSkipNoRelocation() {
		return monsterSkipNoRelocation;
	}
	public void setMonsterSkipNoRelocation(char monsterSkipNoRelocation) {
		this.monsterSkipNoRelocation = monsterSkipNoRelocation;
	}
	public String getMonsterEmailTemplate() {
		return monsterEmailTemplate;
	}
	public void setMonsterEmailTemplate(String monsterEmailTemplate) {
		this.monsterEmailTemplate = monsterEmailTemplate;
	}
	
}
