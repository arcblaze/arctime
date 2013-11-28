package com.arcblaze.arctime;

import static com.arcblaze.arctime.model.Role.ADMIN;
import static com.arcblaze.arctime.model.Role.MANAGER;
import static com.arcblaze.arctime.model.Role.PAYROLL;
import static com.arcblaze.arctime.model.Role.SUPERVISOR;
import static com.arcblaze.arctime.model.Role.USER;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Service;
import org.apache.catalina.Wrapper;
import org.apache.catalina.authenticator.FormAuthenticator;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.servlet.JspServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.model.Role;
import com.arcblaze.arctime.security.SecurityRealm;
import com.sun.jersey.spi.container.servlet.ServletContainer;

/**
 * Responsible for launching this system.
 */
public class Server {
	/** This will be used to log messages. */
	private final static Logger log = LoggerFactory.getLogger(Server.class);

	/**
	 * Default constructor starts the embedded Tomcat server.
	 */
	public Server() {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir(".");
		tomcat.getHost().setAppBase(".");
		tomcat.getConnector().setPort(Property.SERVER_PORT_INSECURE.getInt());

		Service service = tomcat.getService();
		if (!Property.SERVER_INSECURE_MODE.getBoolean()) {
			service.addConnector(getSecureConnector());
			tomcat.getConnector().setRedirectPort(
					Property.SERVER_PORT_INSECURE.getInt());
		}

		SecurityRealm realm = new SecurityRealm("arctime");
		tomcat.getEngine().setRealm(realm);

		Context context = null;
		if (Property.SERVER_DEVELOPMENT_MODE.getBoolean())
			context = tomcat.addContext("", "arctime-web/src/main/webapp");
		else
			context = tomcat.addContext("", "webapp");
		context.addWelcomeFile("/index.jsp");

		Wrapper defaultServlet = getDefaultServletWrapper(context);
		context.addChild(defaultServlet);
		context.addServletMapping("/", defaultServlet.getName());

		Wrapper jspServlet = getJspServletWrapper(context);
		context.addChild(jspServlet);
		context.addServletMapping("*.jsp", jspServlet.getName());

		Wrapper jerseyServlet = getJerseyServletWrapper(context);
		context.addChild(jerseyServlet);
		context.addServletMapping("/rest/*", jerseyServlet.getName());

		for (Role role : Role.values())
			context.addSecurityRole(role.name());
		for (SecurityConstraint constraint : getSecurityConstraints())
			context.addConstraint(constraint);

		LoginConfig loginConfig = new LoginConfig();
		loginConfig.setAuthMethod("FORM");
		loginConfig.setRealmName("arctime");
		loginConfig.setLoginPage("/login.jsp");
		loginConfig.setErrorPage("/error.jsp");
		context.setLoginConfig(loginConfig);
		context.getPipeline().addValve(new FormAuthenticator());

		try {
			tomcat.start();
			tomcat.getServer().await();
		} catch (LifecycleException e) {
			log.error("Tomcat error.", e);
		}
	}

	/**
	 * @return a {@link Connector} for secure HTTPS connections with web clients
	 */
	protected Connector getSecureConnector() {
		Connector httpsConnector = new Connector();
		httpsConnector.setPort(Property.SERVER_PORT_SECURE.getInt());
		httpsConnector.setSecure(true);
		httpsConnector.setScheme("https");
		httpsConnector.setAttribute("clientAuth", "false");
		httpsConnector.setAttribute("sslProtocol", "TLS");
		httpsConnector.setAttribute("SSLEnabled", true);
		httpsConnector.setAttribute("keyAlias",
				Property.SERVER_CERTIFICATE_KEY_ALIAS.getString());
		httpsConnector.setAttribute("keystorePass",
				Property.SERVER_KEYSTORE_PASS.getString());
		httpsConnector.setAttribute("keystoreFile",
				Property.SERVER_KEYSTORE_FILE.getString());
		return httpsConnector;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the default servlet wrapper to be included in the app
	 */
	protected Wrapper getDefaultServletWrapper(Context context) {
		Wrapper defaultServlet = context.createWrapper();
		defaultServlet.setName("default");
		defaultServlet.setServletClass(DefaultServlet.class.getName());
		defaultServlet.addInitParameter("debug", "0");
		defaultServlet.addInitParameter("listings", "false");
		defaultServlet.setLoadOnStartup(1);
		return defaultServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the default servlet wrapper to be included in the app
	 */
	protected Wrapper getJspServletWrapper(Context context) {
		Wrapper defaultServlet = context.createWrapper();
		defaultServlet.setName("jsp");
		defaultServlet.setServletClass(JspServlet.class.getName());
		defaultServlet.addInitParameter("classdebuginfo", "false");
		defaultServlet.addInitParameter("development",
				String.valueOf(Property.SERVER_DEVELOPMENT_MODE.getBoolean()));
		defaultServlet.addInitParameter("fork", "false");
		defaultServlet.setLoadOnStartup(3);
		return defaultServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the default servlet wrapper to be included in the app
	 */
	protected Wrapper getJerseyServletWrapper(Context context) {
		Wrapper jerseyServlet = context.createWrapper();
		jerseyServlet.setName("jersey");
		jerseyServlet.setServletClass(ServletContainer.class.getName());
		jerseyServlet.addInitParameter(
				"com.sun.jersey.config.property.packages",
				"com.arcblaze.arctime.rest");
		jerseyServlet.setLoadOnStartup(1);
		return jerseyServlet;
	}

	/**
	 * @return the {@link SecurityConstraint} objects to be applied to the web
	 *         application
	 */
	protected List<SecurityConstraint> getSecurityConstraints() {
		List<SecurityConstraint> constraints = new ArrayList<>();

		Map<String, List<Role>> map = new HashMap<>();
		map.put("/rest/admin/*", Arrays.asList(ADMIN));
		map.put("/rest/manager/*", Arrays.asList(ADMIN, MANAGER));
		map.put("/rest/payroll/*", Arrays.asList(ADMIN, PAYROLL));
		map.put("/rest/supervisor/*", Arrays.asList(ADMIN, SUPERVISOR));
		map.put("/rest/user/*", Arrays.asList(ADMIN, USER));

		for (Map.Entry<String, List<Role>> entry : map.entrySet()) {
			SecurityCollection collection = new SecurityCollection();
			collection.setName(entry.getKey());
			collection.addPattern(entry.getKey());
			SecurityConstraint constraint = new SecurityConstraint();
			for (Role role : entry.getValue())
				constraint.addAuthRole(role.name());
			constraint.setDisplayName(entry.getKey());
			constraint.addCollection(collection);
			constraint.setUserConstraint("NONE");
			constraints.add(constraint);
		}

		return constraints;
	}

	/**
	 * @param args
	 *            the command-line arguments
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception {
		new Server();
	}
}
