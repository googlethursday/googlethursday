package nl.googlethursday.projectbackoffice.helper;


import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import nl.googlethursday.projectbackoffice.entity.Project;
import nl.googlethursday.projectbackoffice.entity.jaxb.JAXBProject;

/**
 * Static helper methodes
 * @author rodo
 */
public class ProjectBackofficeHelper {

	public static Project JaxbProjectToProjectEntity(JAXBProject jaxbProject) {
		Project project = new Project(jaxbProject.getProjectNaam(), jaxbProject.getProjectOmschrijving(), jaxbProject.getProjectLeider());
		return project;
	}

	public static JAXBProject ProjectToJAXBProject(Project project)
			throws JAXBException {
		if (project == null) {
			throw new JAXBException("kan geen JAXBObject maken");
		} else {
			return convert(project);
		}

	}
	
	/**
	 * converteren van {List &ltProject&gt} naar {List&ltJAXBProject&gt}
	 * @param projectList
	 * @return
	 */
	public static List<JAXBProject> ProjectListToJAXBProjectList(List<Project> projectList){
		ArrayList <JAXBProject> jaxbProjectList = new ArrayList<JAXBProject>();
		for (Project project : projectList) {
			jaxbProjectList.add(convert(project));
		}
		return jaxbProjectList;
	}

	/**
	 * Maak een JAXB element
	 * @param project
	 * @return
	 */
	private static JAXBProject convert(Project project){
		return new JAXBProject(project.getProjectNaam(),
				project.getProjectOmschrijving(),
				project.getProjectLeider());
	}
}