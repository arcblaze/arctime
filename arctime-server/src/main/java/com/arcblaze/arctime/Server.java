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
import org.apache.catalina.deploy.FilterDef;
import org.apache.catalina.deploy.FilterMap;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.deploy.SecurityCollection;
import org.apache.catalina.deploy.SecurityConstraint;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;
import org.apache.commons.lang.StringUtils;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.jasper.servlet.JspServlet;
import org.glassfish.jersey.servlet.ServletContainer;
import org.glassfish.jersey.servlet.ServletProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.arcblaze.arctime.config.Property;
import com.arcblaze.arctime.model.Role;
import com.arcblaze.arctime.rest.ArctimeApplication;
import com.arcblaze.arctime.security.SecurityRealm;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.servlet.InstrumentedFilter;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.codahale.metrics.servlets.PingServlet;
import com.codahale.metrics.servlets.ThreadDumpServlet;

/**
 * Responsible for launching this system.
 */
public class Server {
	/** This will be used to log messages. */
	private final static Logger log = LoggerFactory.getLogger(Server.class);

	/** Used to manage application metrics. */
	private final MetricRegistry metricRegistry = new MetricRegistry();

	/** Used to manage application health and status. */
	private final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();

	/**
	 * Default constructor starts the embedded Tomcat server.
	 */
	public Server() {
		Tomcat tomcat = new Tomcat();
		tomcat.setBaseDir(".");
		tomcat.getHost().setAppBase(".");

		Service service = tomcat.getService();
		Connector insecureConnector = getInsecureConnector();
		service.addConnector(insecureConnector);
		if (!Property.SERVER_INSECURE_MODE.getBoolean()) {
			service.addConnector(getSecureConnector());
			insecureConnector.setRedirectPort(Property.SERVER_PORT_INSECURE
					.getInt());
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

		Wrapper metricsServlet = getMetricsServletWrapper(context);
		context.addChild(metricsServlet);
		context.addServletMapping("/rest/admin/metrics",
				metricsServlet.getName());

		Wrapper healthServlet = getHealthServletWrapper(context);
		context.addChild(healthServlet);
		context.addServletMapping("/rest/admin/health", healthServlet.getName());

		Wrapper threadDumpServlet = getThreadDumpServletWrapper(context);
		context.addChild(threadDumpServlet);
		context.addServletMapping("/rest/admin/threads",
				threadDumpServlet.getName());

		Wrapper pingServlet = getPingServletWrapper(context);
		context.addChild(pingServlet);
		context.addServletMapping("/ping", pingServlet.getName());

		Wrapper jerseyServlet = getJerseyServletWrapper(context);
		context.addChild(jerseyServlet);
		context.addServletMapping("/rest/*", jerseyServlet.getName());

		FilterDef metricsFilter = new FilterDef();
		metricsFilter.setFilterName("metricsFilter");
		metricsFilter.setFilterClass(InstrumentedFilter.class.getName());
		context.addFilterDef(metricsFilter);
		FilterMap metricsFilterMap = new FilterMap();
		metricsFilterMap.setFilterName(metricsFilter.getFilterName());
		metricsFilterMap.addURLPattern("/*");
		context.addFilterMap(metricsFilterMap);

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

		context.getServletContext().setAttribute(
				InstrumentedFilter.REGISTRY_ATTRIBUTE, this.metricRegistry);
		context.getServletContext().setAttribute(
				MetricsServlet.METRICS_REGISTRY, this.metricRegistry);
		context.getServletContext().setAttribute(
				HealthCheckServlet.HEALTH_CHECK_REGISTRY,
				this.healthCheckRegistry);

		try {
			tomcat.start();
			tomcat.getServer().await();
		} catch (LifecycleException e) {
			log.error("Tomcat error.", e);
		}
	}

	/**
	 * @return a {@link Connector} for insecure HTTP connections with web
	 *         clients
	 */
	protected Connector getInsecureConnector() {
		Connector httpConnector = new Connector(
				Http11NioProtocol.class.getName());
		httpConnector.setPort(Property.SERVER_PORT_INSECURE.getInt());
		httpConnector.setSecure(false);
		httpConnector.setScheme("http");
		addCompressionAttributes(httpConnector);
		return httpConnector;
	}

	/**
	 * @return a {@link Connector} for secure HTTPS connections with web clients
	 */
	protected Connector getSecureConnector() {
		Connector httpsConnector = new Connector(
				Http11NioProtocol.class.getName());
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
		addCompressionAttributes(httpsConnector);
		return httpsConnector;
	}

	/**
	 * @param connector
	 *            the {@link Connector} on which the compression attributes will
	 *            be applied
	 */
	protected void addCompressionAttributes(Connector connector) {
		connector.setAttribute("compression", "on");
		connector.setAttribute("compressionMinSize", "2048");
		connector.setAttribute("noCompressionUserAgents", "gozilla, traviata");
		connector.setAttribute("compressableMimeType", StringUtils.join(Arrays
				.asList("text/html", "text/plain", "text/javascript",
						"application/json", "application/xml"), ","));
		connector.setAttribute("useSendfile", "false");
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
		defaultServlet.addInitParameter("sendfileSize", "-1");
		defaultServlet.setLoadOnStartup(1);
		return defaultServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the JSP servlet wrapper to be included in the app
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
	 * @return the jersey servlet wrapper to be included in the app
	 */
	protected Wrapper getJerseyServletWrapper(Context context) {
		Wrapper jerseyServlet = context.createWrapper();
		jerseyServlet.setName("jersey");
		jerseyServlet.setServletClass(ServletContainer.class.getName());
		jerseyServlet.addInitParameter(
				ServletProperties.JAXRS_APPLICATION_CLASS,
				ArctimeApplication.class.getName());
		jerseyServlet.setLoadOnStartup(1);
		return jerseyServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the metrics servlet wrapper to be included in the app
	 */
	protected Wrapper getMetricsServletWrapper(Context context) {
		Wrapper metricsServlet = context.createWrapper();
		metricsServlet.setName("metrics");
		metricsServlet.setServletClass(MetricsServlet.class.getName());
		metricsServlet.setLoadOnStartup(2);
		return metricsServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the health check servlet wrapper to be included in the app
	 */
	protected Wrapper getHealthServletWrapper(Context context) {
		Wrapper metricsServlet = context.createWrapper();
		metricsServlet.setName("health");
		metricsServlet.setServletClass(HealthCheckServlet.class.getName());
		metricsServlet.setLoadOnStartup(2);
		return metricsServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the ping servlet wrapper to be included in the app
	 */
	protected Wrapper getPingServletWrapper(Context context) {
		Wrapper metricsServlet = context.createWrapper();
		metricsServlet.setName("ping");
		metricsServlet.setServletClass(PingServlet.class.getName());
		metricsServlet.setLoadOnStartup(2);
		return metricsServlet;
	}

	/**
	 * @param context
	 *            the context to use when creating the servlet wrapper
	 * 
	 * @return the thread dump servlet wrapper to be included in the app
	 */
	protected Wrapper getThreadDumpServletWrapper(Context context) {
		Wrapper metricsServlet = context.createWrapper();
		metricsServlet.setName("threads");
		metricsServlet.setServletClass(ThreadDumpServlet.class.getName());
		metricsServlet.setLoadOnStartup(2);
		return metricsServlet;
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

		String userConstraint = "NONE";
		if (!Property.SERVER_DEVELOPMENT_MODE.getBoolean())
			userConstraint = "CONFIDENTIAL";

		for (Map.Entry<String, List<Role>> entry : map.entrySet()) {
			SecurityCollection collection = new SecurityCollection();
			collection.setName(entry.getKey());
			collection.addPattern(entry.getKey());
			SecurityConstraint constraint = new SecurityConstraint();
			for (Role role : entry.getValue())
				constraint.addAuthRole(role.name());
			constraint.setDisplayName(entry.getKey());
			constraint.addCollection(collection);
			constraint.setUserConstraint(userConstraint);
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
