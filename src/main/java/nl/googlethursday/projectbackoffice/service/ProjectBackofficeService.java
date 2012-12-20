package nl.googlethursday.projectbackoffice.service;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;

import nl.googlethursday.projectbackoffice.entity.Project;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Service tbv ophalen en opslaan van Project entiteiten
 * 
 * @author rodo
 * 
 */
@Stateless
public class ProjectBackofficeService {

	List<Project> projectList;

	/*********************************************/
	/** mongodb settings **/
	/*********************************************/
	final String COLLECTIONNAME = "projecten";
	final String USERNAME = "admin";
	final String PWD = "Fe7WQ2cN2wp9";
	final String IP = "127.10.61.129";
	final int PORT = 27017;

	private Mongo conn = null;
	private DB db = null;
	private DBCollection coll = null;

	public ProjectBackofficeService() {
		try {
			/**
			 * FIXME connectie niet in code maken
			 * 
			 */
			conn = new Mongo(IP, PORT);
			db = conn.getDB("rodofumi");
			if (!db.authenticate(USERNAME, PWD.toCharArray())) {
				System.out.println("unable to authenticate");
				throw new MongoException("unable to authenticate");
			}

			System.out.println("authenticated");

			// Maak collection indien niet aanwezig
			coll = db.getCollection(COLLECTIONNAME);
			
			
			if (coll.count() == 0) {
				System.out.println("collection leeg");
				// tabel is leeg, deze vullen met testgegevens
				BasicDBObject document = new BasicDBObject();
				document.put("projectid", new Integer(1));
				document.put("projectnaam", "projectnaam1");
				document.put("projectomschrijving", "omschrijving1");
				document.put("projectleider", "projectleider1");
				coll.insert(document);
				document = new BasicDBObject();
				document.put("projectid", new Integer(2));
				document.put("projectnaam", "projectnaam2");
				document.put("projectomschrijving", "omschrijving2");
				document.put("projectleider", "projectleider2");
				coll.insert(document);
				document = new BasicDBObject();
				document.put("projectid", new Integer(3));
				document.put("projectnaam", "projectnaam3");
				document.put("projectomschrijving", "omschrijving3");
				document.put("projectleider", "projectleider3");
				coll.insert(document);
			}
			System.out.println("volgende stap2");
		} catch (Exception e) {
			System.out.println(e);
		}

		System.out.println("hier geen exception meer");

	}

	/**
	 * ophalen project uit database op basis van een aangeleverd id
	 * 
	 * @param id
	 * @return
	 */
	public Project getProject(int id) {

		DBObject searchById = new BasicDBObject("projectid", new Integer(id));
		DBObject found = coll.findOne(searchById);
		System.out.println(found);
		String projectnaam = (String) found.get("projectnaam");
		String projectomschrijving = (String) found.get("projectomschrijving");
		String projectleider = (String) found.get("projectleider");
		Project project = new Project(projectnaam, projectomschrijving, projectleider);
		return (project);
	}

	/**
	 * ophalen alle projecten uit database
	 * 
	 * @return
	 */
	public List<Project> getProjects() {
		
		Project project;
		List<Project> projectList = new ArrayList<Project>();

		List<DBObject> list = coll.getIndexInfo();
		
		String projectnaam, projectomschrijving, projectleider;
		
		// loop door teruggegegeven lijst uit db
		for (DBObject o : list) {
			System.out.println("opgehaald objectid: "+o.get("projectid"));
			
			projectnaam = (String) o.get("projectnaam");
			projectomschrijving = (String) o.get("projectomschrijving");
			projectleider = (String) o.get("projectleider");
			project = new Project(projectnaam, projectomschrijving, projectleider);

			System.out.println("toevoegen aan list: "+ project.getProjectNaam());
			projectList.add(project);
		}

		return projectList;
	}

	public boolean updateProjectInList(Project project) {
		// FIXME nog doorvoeren wegschrijven in DB
		String projectnaam = project.getProjectNaam();

		// zoek project
		for (Project proj : projectList) {

			// haal projectnaam uit de list op
			String onderhandenProjectNaam = proj.getProjectNaam();

			// komt de aangeleverde projectnaam overeen met het onderhanden
			// projectelement?
			if (projectnaam.equals(onderhandenProjectNaam)) {
				// naam gevonden, update project
				proj = project;

				// geef boolean=true terug
				return true;
			}

		}
		// project niet gevonden
		return false;

	}

	public void opslaanProjectInList(Project project) {
		// FIXME Doorvoeren wegschrijven in database
		projectList.add(project);
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}

}
