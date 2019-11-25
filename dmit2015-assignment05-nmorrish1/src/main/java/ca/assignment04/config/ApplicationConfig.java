package ca.assignment04.config;

import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.enterprise.context.ApplicationScoped;
import javax.faces.annotation.FacesConfig;
import javax.security.enterprise.authentication.mechanism.http.BasicAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.FormAuthenticationMechanismDefinition;
import javax.security.enterprise.authentication.mechanism.http.LoginToContinue;

import org.glassfish.soteria.identitystores.annotation.Credentials;
import org.glassfish.soteria.identitystores.annotation.EmbeddedIdentityStoreDefinition;

@DataSourceDefinitions({
	@DataSourceDefinition(
			name="java:app/datasources/assignment04/assignment04DS",
			className="org.h2.jdbcx.JdbcDataSource",
			url="jdbc:h2:file:~/assignment04db",
			user="sa",
			password="sa")
})

@BasicAuthenticationMechanismDefinition(realmName = "jaspitest")

@EmbeddedIdentityStoreDefinition({
	@Credentials(callerName = "user2015", password = "Password2015", groups = { "USER", "ADMIN" }),
	@Credentials(callerName = "dmit2015", password = "Password2015", groups = "USER"),
	@Credentials(callerName = "admin2015", password = "Password2015", groups = "ADMIN"), 
})



@FacesConfig @ApplicationScoped
public class ApplicationConfig {
	
}