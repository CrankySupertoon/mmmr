package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Example;
import org.mmmr.Dependency;
import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModDependency;
import org.mmmr.ModPack;
import org.mmmr.PersistentObject;
import org.mmmr.Resource;

/**
 * @author Jurgen
 */
@SuppressWarnings("deprecation")
public class DBService {
    private static DBService instance = null;

    public static DBService getInstance(Config cfg) throws IOException, ClassNotFoundException {
	if (instance == null)
	    instance = new DBService(cfg.getDbdir());
	return instance;
    }

    private Session session = null;

    public DBService(File dbdir) throws IOException, ClassNotFoundException {
	System.setProperty("derby.stream.error.file", new File(dbdir, "derby.log").getAbsolutePath());

	AnnotationConfiguration configuration = new AnnotationConfiguration();
	configuration.addAnnotatedClass(Dependency.class);
	configuration.addAnnotatedClass(MCFile.class);
	configuration.addAnnotatedClass(MC.class);
	configuration.addAnnotatedClass(ModDependency.class);
	configuration.addAnnotatedClass(Resource.class);
	configuration.addAnnotatedClass(Mod.class);
	configuration.addAnnotatedClass(ModPack.class);
	Properties properties = new Properties();
	properties.setProperty("hibernate.connection.username", "mmmr");
	properties.setProperty("hibernate.connection.password", "mmmr");
	properties.setProperty("hibernate.hbm2ddl.auto", "update");
	properties.setProperty("hibernate.show_sql", "false");
	properties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
	// embedded server can only be opened by 1 program
	properties.setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");
	String url = "jdbc:derby:" + dbdir.getAbsolutePath() + ";create=" + !dbdir.exists();
	System.out.println(url);
	properties.setProperty("hibernate.connection.url", url);
	configuration.setProperties(properties);
	SessionFactory sessionFactory = configuration.buildSessionFactory();
	session = sessionFactory.openSession();
    }

    public void flush() {
	session.flush();
    }

    public <T extends PersistentObject> T get(T object) {
	return getOrCreate(object, false);
    }

    @SuppressWarnings("unchecked")
    public <T extends PersistentObject> List<T> getAll(T object) {
	session.flush();

	Example example = Example.create(object).excludeZeroes() // exclude zero valued properties
		.ignoreCase() // perform case insensitive string comparisons
		.enableLike(); // use like for string comparisons
	Class<T> type = (Class<T>) object.getClass();
	List<T> results = session.createCriteria(type).add(example).list();
	return results;
    }

    public <T extends PersistentObject> T getOrCreate(T object) {
	return getOrCreate(object, true);
    }

    @SuppressWarnings("unchecked")
    private <T extends PersistentObject> T getOrCreate(T object, boolean create) {
	session.flush();

	Example example = Example.create(object).excludeZeroes() // exclude zero valued properties
		.ignoreCase() // perform case insensitive string comparisons
		.enableLike(); // use like for string comparisons
	Class<T> type = (Class<T>) object.getClass();
	List<T> results = session.createCriteria(type).add(example).list();
	if (results.size() == 0)
	    return create ? object : null;
	if (results.size() > 1)
	    throw new RuntimeException("more than 1 result");
	return results.get(0);
    }

    public <T extends PersistentObject> T save(T object) {
	Transaction tx = session.beginTransaction();
	if (object.getId() == null)
	    session.save(object);
	else
	    session.update(object);
	session.flush();
	tx.commit();

	return object;
    }
}
