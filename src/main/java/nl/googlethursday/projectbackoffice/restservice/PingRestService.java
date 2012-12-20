package nl.googlethursday.projectbackoffice.restservice;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Pingservice tbv testdoeleinden
 * 
 * @author rodo
 * 
 */
@Produces(MediaType.APPLICATION_JSON)
@Path("/service")
public class PingRestService {

	/**
	 * Ping methode
	 * 
	 * @return
	 */

	@GET
	@Path("/")
	public String ping() {
		return "hello ping!";
	}

}
