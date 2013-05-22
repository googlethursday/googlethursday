package nl.googlethursday.projectbackoffice.entity;

import org.bson.types.ObjectId;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Id;


/**
 * Project Entiteit bevat informatie over het project
 * @author rodo
 */
@Entity
public class Project {
	@Id private ObjectId id;
	private String projectNaam;
	private String projectOmschrijving;
	private String projectLeider;
	
	/**
	 * default constructor
	 */
	public Project(){
		
	}
	
	public Project(String projectNaam, String projectOmschrijving,
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
}
