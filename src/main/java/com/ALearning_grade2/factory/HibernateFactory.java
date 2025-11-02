package com.ALearning_grade2.factory;

import com.ALearning_grade2.util.LiquibaseRunner;
import liquibase.exception.LiquibaseException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.sql.SQLException;

/**
 * Фабрика для создания и управления Hibernate SessionFactory.
 * Инициализирует фабрику сессий и запускает миграции базы данных через Liquibase.
 */
public final class HibernateFactory {

    private static final SessionFactory HIBERNATE_SESSION_FACTORY;
    private static final boolean IS_TEST_MODE;

    static {
        try {
            IS_TEST_MODE = Boolean.parseBoolean(System.getProperty("test.mode", "false"));

            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder();

            if (IS_TEST_MODE) {
                // Используем тестовую конфигурацию
                registryBuilder.configure("hibernate-test.cfg.xml");
            } else {
                // Используем основную конфигурацию
                registryBuilder.configure();
            }

            StandardServiceRegistry registry = registryBuilder.build();

            HIBERNATE_SESSION_FACTORY = new MetadataSources(registry)
                    .buildMetadata()
                    .buildSessionFactory();

            // Запускаем Liquibase только в production режиме
            if (!IS_TEST_MODE) {
                LiquibaseRunner.run(HIBERNATE_SESSION_FACTORY);
            }

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

    public static boolean isTestMode() {
        return IS_TEST_MODE;
    }

    private HibernateFactory() {
    }
}
