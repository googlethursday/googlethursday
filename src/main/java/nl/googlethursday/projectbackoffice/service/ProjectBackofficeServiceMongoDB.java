package nl.googlethursday.projectbackoffice.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import nl.googlethursday.projectbackoffice.entity.Project;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import nl.googlethursday.projectbackoffice.service.MongoDBProperties;

/**
 * Service tbv ophalen en opslaan van Project entiteiten
 * gekoppeld aan MongoDB
 * @author rodo
 * 
 */
@Stateless
public class ProjectBackofficeServiceMongoDB {

	/*********************************************/
	/** mongodb settings **/
	/*********************************************/

	private Mongo conn = null;
	private DB db = null;
	private DBCollection coll = null;
	private DBCollection collProjectId = null;
	private int sleutel = 0;

	/**
	 * Constructor
	 */
	public ProjectBackofficeServiceMongoDB() {

		try {
			/**
			 * FIXME connectie niet in code maken
			 */
			conn = new Mongo(MongoDBProperties.IP, MongoDBProperties.PORT);
			db = conn.getDB("rodofumi");
			if (!db.authenticate(MongoDBProperties.USERNAME, MongoDBProperties.PWD.toCharArray())) {
				System.out.println("unable to authenticate");
				throw new MongoException("unable to authenticate");
			}

			System.out.println("authenticated");

			// lees de collectie met projecten
			coll = db.getCollection(MongoDBProperties.COLLECTIONNAME);

			if (coll.count() == 0) {
				System.out.println("collection leeg");

				// tabel is leeg, deze vullen met testgegevens
				Project p = new Project("projectnaam1", "projectomschrijving1", "projectleider1");
				coll.insert(createDBObject(p));

				p = new Project("projectnaam2", "projectomschrijving2", "projectleider2");
				coll.insert(createDBObject(p));

				p = new Project("projectnaam3", "projectomschrijving3", "projectleider3");
				coll.insert(createDBObject(p));
			}
			System.out.println("volgende stap2");
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/**
	 * ophalen project uit database op basis van een aangeleverd id
	 * 
	 * @param id
	 * @return Project indien gevonden, null indien niet gevonden
	 */
	public Project getProject(int id) {

		// ophalen collectie
		coll = db.getCollection(MongoDBProperties.COLLECTIONNAME);

		// zoek naar het meegeleverde id
		DBObject searchById = new BasicDBObject("projectid", new Integer(id));
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

		Project project;
		DBObject one;

		List<Project> projectList = new ArrayList<Project>();
		String projectnaam, projectomschrijving, projectleider;

		coll = db.getCollection(MongoDBProperties.COLLECTIONNAME);

		// ophalen alle elementen
		DBCursor cursor = coll.find();

		try {
			while (cursor.hasNext()) {
				// loop over alle elementen uit de db
				one = cursor.next();
				System.out.println("opgehaald objectid: " + one.get("projectid"));
				projectList.add(createProject(one));
			}

		} finally {
			cursor.close();
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
	 * insert/update project in database
	 * 
	 * @param project
	 */
	public boolean opslaanProject(Project project) {
		coll = db.getCollection(MongoDBProperties.COLLECTIONNAME);
		String projectnaam = project.getProjectNaam();

		// upsert:insert/update gezamelijk op basis van query
		BasicDBObject query = new BasicDBObject();
		query.put("projectnaam", projectnaam);

		// 3e param upsert
		// 4e param = multi, true geeft update over meerdere documents
		coll.update(query, createDBObject(project), true, false);

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
	private DBObject createDBObject(Project project) {
		BasicDBObject document = new BasicDBObject();
		// bepaal laatst uitgegeven id
		document.put("projectid", bepaalId());
		document.put("projectnaam", project.getProjectNaam());
		document.put("projectomschrijving", project.getProjectOmschrijving());
		document.put("projectleider", project.getProjectLeider());
		return document;
	}

	/**
	 * ophalen laatst uitgegeven ID uit de database
	 * 
	 * @return
	 */
	private Integer bepaalId() {
		collProjectId = db.getCollection(MongoDBProperties.COLLECTIONNAME_ID);

		// indien niet gevonden, zet er '1' in
		if (collProjectId.count() == 0) {
			BasicDBObject document = new BasicDBObject();
			document.put("sleutel", new Integer(1));
			collProjectId.insert(document);
			sleutel = 1;
		}

		// ophalen meest recente waarde
		DBCursor cursor = coll.find();
		sleutel = (Integer) cursor.next().get("sleutel");
		return sleutel;
	}
}
