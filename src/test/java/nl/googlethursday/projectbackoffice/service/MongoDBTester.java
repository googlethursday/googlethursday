package nl.googlethursday.projectbackoffice.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.UnknownHostException;

import nl.googlethursday.projectbackoffice.entity.Project;
import nl.googlethursday.projectbackoffice.service.mongodb.MongoDBService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoDBTester {

	// setup mongodb
	int port = 12345;
	String host = "localhost";

	MongodConfig mongodConfig;
	MongodExecutable mongodExecutable = null;
	MongodProcess process;
	Mongo mongoConnection;

	// setup te testen
	MongoDBService teTestenService;

	@SuppressWarnings("deprecation")
	@Before
	public void beforeEach() {

		try {
			mongodConfig = new MongodConfig(Version.V2_2_0, port, Network.localhostIsIPv6());
			MongodStarter starter = MongodStarter.getDefaultInstance();
			mongodExecutable = starter.prepare(mongodConfig);
			process = mongodExecutable.start();

			mongoConnection = new Mongo("localhost", port);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
		}

		// MongoDBService.connection = mongoConnection;

	}

	@After
	public void aferEach() {
		if (this.process != null) {
			this.process.stop();
			this.mongodExecutable.stop();
		}
	}

	/**
	 * test initieel
	 */
	@Test
	public void testGetProjectsInitieel() {

		teTestenService = new MongoDBService(mongoConnection);

		// test
		assertNull(teTestenService.getProjects());
	}

	/**
	 * test met gevulde projecten
	 */
	@Test
	public void testGetProjects() {
		teTestenService = new MongoDBService(mongoConnection);

		// MongoDBUtil.db.createCollection(MongoDBService.COLLECTIONNAAM, new
		// BasicDBObject());

		// 1
		Project p0 = new Project("naam0", "omschrijving0", "pl0");
		teTestenService.opslaanProject(p0);

		// test
		assertEquals(teTestenService.getProjects().size(), 1);
		assertTrue(testEquals(teTestenService.getProjects().get(0), p0));

		// 2
		Project p1 = new Project("naam1", "omschrijving1", "pl1");
		teTestenService.opslaanProject(p1);

		// test
		assertEquals(teTestenService.getProjects().size(), 2);
		assertTrue(testEquals(teTestenService.getProjects().get(0), p0));
		assertTrue(testEquals(teTestenService.getProjects().get(1), p1));

		// nog steeds 2
		Project p3 = new Project("naam0", "omschrijving1", "pl1");
		teTestenService.opslaanProject(p3);
		assertEquals(teTestenService.getProjects().size(), 2);
		assertTrue(testEquals(teTestenService.getProjects().get(0), p3));
		assertTrue(testEquals(teTestenService.getProjects().get(1), p1));

		// nu 3
		Project p4 = new Project("naam3", "omschrijving3", "pl3");
		teTestenService.opslaanProject(p4);

		assertEquals(teTestenService.getProjects().size(), 3);
		assertTrue(testEquals(teTestenService.getProjects().get(0), p3));

	}

	/**
	 * test ophalen specifiek project
	 * 
	 */
	@Test
	public void testGetProject() {
		teTestenService = new MongoDBService(mongoConnection);
		// MongoDBUtil.db.createCollection(MongoDBService.COLLECTIONNAAM, new
		// BasicDBObject());

		// nu testen niet aanwezig
		Project gevondenProject = teTestenService.getProject("naam2");
		assertNull(gevondenProject);

		Project p1 = new Project("naam1", "omschrijving1", "pl1");
		teTestenService.opslaanProject(p1);
		Project p2 = new Project("naam2", "omschrijving2", "pl2");
		teTestenService.opslaanProject(p2);

		// nu testen wél aanwezig
		gevondenProject = teTestenService.getProject("naam2");
		assertNotNull(gevondenProject);
		assertTrue(testEquals(p2, gevondenProject));
	}

	@Test
	public void testZoekProject() {

		teTestenService = new MongoDBService(mongoConnection);

		// setup
		Project p1 = new Project("naam1", "omschrijving1", "pl1");
		teTestenService.opslaanProject(p1);
		Project p2 = new Project("naam2", "omschrijving2", "pl2");
		teTestenService.opslaanProject(p2);
		Project p3 = new Project("naam3", "omschrijving3", "pl3");
		teTestenService.opslaanProject(p3);
		Project p4 = new Project("naam4", "omschrijving4", "pl4");
		teTestenService.opslaanProject(p4);
		Project p5 = new Project("naam44", "omschrijving4", "pl4");
		teTestenService.opslaanProject(p5);

		// testen
		assertEquals(teTestenService.zoekProject("naam").size(), 5);
		assertEquals(teTestenService.zoekProject("naam1").size(), 1);
		assertEquals(teTestenService.zoekProject("naam4").size(), 2);

	}

	/**
	 * helper tbv compare
	 * 
	 * @param pOud
	 * @param pNieuw
	 * @return
	 */
	private boolean testEquals(Project pOud, Project pNieuw) {
		if (pOud != null || pNieuw != null) {
			if (!(pOud.getProjectLeider().equals(pNieuw.getProjectLeider()))) {
				return false;
			}
			if (!(pOud.getProjectNaam().equals(pNieuw.getProjectNaam()))) {
				return false;
			}

			if (!(pOud.getProjectOmschrijving().equals(pNieuw.getProjectOmschrijving()))) {
				return false;
			}
			return true;
		}
		return false;
	}
}
