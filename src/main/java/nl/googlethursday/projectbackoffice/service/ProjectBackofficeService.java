package nl.googlethursday.projectbackoffice.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.mongodb.Mongo;

import nl.googlethursday.projectbackoffice.entity.Project;

/**
 * Service tbv ophalen en opslaan van Project entiteiten
 * @author rodo
 *
 */
@Stateless
public class ProjectBackofficeService {
/**
 * TODO wegschrijven in db
 */
	List<Project> projectList;

	@EJB
	MongoDBService service;
	
	public ProjectBackofficeService() {

		service.openMongoConnection();
		
		
		projectList = new ArrayList<Project>();
		Project p = new Project("naamProject", "omschrijvingProject", "projectLeider");
		projectList.add(p);
		p = new Project("naamProject2", "omschrijvingProject2", "projectLeider2");
		projectList.add(p);
		p = new Project("naamProject3", "omschrijvingProject3", "projectLeider3");
		projectList.add(p);
	}

	public Project getProject(int id) {
		
		if (id>=projectList.size()){
			return null;
		}
		return (projectList.get(id));
	}

	public List<Project> getProjects() {
		System.out.println(projectList);
		return projectList;
	}

	public List<Project> getProjectList() {
		return projectList;
	}

	public boolean updateProjectInList(Project project){
		String projectnaam = project.getProjectNaam();
		
		//zoek project
		for (Project proj : projectList) {
		
			// haal projectnaam uit de list op
			String onderhandenProjectNaam = proj.getProjectNaam();
			
			// komt de aangeleverde projectnaam overeen met het onderhanden projectelement?
			if (projectnaam.equals(onderhandenProjectNaam)){
				//naam gevonden, update project
				proj=project;
				
				// geef boolean=true terug
				return true;
			}
			
					
		}
		// project niet gevonden
		return false;

	}
	
	public void opslaanProjectInList(Project project){
		projectList.add(project);
	}
	public void setProjectList(List<Project> projectList) {
		this.projectList = projectList;
	}

}
