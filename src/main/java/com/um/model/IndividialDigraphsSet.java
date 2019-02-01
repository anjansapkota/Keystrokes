package com.um.model;

import java.util.HashMap;

import groovyjarjarantlr.collections.impl.Vector;

public class IndividialDigraphsSet {
	HashMap<String, java.util.Vector> IDS = new HashMap<String, java.util.Vector> ();
	
	public HashMap<String, java.util.Vector> getIDS() {
		return IDS;
	}

	public void setIDS(HashMap<String, java.util.Vector> temphashIDS) {
		IDS = temphashIDS;
	}
}
