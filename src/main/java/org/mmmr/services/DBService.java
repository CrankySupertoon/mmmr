package org.mmmr.services;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Example;
import org.mmmr.Dependency;
import org.mmmr.Mod;

@SuppressWarnings("deprecation")
public class DBService {
    private static DBService instance = null;

    public static DBService getInstance(File dbdir) throws IOException, ClassNotFoundException {
        if (instance == null)
            instance = new DBService(dbdir);
        return instance;
    }

    private Session session = null;

    public DBService(File dbdir) throws IOException, ClassNotFoundException {
        AnnotationConfiguration configuration = new AnnotationConfiguration();
        String packagename = Mod.class.getPackage().getName();
        BufferedReader br = new BufferedReader(new InputStreamReader(DBService.class.getClassLoader().getResourceAsStream(
                packagename.replace('.', '/') + "/jaxb.index")));
        String line;
        while ((line = br.readLine()) != null) {
            Class<?> persistentClass = Class.forName(packagename + "." + line);
            configuration.addAnnotatedClass(persistentClass);
            System.out.println(persistentClass.getName());
        }
        configuration.addAnnotatedClass(Dependency.class);
        System.out.println(Dependency.class.getName());
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.username", "mmmr");
        properties.setProperty("hibernate.connection.password", "mmmr");
        properties.setProperty("hibernate.hbm2ddl.auto", "update");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect");
        properties.setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver");
        String url = "jdbc:derby:" + dbdir.getAbsolutePath() + ";create=" + !dbdir.exists();
        System.out.println(url);
        properties.setProperty("hibernate.connection.url", url);
        configuration.setProperties(properties);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();
    }

    public <T> T save(T object) {
        Transaction tx = session.beginTransaction();
        session.persist(object);
        session.flush();
        tx.commit();

        return object;
    }

    @SuppressWarnings("unchecked")
    public <T> T getOrCreate(T object) {
        Example example = Example.create(object).excludeZeroes() // exclude zero valued properties
                .ignoreCase() // perform case insensitive string comparisons
                .enableLike(); // use like for string comparisons
        Class<T> type = (Class<T>) object.getClass();
        List<T> results = session.createCriteria(type).add(example).list();
        if (results.size() == 0)
            return object;
        if (results.size() > 1)
            throw new RuntimeException("more than 1 result");
        return results.get(0);
    }
}
