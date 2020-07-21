package fr.dila.reponses.core.ldap;

import java.util.Arrays;
import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.directory.Session;

import fr.dila.reponses.core.ReponsesRepositoryTestCase;
import fr.dila.st.api.constant.STSchemaConstant;

/**
 * Vérifie le bon fonctionctionnement des répertoires LDAP.
 * 
 * @author jtremeaux
 */
public class TestLDAPSession extends ReponsesRepositoryTestCase {

	public void testGetUserEntry() throws Exception {
		Session session = getLDAPDirectory(USER_DIRECTORY).getSession();
		assertNotNull(session);

		try {
			DocumentModel entry = session.getEntry("Administrator");
			assertNotNull(entry);
			assertEquals("Administrator", entry.getId());
			assertEquals("Manager", entry.getProperty(STSchemaConstant.USER_SCHEMA, "lastName"));

			assertEquals("Administrator", entry.getProperty(STSchemaConstant.USER_SCHEMA, "firstName"));
			assertNull(entry.getProperty(STSchemaConstant.USER_SCHEMA, "password"));

			List<?> val = (List<?>) entry.getProperty(STSchemaConstant.USER_SCHEMA, "employeeType");
			assertTrue(val.isEmpty());

			DocumentModel entry2 = session.getEntry("user1");
			assertNotNull(entry2);
			assertEquals("user1", entry2.getId());
			assertNull(entry2.getProperty(STSchemaConstant.USER_SCHEMA, "password"));

			try {
				entry2.getProperty(STSchemaConstant.USER_SCHEMA, "userPassword");
				fail();
			} catch (ClientException ce) {
				// expected
			}
			assertEquals(Arrays.asList("Boss"), entry2.getProperty(STSchemaConstant.USER_SCHEMA, "employeeType"));

			DocumentModel entry3 = session.getEntry("UnexistingEntry");
			assertNull(entry3);
		} finally {
			session.close();
		}
	}

	public void testGetPosteEntry() throws Exception {
		/*
		 * Session session = getLDAPDirectory(POSTE_DIRECTORY).getSession(); assertNotNull(session); //TODO monter les
		 * schémas LDAP UT et Poste try { DocumentModel entry = session.getEntry("Premier Ministre386");
		 * assertNotNull(entry); assertEquals("Premier Ministre386", entry.getId()); //assertEquals("Manager",
		 * entry.getProperty(ReponsesConstant.ORGANIGRAMME_POSTE_SCHEMA, "lastName")); assertEquals("Administrator",
		 * entry.getProperty(STSchemaConstant.USER_SCHEMA, "firstName"));
		 * assertNull(entry.getProperty(STSchemaConstant.USER_SCHEMA, "password")); List<?> val = (List<?>)
		 * entry.getProperty(STSchemaConstant.USER_SCHEMA, "employeeType"); assertTrue(val.isEmpty()); DocumentModel
		 * entry2 = session.getEntry("user1"); assertNotNull(entry2); assertEquals("user1", entry2.getId());
		 * assertNull(entry2.getProperty(STSchemaConstant.USER_SCHEMA, "password")); try {
		 * entry2.getProperty(STSchemaConstant.USER_SCHEMA, "userPassword"); fail(); } catch (ClientException ce) { //
		 * expected } assertEquals(Arrays.asList("Boss"), entry2.getProperty(STSchemaConstant.USER_SCHEMA,
		 * "employeeType")); DocumentModel entry3 = session.getEntry("UnexistingEntry"); assertNull(entry3); } finally {
		 * session.close(); }
		 */
	}

}
