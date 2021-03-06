package nl.googlethursday.projectbackoffice.service.mongodb;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;


import nl.googlethursday.projectbackoffice.entity.Project;
import nl.googlethursday.projectbackoffice.interceptors.LoggingInterceptor;
import nl.googlethursday.projectbackoffice.sessioncontext.SessionContext;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * MongoDB operaties tbv project
 * 
 * @author rodo
 * 
 */
@Stateless
@Interceptors(LoggingInterceptor.class)
public class MongoDBService {

	private Mongo mongo;
	private DB mongoDB;
	private DBCollection coll;

	public final static String COLLECTIONNAAM = "projecten";

	public MongoDBService() {
		// haal mongo connectie op
		mongo = (new MongoUtil()).getMongo();

		// haal de specifieke database op
		mongoDB = MongoUtil.getDB(mongo);
	}

	/**
	 * constructor tbv meegeven embeddedMongo
	 * 
	 * @param input
	 */
	public MongoDBService(Mongo input) {
		this.mongo = input;
		mongoDB = MongoUtil.getDB(mongo);
	}

	/**
	 * ophalen project uit database op basis van een aangeleverd id
	 * 
	 * @param id
	 * @return Project indien gevonden, null indien niet gevonden
	 */
	public Project getProject(String projectnaam) {
		// ophalen collectie
		SessionContext.getSleutel().set(projectnaam);
		coll = mongoDB.getCollection(COLLECTIONNAAM);

		// zoek naar het meegeleverde id
		DBObject searchById = new BasicDBObject("projectnaam", projectnaam);
		DBObject found = coll.findOne(searchById);

		Project project = null;

		if (found != null) {
			//logger.debug("entiteit gevonden");
			project = createProject(found);
		}

		return project;
	}

	/**
	 * ophalen van alle projecten uit database
	 * 
	 * @return
	 */
	public List<Project> getProjects() {
		SessionContext.getSleutel().set("");
		DBObject one;
		List<Project> projectList;
		coll = mongoDB.getCollection(COLLECTIONNAAM);

		// ophalen alle elementen
		DBCursor cursor = coll.find();

		try {
			projectList = new ArrayList<Project>();

			while (cursor.hasNext()) {
				// loop over alle elementen uit de db
				one = cursor.next();

				projectList.add(createProject(one));
			}

		} finally {
			cursor.close();
		}

		if (projectList == null || projectList.size() == 0) {
			return null;
		}
		return projectList;
	}

	/**
	 * update van het in de db aanwezige project, op basis van de unieke
	 * projectnaam
	 * 
	 * @param project
	 * @return true indien geslaagd, false indien projectnaam niet voorkomt
	 */
	public boolean updateProject(Project project) {
		return opslaanProject(project);
	}

	/**
	 * Zoek op meegegeven projectnaam
	 */
	public List<Project> zoekProjectinDb(String zoekstring) {
		if (zoekstring.equals("1")){
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SessionContext.getSleutel().set(zoekstring);
		
		if (StringUtils.isEmpty(zoekstring)) {
			return null;
		}

		DBObject one;

		List<Project> projectList = new ArrayList<Project>();
		coll = mongoDB.getCollection(COLLECTIONNAAM);

		// maak een like query, hiervoor gebruikt MongoDB een regexp
		String pattern = zoekstring;

		BasicDBObject db = new BasicDBObject();
		Pattern regExPattern = Pattern.compile(pattern);

		db.put("projectnaam", regExPattern);
		
		// voer de query uit
		DBCursor cursor = coll.find(db);

		try {
			while (cursor.hasNext()) {
				
				// loop over alle elementen uit de db
				one = cursor.next();
				//logger.debug("volgende");
				projectList.add(createProject(one));
			}

		} finally {
			//logger.debug("sluit connectie");
			cursor.close();
		}

		return projectList;
	}

	/**
	 * insert/update project in database
	 * 
	 * @param project
	 */
	public boolean opslaanProject(Project project) {
		SessionContext.getSleutel().set(project.getProjectNaam());
		// ophalen collectie
		coll = mongoDB.getCollection(COLLECTIONNAAM);

		// tbv zoeken op bestaand record obv projectnaam
		String projectnaam = project.getProjectNaam();

		// upsert:insert/update gezamelijk op basis van query
		BasicDBObject query = new BasicDBObject();

		// maak de query aan tbv upsert
		query.put("projectnaam", projectnaam);

		// 3e param upsert (insert/update afhankelijk van hit op de query)
		// 4e param = multi, true geeft update over meerdere documents
		coll.update(query, createDBObject(project), true, false);

		return true;
	}

	/**
	 * Verwijder element obv projectnaam
	 * 
	 * @param project
	 * @return
	 */
	public boolean verwijderProject(Project project) {
		SessionContext.getSleutel().set(project.getProjectNaam());
		// ophalen collectie
		coll = mongoDB.getCollection(COLLECTIONNAAM);

		BasicDBObject deleteQuery = new BasicDBObject();
		deleteQuery.put("projectnaam", project.getProjectNaam());

		DBObject objectToDelete = coll.findOne(deleteQuery);
		if (objectToDelete == null) {
			return false;
		}

		coll.remove(objectToDelete);

		return true;
	}

	/**
	 * helper tbv conversie dbobject->project
	 * 
	 * @param dbObject
	 * @return
	 */
	private Project createProject(DBObject dbObject) {
		return new Project(dbObject.get("projectnaam").toString(), dbObject.get("projectomschrijving").toString(),
				dbObject.get("projectleider").toString());
	}

	/**
	 * helper tbv conversie project->dbobject
	 * 
	 * @param project
	 * @return
	 */
	protected DBObject createDBObject(Project project) {
		SessionContext.getSleutel().set(project.getProjectNaam());
		BasicDBObject document = new BasicDBObject();
		document.put("projectnaam", project.getProjectNaam());
		document.put("projectomschrijving", project.getProjectOmschrijving());
		document.put("projectleider", project.getProjectLeider());
		return document;
	}
}
