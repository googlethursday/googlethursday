package nl.googlethursday.projectbackoffice.service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;

import nl.googlethursday.projectbackoffice.entity.Project;

import org.apache.commons.lang.StringUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

/**
 * Service tbv ophalen en opslaan van Project entiteiten gekoppeld aan MongoDB
 * TODO: unittesten TODO: generiek maken zoekopdrachten
 * 
 * @author rodo
 * 
 */
@Stateless
public class ProjectBackofficeServiceMongoDB {

	public final static String DATABASENAAM = "rodofumi";
	public final static String COLLECTIONNAAM = "projecten";

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
			db = conn.getDB(DATABASENAAM);
			if (!db.authenticate(USERNAME, PWD.toCharArray())) {
				System.out.println("unable to authenticate");
				throw new MongoException("unable to authenticate");
			}

			System.out.println("authenticated");

			// lees de collectie met projecten
			coll = db.getCollection(COLLECTIONNAAM);

			if (coll.count() == 0) {
				System.out.println("collection leeg");
				// tabel is leeg, deze vullen met testgegevens
				coll.insert(createDBObject(new Project("projectnaam1", "projectomschrijving1", "projectleider1")));
				coll.insert(createDBObject(new Project("projectnaam2", "projectomschrijving2", "projectleider2")));
				coll.insert(createDBObject(new Project("projectnaam3", "projectomschrijving3", "projectleider3")));
			}
		} catch (Exception e) {
			System.out.println("exceptie:" + e);
		}
	}

	/**
	 * tbv testen
	 * 
	 * @param mongoconnection
	 */
	public ProjectBackofficeServiceMongoDB(Mongo mongoconnection) {

		try {
			conn = mongoconnection;
			db = conn.getDB(DATABASENAAM);

		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * ophalen project uit database op basis van een aangeleverd id
	 * 
	 * @param id
	 * @return Project indien gevonden, null indien niet gevonden
	 */
	public Project getProject(String projectnaam) {

		// ophalen collectie
		coll = db.getCollection(COLLECTIONNAAM);

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
		coll = db.getCollection(COLLECTIONNAAM);

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
		coll = db.getCollection(COLLECTIONNAAM);

		// maak een like query, hiervoor gebruikt MongoDB een regexp
		String pattern = zoekstring;

		System.out.println("voer query uit met zoekstring:" + zoekstring);
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
		coll = db.getCollection(COLLECTIONNAAM);

		// tbv zoeken op bestaand record obv projectnaam
		String projectnaam = project.getProjectNaam();

		// upsert:insert/update gezamelijk op basis van query
		BasicDBObject query = new BasicDBObject();

		System.out.println("opslaan van project:" + projectnaam);

		// maak de query aan tbv upsert
		query.put("projectnaam", projectnaam);

		// 3e param upsert (insert/update afhankelijk van hit op de query)
		// 4e param = multi, true geeft update over meerdere documents
		WriteResult result = coll.update(query, createDBObject(project), true, false);

		System.out.println(result.getError());

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
		System.out.println("delete 1");
		coll = db.getCollection(COLLECTIONNAAM);

		System.out.println("delete 2");
		BasicDBObject deleteQuery = new BasicDBObject();
		deleteQuery.put("projectnaam", project.getProjectNaam());

		System.out.println("delete 3");
		DBObject objectToDelete = coll.findOne(deleteQuery);
		if (objectToDelete == null) {
			System.out.println("object met sleutel: " + project.getProjectNaam() + "niet gevonden");
			return false;
		}
		System.out.println("delete 4");
		WriteResult result = coll.remove(objectToDelete);

		System.out.println("delete 5");
		return true;
	}

	/**
	 * helper tbv conversie dbobject->project
	 * 
	 * @param dbObject
	 * @return
	 */
	private Project createProject(DBObject dbObject) {
		System.out.println(dbObject);
		
		System.out.println(dbObject.get("projectnaam").toString());
		System.out.println(dbObject.get("projectomschrijving").toString());
		System.out.println(dbObject.get("projectleider").toString());
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

	/**
	 * ophalen laatst uitgegeven ID uit de database
	 * 
	 * @return
	 */
	private Integer bepaalId() {
		return new Integer(getNextId(db, "seq"));
	}

	/**
	 * Get the next unique ID for a named sequence.
	 * 
	 * @param db
	 *            Mongo database to work with
	 * @param seq_name
	 *            The name of your sequence (I name mine after my collections)
	 * @return The next ID
	 */
	public static String getNextId(DB db, String seq_name) {
		String sequence_collection = "seq"; // the name of the sequence
											// collection
		String sequence_field = "seq"; // the name of the field which holds the
										// sequence

		DBCollection seq = db.getCollection(sequence_collection); 
		// get the collection (this  will create it  if  needed)

		// this object represents your "query", its analogous to a WHERE clause
		// in SQL
		DBObject query = new BasicDBObject();
		query.put("_id", seq_name); // where _id = the input sequence name

		// this object represents the "update" or the SET blah=blah in SQL
		DBObject change = new BasicDBObject(sequence_field, 1);
		DBObject update = new BasicDBObject("$inc", change); 
		// the $inc here is a mongodb command for  increment

		// Atomically updates the sequence field and returns the value for you
		DBObject res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
		return res.get(sequence_field).toString();
	}
}
