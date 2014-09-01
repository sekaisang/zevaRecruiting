package com.zeva.recruiting.service;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import com.zeva.recruiting.domain.DiceProfile;
import com.zeva.recruiting.domain.DiceSearchSetup;
import com.zeva.recruiting.domain.MonsterProfile;
import com.zeva.recruiting.domain.MonsterSearchSetup;
import com.zeva.recruiting.domain.SearchSetup;
import com.zeva.recruiting.dto.SearchFormDTO;


@Path("/inactivate")
public class InactivateAgentService {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public  List<SearchSetup> getAgents(SearchFormDTO setup) {
		
		   
		       //create session 
				Configuration cfg = new Configuration().configure("hibernate/hibernate.cfg.xml");
				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
				SessionFactory sf = cfg.buildSessionFactory(serviceRegistry);
		        Session session = sf.openSession();
		        session.beginTransaction();
	           
		        
		        if(setup.getCompany().equals("dice")){ //it's a dice profile
		        //get the userId	
		        Criteria criteria = session.createCriteria(DiceProfile.class).
	      				add(Restrictions.eq("diceUsername", setup.getUsername())).
	      				add(Restrictions.eq("dicePassword", setup.getPassword()));  
				List<DiceProfile> profileList = (List<DiceProfile>) criteria.list();				
				int userId = profileList.get(0).getUserId();
				
				//Update the record by changing activeFlag
				Criteria criteriaForSameSearchAgent = session.createCriteria(DiceSearchSetup.class).
		  				add(Restrictions.eq("searchAgent", setup.getAgent())).
		  				add(Restrictions.eq("activeFlag", 'Y')).
		  				add((Restrictions.eq("userId", userId)));
		        List<DiceSearchSetup> sameAgentList =  (List<DiceSearchSetup>) criteriaForSameSearchAgent.list();
				//HQL for updating
		        String hql = "update from DiceSearchSetup "  +
       	             "set activeFlag = :active_flag " +
                        "where userId = :user_id " +
       			     "and searchAgent = :search_agent ";
		        inactivateRecord(sameAgentList, setup, session, userId, hql);
		        
				//re-get all the agents
				Criteria criteria2 = session.createCriteria(DiceSearchSetup.class).
	      				add(Restrictions.eq("userId", userId)).
	      				add(Restrictions.eq("activeFlag", 'Y'));
				
				List<DiceSearchSetup> diceSearchSetupList = (List<DiceSearchSetup>) criteria2.list();
				
				List<SearchSetup> searchSetupList = new ArrayList<SearchSetup>();
				for (int i = 0; i < diceSearchSetupList.size();i++)
				{
					searchSetupList.add(diceSearchSetupList.get(i));	
				}
				
				session.getTransaction().commit();
			    session.close();
	            return searchSetupList;}
		        
		        
		        else if(setup.getCompany().equals("monster")){
		        	//get the userId	
			        Criteria criteria = session.createCriteria(MonsterProfile.class).
		      				add(Restrictions.eq("monsterUsername", setup.getUsername())).
		      				add(Restrictions.eq("monsterPassword", setup.getPassword()));			        
					List<MonsterProfile> profileList = (List<MonsterProfile>) criteria.list();					
					int userId = profileList.get(0).getUserId();
					
					//Update the record by changing activeFlag
					Criteria criteriaForSameSearchAgent = session.createCriteria(MonsterSearchSetup.class).
			  				add(Restrictions.eq("searchAgent", setup.getAgent())).
			  				add(Restrictions.eq("activeFlag", 'Y')).
			  				add((Restrictions.eq("userId", userId)));
			        List<MonsterSearchSetup> sameAgentList =  (List<MonsterSearchSetup>) criteriaForSameSearchAgent.list();
			      //HQL for updating
			        String hql = "update from MonsterSearchSetup "  +
	        	             "set activeFlag = :active_flag " +
	                         "where userId = :user_id " +
	        			     "and searchAgent = :search_agent ";
			        inactivateRecord(sameAgentList, setup, session, userId,hql);
					
			      //re-get all the agents
					Criteria criteria2 = session.createCriteria(MonsterSearchSetup.class).
		      				add(Restrictions.eq("userId", userId)).
		      				add(Restrictions.eq("activeFlag", 'Y'));
					
					List<MonsterSearchSetup> monsterSearchSetupList = (List<MonsterSearchSetup>) criteria2.list();
					
					List<SearchSetup> searchSetupList = new ArrayList<SearchSetup>();
					for (int i = 0; i < monsterSearchSetupList.size();i++)
					{
						searchSetupList.add(monsterSearchSetupList.get(i));	
					}
					session.getTransaction().commit();
				    session.close();
		            return searchSetupList;}
		        else 
		        	return null;
	            	
	            }
	
	
    private void inactivateRecord(List sameAgentList, SearchFormDTO setup, Session session, int userId, String hql){
    	if (sameAgentList.size() > 0){
        	String sameAgent = setup.getAgent();
        	char inactive = 'N';  
            Query query = session.createQuery(hql);
            query.setParameter("user_id", userId);
            query.setParameter("search_agent", sameAgent);
            query.setParameter("active_flag", inactive);
            int result = query.executeUpdate();
            System.out.println("Rows affected: " + result);
        }//check if there is a record with same agent and is active, if there is , update that one
    }
	

}
