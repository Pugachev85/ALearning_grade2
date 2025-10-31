package com.ALearning_grade2.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

/**
 * Конфигурационный класс для инициализации Hibernate SessionFactory.
 * Обеспечивает создание и закрытие фабрики сессий для работы с БД.
 */

public class HibernateConfig {
    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            StandardServiceRegistry registry = null;
            try {
                registry = new StandardServiceRegistryBuilder()
                        .configure()
                        .build();
                sessionFactory = new MetadataSources(registry)
                        .buildMetadata()
                        .buildSessionFactory();
            } catch (Exception e) {
                System.err.println("Ошибка при создании SessionFactory: " + e);
                if (registry != null) {
                    StandardServiceRegistryBuilder.destroy(registry);
                }
            }
        }
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
        }
    }
}
