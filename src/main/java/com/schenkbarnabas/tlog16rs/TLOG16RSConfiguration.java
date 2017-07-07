package com.schenkbarnabas.tlog16rs;

import io.dropwizard.Configuration;
import lombok.NoArgsConstructor;

@lombok.Getter
@lombok.Setter
@NoArgsConstructor
public class TLOG16RSConfiguration extends Configuration {
    private String dbUrl;
    private String dbConfigName;
    private String dbUsername;
    private String dbPassword;
    private String dbDriver;
}
