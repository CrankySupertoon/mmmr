package org.mmmr.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.mmmr.Dependency;
import org.mmmr.MC;
import org.mmmr.MCFile;
import org.mmmr.Mod;
import org.mmmr.ModPack;
import org.mmmr.PersistentObject;
import org.mmmr.Resource;

/**
 * service uses Hibernate to interact with Derby database
 * 
 * @author Jurgen
 * 
 * @see http://www.hibernate.org/
 * @see http://db.apache.org/derby/
 */
public class DBService {
    /** singleton */
    private static DBService instance = null;

    /**
     * gets singleton {@link DBService}
     */
    public static DBService getInstance(Config cfg) throws IOException, ClassNotFoundException {
        if (DBService.instance == null) {
            DBService.instance = new DBService(cfg);
        }
        return DBService.instance;
    }

    public static String getNamedQuery(String name) {
        try {
            String res = DBService.class.getPackage().getName().replace('.', '/') + "/hql/" + name + ".hql";
            InputStream resourceAsStream = DBService.class.getClassLoader().getResourceAsStream(res);
            ExceptionAndLogHandler.log(res + " >> " + resourceAsStream);
            return new String(UtilityMethods.read(resourceAsStream));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    protected org.hibernate.Session session = null;

    /**
     * creates and configures database (Apache Derby)
     */
    public DBService(Config cfg) throws IOException, ClassNotFoundException {
        System.setProperty("derby.stream.error.file", new File(cfg.getLogs(), "derby.log").getAbsolutePath()); //$NON-NLS-1$ //$NON-NLS-2$

        org.hibernate.cfg.Configuration configuration = new org.hibernate.cfg.Configuration();
        configuration.addAnnotatedClass(Dependency.class);
        configuration.addAnnotatedClass(MCFile.class);
        configuration.addAnnotatedClass(MC.class);
        configuration.addAnnotatedClass(Resource.class);
        configuration.addAnnotatedClass(Mod.class);
        configuration.addAnnotatedClass(ModPack.class);
        Properties properties = new Properties();
        properties.setProperty("hibernate.connection.username", "mmmr"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.setProperty("hibernate.connection.password", "mmmr"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.setProperty("hibernate.hbm2ddl.auto", "update"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.setProperty("hibernate.show_sql", "false"); //$NON-NLS-1$ //$NON-NLS-2$
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.DerbyDialect"); //$NON-NLS-1$ //$NON-NLS-2$
        // embedded server can only be opened by 1 program
        properties.setProperty("hibernate.connection.driver_class", "org.apache.derby.jdbc.EmbeddedDriver"); //$NON-NLS-1$ //$NON-NLS-2$
        String url = "jdbc:derby:" + cfg.getDbdir().getAbsolutePath() + ";create=" + !cfg.getDbdir().exists(); //$NON-NLS-1$ //$NON-NLS-2$
        ExceptionAndLogHandler.log(url);
        properties.setProperty("hibernate.connection.url", url); //$NON-NLS-1$
        configuration.setProperties(properties);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        this.session = sessionFactory.openSession();
    }

    public DBService(org.hibernate.Session session) throws IOException, ClassNotFoundException {
        this.session = session;
    }

    /**
     * returns all objects saved to database of given type
     */
    public <T extends PersistentObject> List<T> all(Class<T> clazz) {
        return this.hql("from " + clazz.getName(), clazz);
    }

    public <T extends PersistentObject> void delete(T object) {
        if (object.getId() != null) {
            Transaction tx = this.session.beginTransaction();
            this.session.delete(object);
            this.session.flush();
            tx.commit();
        }
    }

    /**
     * returns an object saved from databse or null
     */
    public <T extends PersistentObject> T get(T object) {
        return this.getOrCreate(object, false);
    }

    /**
     * finds all objects that have matching properties (like is used), not case sensitive
     */
    @SuppressWarnings("unchecked")
    public <T extends PersistentObject> List<T> getAll(T object) {
        this.session.flush();

        Example example = Example.create(object).excludeZeroes() // exclude zero valued properties
                .ignoreCase() // perform case insensitive string comparisons
                .enableLike(); // use like for string comparisons
        Class<T> type = (Class<T>) object.getClass();
        List<T> results = this.session.createCriteria(type).add(example).list();
        return results;
    }

    /**
     * returns object from database or a new one when not saved to database yet
     */
    public <T extends PersistentObject> T getOrCreate(T object) {
        return this.getOrCreate(object, true);
    }

    /**
     * returns object from database or a new one when not saved to database yet
     */
    @SuppressWarnings("unchecked")
    private <T extends PersistentObject> T getOrCreate(T object, boolean create) {
        this.session.flush();

        Example example = Example.create(object).excludeZeroes() // exclude zero valued properties
                .ignoreCase() // perform case insensitive string comparisons
                .enableLike(); // use like for string comparisons
        Class<T> type = (Class<T>) object.getClass();
        List<T> results = this.session.createCriteria(type).add(example).list();
        if (results.size() == 0) {
            return create ? object : null;
        }
        if (results.size() > 1) {
            throw new RuntimeException(Messages.getString("DBService.morethan1")); //$NON-NLS-1$
        }
        return results.get(0);
    }

    /**
     * execute HQL, returns list (never null)
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public <T> List<T> hql(String hql, Class<T> returnType, Map<String, Object> params) {
        Query createQuery = this.session.createQuery(hql);
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            createQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return createQuery.list();
    }

    /**
     * execute HQL, returns list (never null)
     */
    @SuppressWarnings({ "unchecked", "unused" })
    public <T> List<T> hql(String hql, Class<T> returnType, Object... params) {
        Query createQuery = this.session.createQuery(hql);
        for (int i = 0; i < params.length; i++) {
            createQuery.setParameter(i, params[i]);
        }
        return createQuery.list();
    }

    /**
     * execute HQL, returns singleton or null when not exits or throws exception (message 'more_than_1_result') when more than 1 result
     */
    public <T> T hql1(String hql, Class<T> returnType, Map<String, Object> params) {
        List<T> list = this.hql(hql, returnType, params);
        if (list.size() == 0) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new RuntimeException("more_than_1_result");
    }

    /**
     * execute HQL, returns singleton or null when not exits or throws exception (message 'more_than_1_result') when more than 1 result
     */
    public <T> T hql1(String hql, Class<T> returnType, Object... params) {
        List<T> list = this.hql(hql, returnType, params);
        if (list.size() == 0) {
            return null;
        }
        if (list.size() == 1) {
            return list.get(0);
        }
        throw new RuntimeException("more_than_1_result");
    }

    /**
     * refresh object as found in database
     */
    @SuppressWarnings("unchecked")
    public <T extends PersistentObject> T refresh(T object) {
        this.session.evict(object);
        return (T) this.session.load(object.getClass(), object.getId());
    }

    /**
     * saves an object (first and consecutive saves)
     */
    public <T extends PersistentObject> T save(T object) {
        Transaction tx = this.session.beginTransaction();
        if (object.getId() == null) {
            this.session.save(object);
        } else {
            this.session.update(object);
        }
        this.session.flush();
        tx.commit();
        return object;
    }
}
