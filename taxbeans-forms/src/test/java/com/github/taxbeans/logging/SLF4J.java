package com.github.taxbeans.logging;

import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

public class SLF4J {
	
	public static void start() {
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
		LoggerFactory.getILoggerFactory();
	}
}
