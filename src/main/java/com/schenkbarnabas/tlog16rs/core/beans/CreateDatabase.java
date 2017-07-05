package com.schenkbarnabas.tlog16rs.core.beans;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.schenkbarnabas.tlog16rs.entities.TestEntity;
import liquibase.Liquibase;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by bschenk on 7/5/17.
 */
public class CreateDatabase {
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;
    private EbeanServer ebeanServer;

    public CreateDatabase() {
        try {
            updateSchema();
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
        }

        this.serverConfig = new ServerConfig();

        dataSourceConfig.setUrl(System.getProperty("dbUrl", "jdbc:mariadb://127.0.0.1:9001/timelogger"));// timelogger will be the name of the database: jdbc:mariadb://127.0.0.1:9001/timelogger
        dataSourceConfig.setUsername(System.getProperty("dbUsername", "timelogger")); // timelogger
        dataSourceConfig.setPassword(System.getProperty("dbPassword", "633Ym2aZ5b9Wtzh4EJc4pANx")); // 633Ym2aZ5b9Wtzh4EJc4pANx

        serverConfig.setName(System.getProperty("dbConfigName", "timelogger")); // timelogger
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false); // if the last 2 property is true, the database will be generated automatically
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TestEntity.class);// (Now it is only the TestEntity, but here you can add the list of the annotated classes)
        serverConfig.setDefaultServer(true);

        ebeanServer = EbeanServerFactory.create(serverConfig);
    }

    private void updateSchema() throws SQLException, LiquibaseException {
        this.dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(System.getProperty("dbDriver", "org.mariadb.jdbc.Driver")); // org.mariadb.jdbc.Driver
        Connection connection = getConnection();
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
        liquibase.update("");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("dbUrl", "jdbc:mariadb://127.0.0.1:9001/timelogger"),
                System.getProperty("dbUsername","timelogger"), System.getProperty("dbPassword", "633Ym2aZ5b9Wtzh4EJc4pANx"));
    }
}
