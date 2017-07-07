package com.schenkbarnabas.tlog16rs.core.beans;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.EbeanServerFactory;
import com.avaje.ebean.config.DataSourceConfig;
import com.avaje.ebean.config.ServerConfig;
import com.schenkbarnabas.tlog16rs.TLOG16RSConfiguration;
import com.schenkbarnabas.tlog16rs.entities.Task;
import com.schenkbarnabas.tlog16rs.entities.TimeLogger;
import com.schenkbarnabas.tlog16rs.entities.WorkDay;
import com.schenkbarnabas.tlog16rs.entities.WorkMonth;
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

    public CreateDatabase(TLOG16RSConfiguration tlog16RSConfiguration) {
        initDataSourceConfig(tlog16RSConfiguration);
        initServerConfig(tlog16RSConfiguration);
        try {
            updateSchema(tlog16RSConfiguration);
        } catch (SQLException | LiquibaseException e) {
            e.printStackTrace();
        }
        ebeanServer = EbeanServerFactory.create(serverConfig);
    }

    private void initDataSourceConfig(TLOG16RSConfiguration tlog16RSConfiguration){
        this.dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setDriver(tlog16RSConfiguration.getDbDriver());
        dataSourceConfig.setUrl(tlog16RSConfiguration.getDbUrl());// timelogger will be the name of the database
        dataSourceConfig.setUsername(tlog16RSConfiguration.getDbUsername());
        dataSourceConfig.setPassword(tlog16RSConfiguration.getDbPassword());
    }

    private void initServerConfig(TLOG16RSConfiguration tlog16RSConfiguration){
        this.serverConfig = new ServerConfig();
        serverConfig.setName(tlog16RSConfiguration.getDbConfigName());
        serverConfig.setDdlGenerate(false);
        serverConfig.setDdlRun(false); // if the last 2 property is true, the database will be generated automatically
        serverConfig.setRegister(true);
        serverConfig.setDataSourceConfig(dataSourceConfig);
        serverConfig.addClass(TimeLogger.class);
        serverConfig.addClass(WorkMonth.class);
        serverConfig.addClass(WorkDay.class);
        serverConfig.addClass(Task.class);
        serverConfig.setDefaultServer(true);

    }
    private void updateSchema(TLOG16RSConfiguration tlog16RSConfiguration) throws SQLException, LiquibaseException {
        Connection connection = getConnection();
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
        liquibase.update("");
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(System.getProperty("dbUrl", "jdbc:mariadb://127.0.0.1:9001/timelogger"),
                System.getProperty("dbUsername","timelogger"), System.getProperty("dbPassword", "633Ym2aZ5b9Wtzh4EJc4pANx"));
    }
}
