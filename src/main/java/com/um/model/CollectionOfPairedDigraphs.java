package com.um.model;

import java.util.HashMap;

public class CollectionOfPairedDigraphs {
	HashMap<String, IndividialDigraphsSet> collectedKeys = new HashMap<String, IndividialDigraphsSet> ();

	public HashMap<String, IndividialDigraphsSet> getCollectedKeys() {
		return collectedKeys;
	}

	public void setCollectedKeys(HashMap<String, IndividialDigraphsSet> collectedKeys) {
		this.collectedKeys = collectedKeys;
	}
}
