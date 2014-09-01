package com.zeva.recruiting.service;




import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.hibernate.service.ServiceRegistry;

import com.zeva.recruiting.domain.DiceProfile;
import com.zeva.recruiting.domain.MonsterProfile;
import com.zeva.recruiting.domain.UserCredential;
import com.zeva.recruiting.dto.DefaultProfile;
import com.zeva.recruiting.dto.UserCredentialDTO;



@Path("/auth")
public class AuthenticationService {
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON) 
	@Produces(MediaType.APPLICATION_JSON)
	public DefaultProfile check(UserCredentialDTO user) {
		
	    //create session from database
		Configuration cfg = new Configuration().configure("hibernate/hibernate.cfg.xml");
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(cfg.getProperties()).build();
		SessionFactory sf = cfg.buildSessionFactory(serviceRegistry);
        Session session = sf.openSession();
       
		//set criteria to check if there is a valid user credential
		Criteria criteria = session.createCriteria(UserCredential.class).
				add(Restrictions.eq("username", user.getName())).
				add(Restrictions.eq("password", user.getPassword()));
		
		//store valid user credential into a list
		List<UserCredential> userList = (List<UserCredential>) criteria.list();
	
		if (userList.size()>0)
		 { int guid = userList.get(0).getGuid(); //get the guid of the user_credential 
		   //make a criteria which uses guid of the user_credential as a parameter
		   Criteria criteria2 = session.createCriteria(DiceProfile.class).
					add(Restrictions.eq("userId", guid));
		   Criteria criteria3 = session.createCriteria(MonsterProfile.class).
					add(Restrictions.eq("userId", guid));
		   List<DiceProfile> diceProfileList = (List<DiceProfile>) criteria2.list();
		   List<MonsterProfile> monsterProfileList = (List<MonsterProfile>) criteria3.list();
		   //get the profile(the first item in the list)
		   DiceProfile diceProfile = new DiceProfile();
		   MonsterProfile monsterProfile = new MonsterProfile();
		   if (diceProfileList.size()!= 0){
			   diceProfile = diceProfileList.get(0);
			   }		   
		   if (monsterProfileList.size()!= 0){
			   monsterProfile = monsterProfileList.get(0);
			  }
	    
		   DefaultProfile defaultProfile = new DefaultProfile(
				                guid,
				                diceProfile.getDiceUsername(),
				                diceProfile.getDicePassword(),
				                diceProfile.getMax(),
	                            diceProfile.getCandidateIndex(),
	                            diceProfile.getAfterDate(),
	                            diceProfile.getSearchAgent(),
	                            diceProfile.getSkipViewed(),
	                            diceProfile.getSortByDate(),
	                            diceProfile.getSendEmail(),
	                            diceProfile.getSkipNoRelocation(),
	                            diceProfile.getEmailTemplate(),
	                            monsterProfile.getMonsterUsername(),
	                            monsterProfile.getMonsterPassword(),
	                            monsterProfile.getMax(),
	                            monsterProfile.getCandidateIndex(),
	                            monsterProfile.getAfterDate(),
	                            monsterProfile.getSearchAgent(),
	                            monsterProfile.getSkipViewed(),
	                            monsterProfile.getSortByDate(),
	                            monsterProfile.getSendEmail(),
	                            monsterProfile.getSkipNoRelocation(),
	                            monsterProfile.getEmailTemplate()
				                );
		   
	       session.close();
		   return defaultProfile; }//is valid input and return the default value of home page
		   else 
		     {//close the session
			  session.close();
			  return null; }//invalid input
		   }
	   	}