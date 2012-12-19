package nl.googlethursday.projectbackoffice.webservice;

import javax.jws.WebService;

import nl.googlethursday.projectbackoffice.entity.Project;

@WebService
public class ProjectWebservice {

	public Project helloService(){
	 return new Project("naam","omschrijving","pl");
	}

}
