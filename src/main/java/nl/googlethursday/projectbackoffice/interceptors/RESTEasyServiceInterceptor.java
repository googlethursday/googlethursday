package nl.googlethursday.projectbackoffice.interceptors;

import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;

import nl.googlethursday.projectbackoffice.sessioncontext.SessionContext;

import org.jboss.resteasy.annotations.interception.ServerInterceptor;
import org.jboss.resteasy.core.ResourceMethod;
import org.jboss.resteasy.core.ServerResponse;
import org.jboss.resteasy.spi.Failure;
import org.jboss.resteasy.spi.HttpRequest;
import org.jboss.resteasy.spi.interception.PreProcessInterceptor;

@Provider
@ServerInterceptor
public class RESTEasyServiceInterceptor implements PreProcessInterceptor {

	@Context
	HttpServletRequest servletRequest;

	@Override
	public ServerResponse preProcess(HttpRequest arg0, ResourceMethod resourceMethod) throws Failure, WebApplicationException {
		
		SessionContext.cleanSessionContext();
		SessionContext.getCurrentId().set(UUID.randomUUID().toString());
		SessionContext.getCurrentMethod().set(resourceMethod.getMethod().getName());
		
//		logger.info("Receiving request from: " + servletRequest.getRemoteAddr());
//
//		logger.info("Attempt to invoke method \"" + methodName + "\"");
//
//		if (methodName.equals("calculateAllBasicTrigonometricFunctions")) {
//
//			logger.info("\tCalculate will be performed with parameters:");
//
//			logger.info("\tAdjacent: "
//
//			+ request.getFormParameters().get("adjacent"));
//
//			logger.info("\tOpposite: "
//
//			+ request.getFormParameters().get("opposite"));
//
//			logger.info("\tHypotenusa: "
//
//			+ request.getFormParameters().get("hypotenusa"));
//		}
//		if (methodName.equals("history")) {
//			logger.info("Retrieving history...");
//		}
//		if (methodName.equals("clearAll")) {
//			logger.info("User " + servletRequest.getRemoteUser() + " is trying to clear the history...");
//		}
		return null;
	}

}
