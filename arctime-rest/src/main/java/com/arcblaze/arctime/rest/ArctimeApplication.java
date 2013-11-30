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

	protected SortedSet<String> getClassNames() {
		PackageNamesScanner scanner = new PackageNamesScanner(
				new String[] { ArctimeApplication.class.getPackage().getName() },
				true);
		SortedSet<String> classNames = new TreeSet<>();
		while (scanner.hasNext()) {
			String file = scanner.next();
			classNames.add(StringUtils.substringBeforeLast(file, ".").replace(
					'/', '.'));
		}
		return classNames;
	}

	@Override
	public Set<Class<?>> getClasses() {
		SortedSet<String> classNames = getClassNames();

		Set<Class<?>> classes = new HashSet<>(super.getClasses());
		try {
			for (String className : classNames) {
				log.info("Found resource: {}", className);
				classes.add(Class.forName(className));
			}
		} catch (ClassNotFoundException badClass) {
			log.error("Failed to add jersey resource class", badClass);
		}
		return classes;
	}
}
