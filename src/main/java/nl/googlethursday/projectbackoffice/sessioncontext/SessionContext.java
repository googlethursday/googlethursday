package nl.googlethursday.projectbackoffice.sessioncontext;

/**
 * Sessioncontext 
 * @author rodo
 *
 */
public class SessionContext {

	private static ThreadLocal<String> currentId = new ThreadLocal<String>();
	
	private static ThreadLocal<String> currentMethod = new ThreadLocal<String>();
	
	private static ThreadLocal<String> currentSleutel = new ThreadLocal<String>();
	
	/**
	 * Ophalen id uit sessioncontext
	 * @return
	 */
	public static ThreadLocal<String> getCurrentId(){
		return currentId;
	}
	
	public static ThreadLocal<String> getCurrentMethod(){
		return currentMethod;
	}
	
	public static ThreadLocal<String> getSleutel(){
		return currentSleutel;
	}
	
	public static void cleanSessionContext(){
		currentId=new ThreadLocal<String>();
		currentMethod=new ThreadLocal<String>();
		currentSleutel=new ThreadLocal<String>();
	}
}
