package com.um.model;

import javax.validation.constraints.Size;

public class Role {
	
	private int id;
	@Size(min = 3, max = 50, message="Campo obligatorio")
	private String nombre;
	private String descripcion;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	
	public String getDescripcion() {
		return descripcion;
	}
	
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

}
