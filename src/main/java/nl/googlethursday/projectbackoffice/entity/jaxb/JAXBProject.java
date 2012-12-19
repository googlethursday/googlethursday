package nl.googlethursday.projectbackoffice.entity.jaxb;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * JAXB representatie van {@link nl.googlethursday.projectbackoffice.entity.Project}
 * @author rodo
 *
 */
@XmlRootElement
public class JAXBProject {

	private String projectNaam;
	private String projectOmschrijving;
	private String projectLeider;

	
	/**
	 * Public constructor, nodig voor JSON-->JAXB conversie!
	 */
	public JAXBProject() {

	}
	
	public JAXBProject(String projectNaam, String projectOmschrijving,
			String projectLeider) {
		super();
		this.projectNaam = projectNaam;
		this.projectOmschrijving = projectOmschrijving;
		this.projectLeider = projectLeider;
	}
	
	
	public String getProjectNaam() {
		return projectNaam;
	}
	public void setProjectNaam(String projectNaam) {
		this.projectNaam = projectNaam;
	}
	public String getProjectOmschrijving() {
		return projectOmschrijving;
	}
	public void setProjectOmschrijving(String projectOmschrijving) {
		this.projectOmschrijving = projectOmschrijving;
	}
	public String getProjectLeider() {
		return projectLeider;
	}
	public void setProjectLeider(String projectLeider) {
		this.projectLeider = projectLeider;
	}

	@Override
	public String toString() {
		return "JAXBProject [projectNaam=" + projectNaam + ", projectOmschrijving=" + projectOmschrijving
				+ ", projectLeider=" + projectLeider + "]";
	}

}
