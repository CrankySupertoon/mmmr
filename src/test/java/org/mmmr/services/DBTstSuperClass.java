package org.mmmr.services;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.mmmr.Dependency;
import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModPack;
import org.mmmr.Resource;
import org.mmmr.services.impl.ExceptionAndLogHandlerLog4j;

public class DBTstSuperClass {
    protected static DBService dbService;

    protected static org.hibernate.Session session;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        ExceptionAndLogHandlerLog4j.noFileLogging();

        if (DBTstSuperClass.dbService == null) {
            org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
            configuration.addAnnotatedClass(Dependency.class);
            configuration.addAnnotatedClass(MCFile.class);
            configuration.addAnnotatedClass(MC.class);
            configuration.addAnnotatedClass(Resource.class);
            configuration.addAnnotatedClass(Mod.class);
            configuration.addAnnotatedClass(ModPack.class);
            Properties properties = new Properties();
            properties.setProperty("hibernate.connection.username", "testing"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("hibernate.connection.password", "testing"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("hibernate.hbm2ddl.auto", "update"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("hibernate.show_sql", "false"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.HSQLDialect"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("hibernate.connection.driver_class", "org.hsqldb.jdbcDriver"); //$NON-NLS-1$ //$NON-NLS-2$
            properties.setProperty("hibernate.connection.url", "jdbc:hsqldb:mem:testdb"); //$NON-NLS-1$ //$NON-NLS-2$
            configuration.setProperties(properties);
            SessionFactory sessionFactory = configuration.buildSessionFactory();
            DBTstSuperClass.session = sessionFactory.openSession();
            DBTstSuperClass.dbService = new DBService(DBTstSuperClass.session);
        }
    }

    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        //
    }

    @Before
    public void setUp() throws Exception {
        MC p = new MC("mc1");
        for (int i = 1; i <= 10; i++) {
            MCFile f = new MCFile("class" + i + ".class");
            p.addFile(f);
        }
        DBTstSuperClass.dbService.session.persist(p);
        DBTstSuperClass.dbService.session.flush();
    }

    @After
    public void tearDown() throws Exception {
        DBTstSuperClass.dbService.session.createQuery("delete from MCFile").executeUpdate();
        DBTstSuperClass.dbService.session.createQuery("delete from Dependency").executeUpdate();
        DBTstSuperClass.dbService.session.createQuery("delete from Resource").executeUpdate();
        DBTstSuperClass.dbService.session.createQuery("delete from ModPack").executeUpdate();
        DBTstSuperClass.dbService.session.createQuery("delete from Mod").executeUpdate();
        DBTstSuperClass.dbService.session.createQuery("delete from MC").executeUpdate();
    }
}
