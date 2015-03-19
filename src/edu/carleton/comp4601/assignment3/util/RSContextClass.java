package edu.carleton.comp4601.assignment3.util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.carleton.comp4601.assignment3.Main.SocialGraph;

public class RSContextClass implements ServletContextListener {

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		SocialGraph.getInstance();

	}

}
