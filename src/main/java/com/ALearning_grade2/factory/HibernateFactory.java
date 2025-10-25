package com.ALearning_grade2.factory;

import com.ALearning_grade2.util.LiquibaseRunner;
import liquibase.exception.LiquibaseException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.SQLException;

public final class HibernateFactory {

    private static final SessionFactory HIBERNATE_SESSION_FACTORY;

    static {
        try {
            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .configure()
                    .build();

            HIBERNATE_SESSION_FACTORY = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();

            LiquibaseRunner.run(HIBERNATE_SESSION_FACTORY);

        } catch (LiquibaseException | SQLException e) {
            e.printStackTrace();
            throw new ExceptionInInitializerError(e);
        } catch (Throwable t) {
            throw new ExceptionInInitializerError(t);
        }
    }

    public static void shutdown() {
        if (HIBERNATE_SESSION_FACTORY != null) {
            HIBERNATE_SESSION_FACTORY.close();
        }
    }

    public static SessionFactory getHibernateSessionFactory() {
        return HIBERNATE_SESSION_FACTORY;
    }

    public static Session openSession() {
        return HIBERNATE_SESSION_FACTORY.openSession();
    }

    private HibernateFactory() {
    }
}
