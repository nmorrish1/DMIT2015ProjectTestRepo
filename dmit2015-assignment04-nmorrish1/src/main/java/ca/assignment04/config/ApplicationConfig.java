package ca.assignment04.config;

import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.enterprise.context.ApplicationScoped;

@DataSourceDefinitions({
	@DataSourceDefinition(
			name="java:app/datasources/assignment04/assignment04DS",
			className="org.h2.jdbcx.JdbcDataSource",
			url="jdbc:h2:file:~/assignment04db",
			user="sa",
			password="sa")
})

@ApplicationScoped
public class ApplicationConfig {
	
}