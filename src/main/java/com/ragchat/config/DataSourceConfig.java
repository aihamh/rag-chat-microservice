package com.ragchat.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URISyntaxException;

@Slf4j
@Configuration
public class DataSourceConfig {

    @Value("${DATABASE_URL:}")
    private String databaseUrl;

    @Value("${PGHOST:localhost}")
    private String pgHost;

    @Value("${PGPORT:5432}")
    private int pgPort;

    @Value("${PGDATABASE:ragchat}")
    private String pgDatabase;

    @Value("${PGUSER:postgres}")
    private String pgUser;

    @Value("${PGPASSWORD:}")
    private String pgPassword;

    @Bean
    public DataSource dataSource() throws URISyntaxException {
        HikariConfig config = new HikariConfig();

        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            if (databaseUrl.startsWith("postgresql://") || databaseUrl.startsWith("postgres://")) {
                URI dbUri = new URI(databaseUrl);

                String host = dbUri.getHost();
                int port = dbUri.getPort() == -1 ? 5432 : dbUri.getPort();
                String database = dbUri.getPath() != null && dbUri.getPath().length() > 1 
                        ? dbUri.getPath().substring(1) : pgDatabase;
                String query = dbUri.getQuery();

                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);
                if (query != null && !query.isEmpty()) {
                    jdbcUrl += "?" + query;
                }

                config.setJdbcUrl(jdbcUrl);

                String userInfo = dbUri.getUserInfo();
                if (userInfo != null && !userInfo.isEmpty()) {
                    String[] credentials = userInfo.split(":");
                    config.setUsername(credentials[0]);
                    if (credentials.length > 1) {
                        config.setPassword(credentials[1]);
                    }
                } else {
                    config.setUsername(pgUser);
                    config.setPassword(pgPassword);
                }

                log.info("Configured database connection to: {}:{}/{}", host, port, database);
            } else if (databaseUrl.startsWith("jdbc:")) {
                config.setJdbcUrl(databaseUrl);
                config.setUsername(pgUser);
                config.setPassword(pgPassword);
            } else {
                config.setJdbcUrl("jdbc:" + databaseUrl);
                config.setUsername(pgUser);
                config.setPassword(pgPassword);
            }
        } else {
            String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", pgHost, pgPort, pgDatabase);
            config.setJdbcUrl(jdbcUrl);
            config.setUsername(pgUser);
            config.setPassword(pgPassword);
            log.info("Using fallback database configuration: {}:{}/{}", pgHost, pgPort, pgDatabase);
        }

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setIdleTimeout(30000);
        config.setConnectionTimeout(30000);
        config.setMaxLifetime(1800000);

        return new HikariDataSource(config);
    }
}
