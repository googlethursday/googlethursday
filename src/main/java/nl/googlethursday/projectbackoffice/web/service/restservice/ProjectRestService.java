package nl.googlethursday.projectbackoffice.web.service.restservice;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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
import nl.googlethursday.projectbackoffice.interceptors.LoggingInterceptor;
import nl.googlethursday.projectbackoffice.service.mongodb.MongoDBService;
import nl.googlethursday.projectbackoffice.sessioncontext.SessionContext;

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
@Produces({ "application/json", "application/xml" })
@Interceptors(LoggingInterceptor.class)
public class ProjectRestService {

	private final static Logger logger = LoggerFactory.getLogger(ProjectRestService.class);
	private final static org.apache.log4j.Logger logger2 = org.apache.log4j.Logger.getLogger(ProjectRestService.class);
	
	// tbv cross site json calls
	private final static String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
	
	
	@EJB
	MongoDBService service;

	// wordt gebruikt om de juiste http response terug te geven
	ResponseBuilder builder;

	@GET
	@Path("/json")
	//@Produces({ "application/json" })
	@Produces({ "application/javascript" })
	
	public Response getJsonProjects() {
		List<Project> projects = new ArrayList<Project>();
		projects.add(new Project("jaap2", "en", "martijn2"));
		projects.add(new Project("martijn3", "en", "jaap3"));
		builder=Response.ok(projects);
		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		return builder.build();
	}

	/**
	 * Ophalen alle projecten
	 * 
	 * @return
	 */
	@GET
	@Path("/")
	public List<JAXBProject> getProjects() {
		// ophalen van alle projecten

		// FIXME: tijdelijk voor collega's
		List<Project> projects = new ArrayList<Project>();
		projects.add(new Project("jaap1", "en", "martijn"));
		projects.add(new Project("martijn2", "en", "jaap"));
		// List<Project> projects = service.getProjects();

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
	@Produces({ "application/json" })
	public Response zoekProject(@PathParam("projectZoekString") String projectZoekString) {

		SessionContext.getSleutel().set(projectZoekString);
		
		if (StringUtils.isEmpty(projectZoekString)) {
			builder = Response.noContent();
			logger.debug("niets gevonden");
		} else {
			logger.debug("iets gevonden");
			List<Project> projectList = service.zoekProjectinDb(projectZoekString);

			builder = Response.ok(projectList);
			logger.debug("returnwaarde:" + projectList);
		}
		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
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

		SessionContext.getSleutel().set(projectnaam);
		
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

		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		
		// geef antwoord
		return builder.build();
	}

	/**
	 * POST maakt een nieuwe resource op basis van een json aanlevering maar met
	 * een op de server geidentificeerde resource (vandaar geen id meegegeven)
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
		SessionContext.getSleutel().set(project.getProjectNaam());

		service.opslaanProject(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project));
		builder = Response.ok();
		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		return builder.build();
	}

	/**
	 * PUT van een project, een bestaande resource of een nieuwe resource wordt
	 * vanaf de CLIENT geinstantieerd (vandaar de id die meegegeven wordt) PUT
	 * is idempotent. <br/>
	 * Voor het testen: voeg de volgende header toe Content-Type:
	 * application/json; charset=utf-8 <br/>
	 * Een JSON voorbeeld: <b>{"projectOmschrijving":"omschrijvingProject2"
	 * ,"projectLeider":"projectLeider2" ,"projectNaam":"naamProject2"}</b> <br/>
	 * 
	 * @param project
	 * @return HTTP 500 indien serverfout <br>
	 *         HTTP 304 Not Modified indien resource niet gevonden<br>
	 *         HTTP 200 indien ok
	 */
	@PUT
	// @Path("/put/{projectId:[0-9][0-9]*}")
	@Path("/put/{projectId}")
	// @Consumes({"application/json"})
	@Consumes(MediaType.APPLICATION_JSON)
	public Response createOrUpdateProject(@PathParam("projectId") String id, JAXBProject project) {
		SessionContext.getSleutel().set(id);
		
		if (service.updateProject(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project)) == true) {
			builder = Response.ok();
		} else {
			builder = Response.notModified();
		}
		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
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
		SessionContext.getSleutel().set(project.getProjectNaam());
		if (service.updateProject(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project)) == true) {
			builder = Response.ok();
		} else {
			builder = Response.notModified();
		}
		return builder.build();
	}

	/**
	 * Delete van een resource op basis van de aangeleverde projectnaam
	 * 
	 * @param projectNaam
	 */
	@DELETE
	@Path("/delete/{projectNaam}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteProject(@PathParam("projectNaam") String projectNaam) {
		SessionContext.getSleutel().set(projectNaam);
		
		if (service.verwijderProject(new Project(projectNaam, null, null)) == true) {
			builder = Response.ok();
		} else {
			builder = Response.notModified();
		}
		builder.header(ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		return builder.build();
	}

	public MongoDBService getService() {
		return service;
	}

	public void setService(MongoDBService service) {
		this.service = service;
	}
}
