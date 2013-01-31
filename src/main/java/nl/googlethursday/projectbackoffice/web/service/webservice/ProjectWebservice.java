package nl.googlethursday.projectbackoffice.web.service.webservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

import org.jboss.wsf.spi.annotation.WebContext;

import nl.googlethursday.projectbackoffice.entity.Project;
import nl.googlethursday.projectbackoffice.service.ProjectBackofficeServiceMongoDB;

/**
 * Webservice tbv Projecten
 * @author rodo
 * FIXME: WebContext zou een context moeten meegeven, maar dit gebeurt niet remote
 * 
 */
@Stateless
@WebService(serviceName="projectwebservice", targetNamespace="nl.googlethursday")
@SOAPBinding(style = SOAPBinding.Style.DOCUMENT, use = SOAPBinding.Use.LITERAL, parameterStyle = SOAPBinding.ParameterStyle.WRAPPED)
@WebContext(contextRoot="/xyz", urlPattern="/*", secureWSDLAccess=false)
public class ProjectWebservice {

	@EJB
	ProjectBackofficeServiceMongoDB service;

	/**
	 * Ophalen van alle projecten
	 * @return
	 */
	@WebMethod
	public List<Project> ophalenProjecten(){
	 return (service.getProjects());
	}
	
	/**
	 * Ophalen specifiek project
	 * @param projectId
	 * @return
	 */
	@WebMethod
	public Project ophalenProject(String projectNaam){
		return service.getProject(projectNaam);
	}
	
	/**
	 * Opslaan project
	 * @param project
	 * @return
	 */
	@WebMethod
	public boolean opslaanProject(Project project){
		
		try {
			service.opslaanProject(project);
			return true;
		} catch (Exception e) {
			System.out.println(e);
			return false;
		}
		
	}
	

}
