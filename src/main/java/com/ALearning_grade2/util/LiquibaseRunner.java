package com.ALearning_grade2.util;

import java.sql.Connection;
import java.sql.SQLException;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.hibernate.SessionFactory;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public final class LiquibaseRunner {

    private LiquibaseRunner() {
    }

    public static void run(SessionFactory sessionFactory) throws LiquibaseException, SQLException {

        SessionFactoryImplementor sessionFactoryImplementor = (SessionFactoryImplementor) sessionFactory;
        ConnectionProvider provider = sessionFactoryImplementor.getServiceRegistry()
                .getService(ConnectionProvider.class);

        assert provider != null : "ConnectionProvider не должен быть null";

        try (Connection conn = provider.getConnection()) {

            assert conn != null : "Соединение не должно быть null";

            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            Liquibase liquibase = new Liquibase(
                    "db/liquibase/changelog.xml",
                    new ClassLoaderResourceAccessor(),
                    database);

            liquibase.update(new Contexts(), new LabelExpression());
        }
    }
}
