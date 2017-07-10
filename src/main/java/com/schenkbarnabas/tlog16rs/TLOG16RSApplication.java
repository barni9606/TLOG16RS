package com.schenkbarnabas.tlog16rs;

import com.schenkbarnabas.tlog16rs.core.beans.CreateDatabase;
import com.schenkbarnabas.tlog16rs.resources.TLOG16RSResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import liquibase.exception.LiquibaseException;

import java.sql.SQLException;

public class TLOG16RSApplication extends Application<TLOG16RSConfiguration> {

    public static void main(final String[] args) throws Exception {
        new TLOG16RSApplication().run(args);
    }

    @Override
    public String getName() {
        return "TLOG16RS";
    }

    @Override
    public void initialize(final Bootstrap<TLOG16RSConfiguration> bootstrap) {
        // TODO: application initialization
    }

    @Override
    public void run(final TLOG16RSConfiguration configuration,
                    final Environment environment) throws SQLException, LiquibaseException {
        new CreateDatabase(configuration);
        environment.jersey().register(new TLOG16RSResource());
    }

}
