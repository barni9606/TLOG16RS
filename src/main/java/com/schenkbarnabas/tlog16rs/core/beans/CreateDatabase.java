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
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by bschenk on 7/5/17.
 */
@Slf4j
public class CreateDatabase {
    private DataSourceConfig dataSourceConfig;
    private ServerConfig serverConfig;

    public CreateDatabase(TLOG16RSConfiguration tlog16RSConfiguration) {
        initDataSourceConfig(tlog16RSConfiguration);
        initServerConfig(tlog16RSConfiguration);
        try {
            updateSchema(tlog16RSConfiguration);
        } catch (SQLException | LiquibaseException e) {
            log.error(e.getClass().toString() + ": " +  e.getMessage());
        }
        EbeanServerFactory.create(serverConfig);
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
        Connection connection = getConnection(tlog16RSConfiguration);
        Liquibase liquibase = new Liquibase("migrations.xml", new ClassLoaderResourceAccessor(), new JdbcConnection(connection));
        liquibase.update("");
    }

    private Connection getConnection(TLOG16RSConfiguration tlog16RSConfiguration) throws SQLException {
        return DriverManager.getConnection(tlog16RSConfiguration.getDbUrl(),
                tlog16RSConfiguration.getDbUsername(), tlog16RSConfiguration.getDbPassword());
    }
}
