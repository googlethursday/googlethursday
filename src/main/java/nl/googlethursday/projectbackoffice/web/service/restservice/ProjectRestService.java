package nl.googlethursday.projectbackoffice.web.service.restservice;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.xml.bind.JAXBException;

import nl.googlethursday.projectbackoffice.entity.Project;
import nl.googlethursday.projectbackoffice.entity.jaxb.JAXBProject;
import nl.googlethursday.projectbackoffice.helper.ProjectBackofficeHelper;
import nl.googlethursday.projectbackoffice.service.mongodb.MongoDBService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */

@Stateless
@Path("/projectService")
@Produces({"application/json","application/xml"})
public class ProjectRestService {

	private final static Logger logger = LoggerFactory.getLogger(ProjectRestService.class);
	
	@EJB
	MongoDBService service;

	// wordt gebruikt om de juiste http response terug te geven
	ResponseBuilder builder;

	/**
	 * Ophalen alle projecten
	 * 
	 * @return
	 */
	@GET
	@Path("/")
	public List<JAXBProject> getProjects() {
		// ophalen van alle projecten
		
		//FIXME: tijdelijk voor collega's
		List <Project> projects = new ArrayList<Project>();
		projects.add(new Project("jaap1","en","martijn"));
		projects.add(new Project("martijn2","en","jaap"));
		//List<Project> projects = service.getProjects();

		List<JAXBProject> jaxbProjects = null;

		if (projects != null) {
			// 1 of meer projecten gevonden, omvormen tot jaxbProjecten
			jaxbProjects = ProjectBackofficeHelper.ProjectListToJAXBProjectList(projects);
		}
		return jaxbProjects;
	}

	/**
	 * Ophalen alle projecten
	 * 
	 * @return
	 */
	@GET
	@Path("/nw")
	public List<JAXBProject> getProjectsNw() {
		// ophalen van alle projecten
		logger.debug("getProjectsNw");
		List<Project> projects = service.getProjects();

		List<JAXBProject> jaxbProjects = null;

		if (projects != null) {
			// 1 of meer projecten gevonden, omvormen tot jaxbProjecten
			jaxbProjects = ProjectBackofficeHelper.ProjectListToJAXBProjectList(projects);
		}
		return jaxbProjects;
	}
	
	@GET
	@Path("/zoekProject/{projectZoekString}")
	public Response zoekProject(@PathParam("projectZoekString") String projectZoekString) {
		logger.debug("zoekstring:"+projectZoekString);
		if (StringUtils.isEmpty(projectZoekString)) {
			builder = Response.noContent();
		}
		else {
			List<Project> projectList = service.zoekProject(projectZoekString);
			builder = Response.ok(projectList) ;
		}
		
		return builder.build();
	
	}
	
	/**
	 * Ophalen specifiek project
	 * 
	 * @param projectId
	 * @return HTTP 200 + JAXBProject indien gevonden <br>
	 *         HTTP 204 No Content indien niet gevonden <br>
	 *         HTTP 500 indien serverfout <br>
	 */
	@GET
	@Path("/{projectNaam}")
	public Response getSpecificProject(@PathParam("projectNaam") String projectnaam) {

		JAXBProject jaxbProject = null;

		try {
			// ophalen project
			Project project = service.getProject(projectnaam);

			if (project != null) {
				jaxbProject = ProjectBackofficeHelper.ProjectToJAXBProject(project);
			}

			if (jaxbProject == null) {
				// niet gevonden, juist http status teruggeven
				builder = Response.noContent();
			} else {
				// project gevonden, project + juiste http status teruggeven
				builder = Response.ok(jaxbProject);
			}

		} catch (JAXBException e) {
			// fout, geef juiste http status terug
			builder = Response.serverError();
			
			builder.build();
		}

		// geef antwoord
		return builder.build();

	}

	/**
	 * POST maakt een nieuwe resource op basis van een json aanlevering maar met een op de server geidentificeerde resource (vandaar geen id meegegeven)
	 * POST is niet idempotent.
	 * 
	 * @param project
	 * @return Resource created <br/>
	 * <br/>
	 *         Voor het testen: voeg de volgende header toe <b>Content-type:
	 *         application/json; charset=utf-8</b> <br/>
	 * <br/>
	 *         Een JSON voorbeeld:
	 *         <b>{"projectOmschrijving":"omschrijvingProject2"
	 *         ,"projectLeider":"projectLeider2"
	 *         ,"projectNaam":"naamProject2"}</b>
	 */
	@POST
	public Response createProject(JAXBProject project) {
		logger.debug("save project");
		service.opslaanProject(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project));
		builder = Response.ok();
		return builder.build();
	}

	/**
	 * PUT van een project, een bestaande resource of een nieuwe resource wordt vanaf de CLIENT geinstantieerd (vandaar de id die meegegeven wordt)
	 * PUT is idempotent.
	 * <br/>
	 * Voor het testen: voeg de volgende header toe
	 * Content-Type: application/json; charset=utf-8
	 * <br/>
	 * Een JSON voorbeeld: <b>{"projectOmschrijving":"omschrijvingProject2"
	 * ,"projectLeider":"projectLeider2" ,"projectNaam":"naamProject2"}</b> <br/>
	 * 
	 * @param project
	 * @return HTTP 500 indien serverfout <br>
	 *         HTTP 304 Not Modified indien resource niet gevonden<br>
	 *         HTTP 200 indien ok
	 */
	@PUT
	//@Path("/put/{projectId:[0-9][0-9]*}")
	@Path("/put/{projectId}")
	//@Consumes({"application/json"})
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdateProject(@PathParam("projectId") String id, JAXBProject project) {
		logger.debug("PUT van id"+id);
		if (service.updateProject(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project)) == true) {
			builder = Response.ok();
		} else {
			builder = Response.notModified();
		}
		return builder.build();
	}

	/**
	 * Put zonder id
	 * 
	 * @param project
	 * @return
	 */
	@PUT
	@Path("/put")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdateProject(JAXBProject project) {
		logger.debug("PUT zonder id");
		if (service.updateProject(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project)) == true) {
			builder = Response.ok();
		} else {
			builder = Response.notModified();
		}
		return builder.build();
	}
	
	/**
	 * Delete van een resource op basis van de aangeleverde projectnaam
	 * @param projectNaam
	 */
	@DELETE
	@Path("/delete/{projectNaam}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("projectNaam") String projectNaam){
		logger.debug("delete:" + projectNaam);
		
		if (service.verwijderProject(new Project(projectNaam,null,null)) == true) {
			builder=Response.ok();
		} else {
			builder = Response.notModified();
		}
		return builder.build();
	}

	public MongoDBService getService() {
		return service;
	}

	public void setService(MongoDBService service) {
		this.service = service;
	}
}
