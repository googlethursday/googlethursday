package nl.googlethursday.projectbackoffice.web.service.restservice.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interceptor tbv onderscheppen rest calls
 * 
 * @author rodo TODO werkt nog niet
 */
@Provider
@ServerInterceptor
public class RestInterceptors implements PreProcessInterceptor {
	private final static Logger logger = LoggerFactory.getLogger(RestInterceptors.class);

	// Getting the request as a context
	@Context
	HttpServletRequest request;

	@Override
	public ServerResponse preProcess(HttpRequest arg0, ResourceMethod arg1) throws Failure, WebApplicationException {
		logger.debug("Thread:" + Thread.currentThread().getId() + "URL:" + request.getRequestURL().toString());
		return null;
	}
}