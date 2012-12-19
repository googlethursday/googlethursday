package nl.googlethursday.projectbackoffice.restservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Produces(MediaType.APPLICATION_JSON)
@Path("/service")
public class Restdingetje {

	@GET
@Path("/")
	public String hallo(){
		return "hello";
	}

}
