package com.zeva.recruiting.domain;

public class DiceSearchSetup extends SearchSetup {
	
 
    private String diceUsername;
 
    
    public DiceSearchSetup(){};//default constructor
    
    public DiceSearchSetup( int userId, String diceUsername, int max,int candidateIndex, String afterDate, String searchAgent,
    		 char skipViewed, char sortByDate, char sendEmail, char skipNoRelocation, String emailTemplate){
    	super(userId, max, candidateIndex,afterDate,searchAgent, skipViewed, sortByDate, sendEmail, skipNoRelocation,emailTemplate);
        this.diceUsername = diceUsername;
    
    }//constructor
    
    //all the getters and setters
    
	
    
	public String getDiceUsername() {
		return diceUsername;
	}
	public void setDiceUsername(String username) {
		this.diceUsername = username;
	}
	
	public SearchSetup toSearchSetup(DiceSearchSetup diceSearchSetup)
	{   SearchSetup searchSetup = new SearchSetup(diceSearchSetup.getUserId(),
			diceSearchSetup.getMax(),
			diceSearchSetup.getCandidateIndex(),
			diceSearchSetup.getAfterDate(),
			diceSearchSetup.getSearchAgent(),
			diceSearchSetup.getSkipViewed(),
			diceSearchSetup.getSortByDate(),
			diceSearchSetup.getSendEmail(),
			diceSearchSetup.getSkipNoRelocation(),
			diceSearchSetup.getEmailTemplate());
		return searchSetup;
	}
	
}



