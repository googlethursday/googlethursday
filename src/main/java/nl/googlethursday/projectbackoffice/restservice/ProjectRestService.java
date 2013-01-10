package nl.googlethursday.projectbackoffice.restservice;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
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
import nl.googlethursday.projectbackoffice.service.ProjectBackofficeServiceMongoDB;

/**
 * JAX-RS Example
 * 
 * This class produces a RESTful service to read the contents of the members
 * table.
 */

@Stateless
@Path("/projectService")
public class ProjectRestService {

	@EJB
	ProjectBackofficeServiceMongoDB service;

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
		List<Project> projects = service.getProjects();

		List<JAXBProject> jaxbProjects = null;

		if (projects != null) {
			// 1 of meer projecten gevonden, omvormen tot jaxbProjecten
			jaxbProjects = ProjectBackofficeHelper.ProjectListToJAXBProjectList(projects);
		}
		return jaxbProjects;
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
	@Path("/{projectId}")
	public Response getSpecificProject(@PathParam("projectId") int projectId) {

		JAXBProject jaxbProject = null;

		try {
			// ophalen project
			Project project = service.getProject(new Integer(projectId).intValue());

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
		System.out.println("save project");
		service.opslaanProjectInList(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project));
		builder = Response.ok();
		return builder.build();
	}

	/**
	 * PUT van een project, een bestaande resource of een nieuwe resource wordt vanaf de CLIENT geinstantieerd (vandaar de id die meegegeven wordt)
	 * PUT is idempotent.
	 * <br/>
	 * Voor het testen: voeg de volgende header toe <b>Content-type:
	 * application/json; charset=utf-8</b> <br/>
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
	@Path("/{projectId:[0-9][0-9]*}")
	public Response createOrUpdateProject(@PathParam("projectId") String id, JAXBProject project) {
		System.out.println("PUT van id"+id);
		if (service.updateProjectInList(ProjectBackofficeHelper.JaxbProjectToProjectEntity(project)) == true) {
			builder = Response.ok();
		} else {
			builder = Response.notModified();
		}
		return builder.build();
	}

	//
	// @DELETE
	// @Consumes(MediaType.APPLICATION_JSON)
	// public void deleteProject(int id){
	// System.out.println("delete");
	// }
	//

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// //
	public ProjectBackofficeServiceMongoDB getService() {
		System.out.println(service);
		return service;
	}

	public void setService(ProjectBackofficeServiceMongoDB service) {
		System.out.println(service);
		this.service = service;
	}

}
