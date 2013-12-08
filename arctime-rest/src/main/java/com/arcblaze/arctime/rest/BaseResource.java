package com.arcblaze.arctime.rest;

import javax.servlet.ServletContext;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.servlets.MetricsServlet;

/**
 * The base class for all resources.
 */
public class BaseResource {
	/**
	 * @param context
	 *            the servlet context from which the metric registry should be
	 *            retrieved
	 * 
	 * @return an instance of the application metric registry
	 */
	protected MetricRegistry getMetricRegistry(ServletContext context) {
		return (MetricRegistry) context
				.getAttribute(MetricsServlet.METRICS_REGISTRY);
	}

	/**
	 * @param context
	 *            the servlet context from which the metric timer should be
	 *            retrieved
	 * @param name
	 *            the name of the timer to retrieve
	 * 
	 * @return an instance of the requested timer
	 */
	protected Timer.Context getTimer(ServletContext context, String name) {
		MetricRegistry metricRegistry = getMetricRegistry(context);
		return metricRegistry.timer(name).time();
	}
}
