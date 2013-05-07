package nl.googlethursday.projectbackoffice.service.mongodb.dao;


import nl.googlethursday.projectbackoffice.entity.Project;

import org.bson.types.ObjectId;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.Mongo;

/**
 * ProjectDAO
 * @author rodo
 *
 */
public class ProjectDAO extends BasicDAO<Project, ObjectId>{

	protected ProjectDAO(Mongo mongo, Morphia morphia, String dbName) {
		super(mongo, morphia, dbName);
	}
}
