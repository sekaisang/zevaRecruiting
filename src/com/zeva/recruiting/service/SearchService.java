package com.zeva.recruiting.service;

import java.io.BufferedReader;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import com.zeva.recruiting.domain.MonsterProfile;
import com.zeva.recruiting.domain.DiceSearchSetup;
import com.zeva.recruiting.domain.MonsterSearchSetup;
import com.zeva.recruiting.dto.SearchFormDTO;
import com.zevatech.staffing.webcrawler.CandidateSearch;
import com.zevatech.staffing.webcrawler.IConstants;
import com.zevatech.staffing.webcrawler.dice.DiceSearch;
import com.zevatech.staffing.webcrawler.monster.MonsterSearch;

@Path("/send")
public class SearchService implements IConstants {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String sendData(SearchFormDTO searchForm) throws Exception {

		String outputString = null;

		// create session
		Configuration cfg = new Configuration()
				.configure("hibernate/hibernate.cfg.xml");
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(cfg.getProperties()).build();
		SessionFactory sf = cfg.buildSessionFactory(serviceRegistry);
		Session session = sf.openSession();
		session.beginTransaction();

		if (searchForm.getCompany().equals("dice")) { // It's a dice searchForm and it
													// will be stored in
													// dice_search_setup table

			// get the user's userId
			Criteria criteria = session.createCriteria(DiceProfile.class)
					.add(Restrictions.eq("diceUsername", searchForm.getUsername()))
					.add(Restrictions.eq("dicePassword", searchForm.getPassword()));
			List<DiceProfile> profileList = (List<DiceProfile>) criteria.list();
			int userId = profileList.get(0).getUserId();

			// create a record which will be stored in the database later
			DiceSearchSetup diceSearchSetup = new DiceSearchSetup(userId,
					searchForm.getUsername(), searchForm.getMaxCandidates(),
					searchForm.getCandidateIndex(), searchForm.getAfterDate(),
					searchForm.getAgent(), searchForm.getSkipViewed(),
					searchForm.getSortByDate(), searchForm.getSendEmail(),
					searchForm.getSkipNoRelocation(), searchForm.getEmailTemplate());

			// if there is a active record with same agent name,delete it
			Criteria criteriaForSameSearchAgent = session
					.createCriteria(DiceSearchSetup.class)
					.add(Restrictions.eq("searchAgent", searchForm.getAgent()))
					.add(Restrictions.eq("activeFlag", 'Y'))
					.add((Restrictions.eq("userId", userId)));
			List<DiceSearchSetup> sameAgentList = (List<DiceSearchSetup>) criteriaForSameSearchAgent
					.list();
			// HQL for deleting the record
			String hql = "delete from DiceSearchSetup "
					+ "where userId = :user_id "
					+ "and searchAgent = :search_agent "
					+ "and activeFlag = :active_flag";
			deleteSameAgent(sameAgentList, searchForm, session, userId, hql);

			// store the search searchForm into database
			session.save(diceSearchSetup);
			session.getTransaction().commit();
			session.close();

			// start the search
			DiceSearch dice = new DiceSearch();

			outputString = performSearch(dice, searchForm);

		}

		else if (searchForm.getCompany().equals("monster")) { // its a monster
															// searchForm

			// get the userId
			Criteria criteria = session
					.createCriteria(MonsterProfile.class)
					.add(Restrictions.eq("monsterUsername", searchForm.getUsername()))
					.add(Restrictions.eq("monsterPassword", searchForm.getPassword()));
			List<MonsterProfile> profileList = (List<MonsterProfile>) criteria
					.list();
			int userId = profileList.get(0).getUserId();

			// create a record which will be stored in the database later
			MonsterSearchSetup monsterSearchSetup = new MonsterSearchSetup(
					userId, searchForm.getUsername(), searchForm.getMaxCandidates(),
					searchForm.getCandidateIndex(), searchForm.getAfterDate(),
					searchForm.getAgent(), searchForm.getSkipViewed(),
					searchForm.getSortByDate(), searchForm.getSendEmail(),
					searchForm.getSkipNoRelocation(), searchForm.getEmailTemplate());

			// if there is a active record with same agent name, delete it
			Criteria criteriaForSameSearchAgent = session
					.createCriteria(MonsterSearchSetup.class)
					.add(Restrictions.eq("searchAgent", searchForm.getAgent()))
					.add(Restrictions.eq("activeFlag", 'Y'))
					.add((Restrictions.eq("userId", userId)));
			List<MonsterSearchSetup> sameAgentList = (List<MonsterSearchSetup>) criteriaForSameSearchAgent
					.list();
			// HQL for deleting the record
			String hql = "delete from MonsterSearchSetup "
					+ "where userId = :user_id "
					+ "and searchAgent = :search_agent "
					+ "and activeFlag = :active_flag";
			deleteSameAgent(sameAgentList, searchForm, session, userId, hql);

			// store the input into database
			session.save(monsterSearchSetup);
			session.getTransaction().commit();
			session.close();

			MonsterSearch monster = new MonsterSearch();
		    outputString = performSearch(monster, searchForm);
		}
		;

		return outputString;
		// return null;
	}

	private boolean stringToBoolean(char a) {
		if (a == 'Y')
			return true;
		else if (a == 'N')
			return false;
		else
			return true;
	};

	private String performSearch(CandidateSearch search, SearchFormDTO searchForm)
			throws Exception {
		search.setMaxCandidates(searchForm.getMaxCandidates());
		search.setCandidateStart(searchForm.getCandidateIndex());
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/mm/dd");
		if (searchForm.getAfterDate() != null
				&& !searchForm.getAfterDate().equals("")) {
			Date convertedDate = dateFormat.parse(searchForm.getAfterDate());
			search.setAfterDate(convertedDate);
		}
		search.setSearchAgent(searchForm.getAgent());
		search.setSkipViewed(stringToBoolean(searchForm.getSkipViewed()));
		search.setSkipNoRelocation(stringToBoolean(searchForm
				.getSkipNoRelocation()));
		search.setSortByDate(stringToBoolean(searchForm.getSortByDate()));
		search.setSendEmail(stringToBoolean(searchForm.getSendEmail()));
		search.setUserName(searchForm.getUsername());
		search.setPassword(searchForm.getPassword());

		search.startChrome();
		populateEmail(search, searchForm);

		return search.execute();
	}

	private void populateEmail(CandidateSearch search, SearchFormDTO searchForm)
			throws Exception {

		String emailTemplate = searchForm.getEmailTemplate();
		BufferedReader reader = new BufferedReader(new StringReader(
				searchForm.getEmailTemplate()));
		String firstLineInSession = reader.readLine();
		String emailContentInSession = emailTemplate.substring(emailTemplate
				.indexOf('\n') + 1);
		if (firstLineInSession != null && !firstLineInSession.isEmpty()) {
			search.getEmailSender().setFirstLineInSession(firstLineInSession);
			search.getEmailSender().setEmailContentInSession(emailContentInSession);
			System.out.println("the rest is "
					+ search.getEmailSender().getEmailContentInSession());
		}
		
		search.getEmailSender().setEmailSubject(searchForm.getEmailSubject());
		search.getEmailSender().setFromPerson(searchForm.getFromPerson());
		search.getEmailSender().setFromEmail(searchForm.getFromEmail());
	}

	private void deleteSameAgent(List sameAgentList, SearchFormDTO searchForm,
			Session session, int userId, String hql) {

		if (sameAgentList.size() > 0) {
			// there is a record with same agent, delete that one

			String sameAgent = searchForm.getAgent();

			Query query = session.createQuery(hql);
			query.setParameter("user_id", userId);
			query.setParameter("search_agent", sameAgent);
			query.setParameter("active_flag", 'Y');
			int result = query.executeUpdate();
			System.out.println("Rows affected: " + result);
		}

	}

}
