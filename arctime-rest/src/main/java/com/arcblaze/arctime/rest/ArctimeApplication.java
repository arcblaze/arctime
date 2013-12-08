package com.arcblaze.arctime.rest;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.Application;

import org.apache.commons.lang.StringUtils;
import org.glassfish.jersey.server.internal.scanning.PackageNamesScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This application class is used to find and load the resource classes.
 */
public class ArctimeApplication extends Application {
	private final static Logger log = LoggerFactory
			.getLogger(ArctimeApplication.class);

	private final Set<Class<?>> classes;

	/**
	 * Default constructor.
	 */
	public ArctimeApplication() {
		SortedSet<String> classNames = getClassNames();
		this.classes = new HashSet<>(super.getClasses());
		try {
			for (String className : classNames) {
				log.info("Found resource: {}", className);
				this.classes.add(Class.forName(className));
			}
		} catch (ClassNotFoundException badClass) {
			log.error("Failed to add jersey resource class", badClass);
		}
	}

	protected SortedSet<String> getClassNames() {
		PackageNamesScanner scanner = new PackageNamesScanner(
				new String[] { ArctimeApplication.class.getPackage().getName() },
				true);
		SortedSet<String> classNames = new TreeSet<>();
		while (scanner.hasNext()) {
			String file = scanner.next();
			String className = StringUtils.substringBeforeLast(file, ".")
					.replace('/', '.');
			if (StringUtils.endsWith(className, "Resource")
					&& !StringUtils.endsWith(className, ".BaseResource"))
				classNames.add(className);
		}
		return classNames;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return this.classes;
	}
}
