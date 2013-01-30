package nl.googlethursday.projectbackoffice.web.service.webservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;

import nl.googlethursday.projectbackoffice.entity.Project;
import nl.googlethursday.projectbackoffice.service.ProjectBackofficeServiceMongoDB;

/**
 * Webservice tbv Projecten
 * @author rodo
 *
 */
//@Stateless
@WebService(serviceName="projectwebservice", targetNamespace="nl.googlethursday")
public class ProjectWebservice {

	@EJB
	ProjectBackofficeServiceMongoDB service;

	/**
	 * Ophalen van alle projecten
	 * @return
	 */
	@WebMethod
	public List<Project> ophalenProjecten(){
	 return (service.getProjectList());
	}
	
	/**
	 * Ophalen specifiek project
	 * @param projectId
	 * @return
	 */
	@WebMethod
	public Project ophalenProject(int projectId){
		return service.getProject(projectId);
	}
	
	/**
	 * Opslaan project
	 * @param project
	 * @return
	 */
	@WebMethod
	public boolean opslaanProject(Project project){
		
		try {
			service.opslaanProjectInList(project);
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
	}
	

}
