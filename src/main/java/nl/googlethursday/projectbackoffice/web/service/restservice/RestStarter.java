package nl.googlethursday.projectbackoffice.web.service.restservice;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
/**
 * ResyEasy methode om rest servlet in de lucht te krijgen
 * werkt dus NIET met een web.xml servlet-methode zoals Jersey dat doet
 * 
 * @author rodo
 *
 */
@ApplicationPath("rest")
public class RestStarter extends Application{

}

