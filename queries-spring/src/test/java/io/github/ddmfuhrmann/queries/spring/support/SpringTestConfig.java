package io.github.ddmfuhrmann.queries.spring.support;

import io.github.ddmfuhrmann.queries.Queries;
import io.github.ddmfuhrmann.queries.spring.SpringJdbcQueries;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

@Configuration
public class SpringTestConfig {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource();
        ds.setDriverClassName("org.h2.Driver");
        ds.setUrl("jdbc:h2:mem:queries_spring;DB_CLOSE_DELAY=-1;MODE=PostgreSQL");
        ds.setUsername("sa");
        ds.setPassword("");

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
        populator.addScript(new org.springframework.core.io.ClassPathResource("sql/spring-sample-schema.sql"));
        DatabasePopulatorUtils.execute(populator, ds);

        return ds;
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    @Bean
    public Queries queries(NamedParameterJdbcTemplate jdbcTemplate) {
        return new SpringJdbcQueries(jdbcTemplate);
    }
}
