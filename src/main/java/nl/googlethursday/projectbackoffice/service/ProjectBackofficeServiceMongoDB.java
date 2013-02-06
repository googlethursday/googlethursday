package nl.googlethursday.projectbackoffice.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.Stateless;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.WriteResult;

import nl.googlethursday.projectbackoffice.entity.Project;

/**
 * Service tbv ophalen en opslaan van Project entiteiten gekoppeld aan MongoDB
 * TODO: unittesten TODO: generiek maken zoekopdrachten
 * 
 * @author rodo
 * 
 */
@Stateless
public class ProjectBackofficeServiceMongoDB {

	private final static Logger logger = LoggerFactory.getLogger(ProjectBackofficeServiceMongoDB.class);

	public final static String COLLECTIONNAAM = "projecten";

	/*********************************************/
	/** mongodb settings **/
	/*********************************************/
	private DB db = null;
	private DBCollection coll = null;
	public static Mongo connection;

	/**
	 * Constructor
	 */

	public ProjectBackofficeServiceMongoDB() {

		if (connection != null) {
			// is er een connection meegeleverd (mock) dan alleen de database
			// ophalen
			this.db = MongoUtil.getMongo(connection);
		} else {
			// er is geen connection, prd versie dus connectie opzetten
			try {
				logger.debug("maak een connectie");
				this.db = MongoUtil.getMongo(new Mongo(MongoUtil.HOST, MongoUtil.PORT));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MongoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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

		logger.debug("voer query uit met zoekstring:" + zoekstring);
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

		logger.debug("opslaan van project:" + projectnaam);

		// maak de query aan tbv upsert
		query.put("projectnaam", projectnaam);

		// 3e param upsert (insert/update afhankelijk van hit op de query)
		// 4e param = multi, true geeft update over meerdere documents
		WriteResult result = coll.update(query, createDBObject(project), true, false);

		logger.debug(result.getError());

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
		logger.debug("delete 1");
		coll = db.getCollection(COLLECTIONNAAM);

		BasicDBObject deleteQuery = new BasicDBObject();
		deleteQuery.put("projectnaam", project.getProjectNaam());

		DBObject objectToDelete = coll.findOne(deleteQuery);
		if (objectToDelete == null) {
			logger.debug("object met sleutel: " + project.getProjectNaam() + "niet gevonden");
			return false;
		}

		WriteResult result = coll.remove(objectToDelete);

		logger.debug(result.getError());
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

	/**
	 * ophalen laatst uitgegeven ID uit de database
	 * 
	 * @return
	 */
	//private Integer bepaalId() {
	//	return new Integer(getNextId(db, "seq"));
	//}

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
		// get the collection (this will create it if needed)

		// this object represents your "query", its analogous to a WHERE clause
		// in SQL
		DBObject query = new BasicDBObject();
		query.put("_id", seq_name); // where _id = the input sequence name

		// this object represents the "update" or the SET blah=blah in SQL
		DBObject change = new BasicDBObject(sequence_field, 1);
		DBObject update = new BasicDBObject("$inc", change);
		// the $inc here is a mongodb command for increment

		// Atomically updates the sequence field and returns the value for you
		DBObject res = seq.findAndModify(query, new BasicDBObject(), new BasicDBObject(), false, update, true, true);
		return res.get(sequence_field).toString();
	}
}
