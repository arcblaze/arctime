package com.arcblaze.arctime.rest;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.ws.rs.core.Application;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

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
			this.classes.add(JacksonJsonProvider.class);
		} catch (ClassNotFoundException badClass) {
			log.error("Failed to add jersey resource class", badClass);
		}
	}

	protected SortedSet<String> getClassNames() {
		SortedSet<String> classNames = new TreeSet<>();
		try {
			String packageName = ArctimeApplication.class.getPackage()
					.getName();
			ClassPath classPath = ClassPath.from(getClass().getClassLoader());
			for (ClassInfo classInfo : classPath
					.getTopLevelClassesRecursive(packageName)) {
				if (isValidResourceClass(classInfo.getName()))
					classNames.add(classInfo.getName());
			}
		} catch (IOException classpathIssue) {
			log.error("Failed to retrieve resources from class path.",
					classpathIssue);
		}
		return classNames;
	}

	protected boolean isValidResourceClass(String className) {
		if (ArctimeApplication.class.getName().equals(className))
			return false;
		if (StringUtils.endsWith(className, "Test"))
			return false;
		if (StringUtils.endsWith(className, "BaseResource"))
			return false;

		return true;
	}

	@Override
	public Set<Class<?>> getClasses() {
		return Collections.unmodifiableSet(this.classes);
	}
}
