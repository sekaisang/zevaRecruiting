package com.zeva.recruiting.domain;

public class MonsterSearchSetup extends SearchSetup{
	
    
    private String monsterUsername;
    
    
    public MonsterSearchSetup(){};//default constructor
    
    public MonsterSearchSetup( int userId, String monsterUsername, int max,int candidateIndex, String afterDate, String searchAgent,
    		 char skipViewed, char sortByDate, char sendEmail, char skipNoRelocation, String emailTemplate){
        super(userId, max, candidateIndex,afterDate,searchAgent, skipViewed, sortByDate, sendEmail, skipNoRelocation,emailTemplate);
        this.monsterUsername = monsterUsername;
    	
    }//constructor
    
    //all the getters and setters
    
	
 
	public String getMonsterUsername() {
		return monsterUsername;
	}
	public void setMonsterUsername(String username) {
		this.monsterUsername= username;
	}
	
	public SearchSetup toSearchSetup(MonsterSearchSetup monsterSearchSetup)
	{   SearchSetup searchSetup = new SearchSetup(monsterSearchSetup.getUserId(),
			                                      monsterSearchSetup.getMax(),
			                                      monsterSearchSetup.getCandidateIndex(),
			                                      monsterSearchSetup.getAfterDate(),
			                                      monsterSearchSetup.getSearchAgent(),
			                                      monsterSearchSetup.getSkipViewed(),
			                                      monsterSearchSetup.getSortByDate(),
			                                      monsterSearchSetup.getSendEmail(),
			                                      monsterSearchSetup.getSkipNoRelocation(),
			                                      monsterSearchSetup.getEmailTemplate());
		return searchSetup;
	}
	
    

}




