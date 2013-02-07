package nl.googlethursday.projectbackoffice.service.mongodb;
        
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import nl.googlethursday.projectbackoffice.entity.Project;
import org.apache.commons.lang.StringUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoDBService {
	private Mongo mongo;
	private DB mongoDB;
	private String databasenaam = "rodofumi";
	private DBCollection coll;
	public final static String COLLECTIONNAAM = "projecten";

	public MongoDBService() {
		mongo = MongoUtil.getMongo();
		mongoDB = mongo.getDB(databasenaam);
	}

	/**
	 * ophalen project uit database op basis van een aangeleverd id
	 * 
	 * @param id
	 * @return Project indien gevonden, null indien niet gevonden
	 */
	public Project getProject(String projectnaam) {

		// ophalen collectie
		coll = mongoDB.getCollection(COLLECTIONNAAM);

		// zoek naar het meegeleverde id
		DBObject searchById = new BasicDBObject("projectnaam", projectnaam);
		DBObject found = coll.findOne(searchById);

		Project project = null;

		if (found != null) {
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
	public List<Project> zoekProject(String zoekstring) {
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
				projectList.add(createProject(one));
			}

		} finally {
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
		BasicDBObject document = new BasicDBObject();
		document.put("projectnaam", project.getProjectNaam());
		document.put("projectomschrijving", project.getProjectOmschrijving());
		document.put("projectleider", project.getProjectLeider());
		return document;
	}
}
