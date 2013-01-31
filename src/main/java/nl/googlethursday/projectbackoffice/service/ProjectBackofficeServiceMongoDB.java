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


/**
 * Service tbv ophalen en opslaan van Project entiteiten
 * gekoppeld aan MongoDB
 * @author rodo
 * 
 */
@Stateless
public class ProjectBackofficeServiceMongoDB {
	public final static String COLLECTIONNAME = "projecten";
	public final static String COLLECTIONNAME_ID = "projectId";
	public final static String USERNAME = "admin";
	public final static String PWD = "Fe7WQ2cN2wp9";
	public final static String IP = "127.10.61.129";
	public final static int PORT = 27017;
	
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
			conn = new Mongo(IP, PORT);
			db = conn.getDB("rodofumi");
			if (!db.authenticate(USERNAME, PWD.toCharArray())) {
				System.out.println("unable to authenticate");
				throw new MongoException("unable to authenticate");
			}

			System.out.println("authenticated");

			// lees de collectie met projecten
			coll = db.getCollection(COLLECTIONNAME);

			if (coll.count() == 0) {
				System.out.println("collection leeg");

				// tabel is leeg, deze vullen met testgegevens
				Project p = new Project("projectnaam1", "projectomschrijving1", "projectleider1");
				System.out.println("vul een");
				coll.insert(createDBObject(p));

				p = new Project("projectnaam2", "projectomschrijving2", "projectleider2");
				System.out.println("vul twee");
				coll.insert(createDBObject(p));

				p = new Project("projectnaam3", "projectomschrijving3", "projectleider3");
				System.out.println("vul drie");
				coll.insert(createDBObject(p));
			}
			System.out.println("volgende stap2");
		} catch (Exception e) {
			System.out.println("exceptie:"+e);
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
		coll = db.getCollection(COLLECTIONNAME);

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

		coll = db.getCollection(COLLECTIONNAME);

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
		coll = db.getCollection(COLLECTIONNAME);
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
		System.out.println("bepaal id");
		
		collProjectId = db.getCollection(COLLECTIONNAME_ID);

		System.out.println(collProjectId);
		
		// indien niet gevonden, zet er '1' in
		if (collProjectId.count() == 0) {
			System.out.println("niets gevonden");
			BasicDBObject document = new BasicDBObject();
			document.put("sleutel", new Integer(1));
			collProjectId.insert(document);
			sleutel = 1;
		}

		System.out.println("volgende stap");
		
		// ophalen meest recente waarde
		DBCursor cursor = coll.find();
		sleutel = (Integer) cursor.next().get("sleutel");
		System.out.println("sleutel wordt: "+sleutel);
		return sleutel;
	}
}
