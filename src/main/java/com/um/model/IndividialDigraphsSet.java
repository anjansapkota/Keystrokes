package com.um.model;

public class IndividialDigraphsSet {
	Long digraph = (long) 0;
	java.util.Vector<Key> Prueba = new java.util.Vector <Key> ();
	java.util.Vector<Key> Person = new java.util.Vector <Key> ();
	
	
	public Long getDigraph() {
		return digraph;
	}
	public void setDigraph(Long diagraph) {
		this.digraph = diagraph;
	}
	public java.util.Vector<Key> getPrueba() {
		return Prueba;
	}
	public void setPrueba(java.util.Vector<Key> prueba) {
		Prueba = prueba;
	}
	public java.util.Vector<Key> getPerson() {
		return Person;
	}
	public void setPerson(java.util.Vector<Key> person) {
		Person = person;
	}	
}
