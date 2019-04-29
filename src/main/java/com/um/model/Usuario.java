package com.um.model;

import javax.validation.constraints.Size;

public class Usuario {
	
	private int id;
	@Size(min = 1, max = 30, message="Campo obligatorio")
	private String nombre;
	@Size(min = 7, max = 7, message="Matricula invalido")
	private String matricula;
	@Size(min=2, max=50, message="Debe tener mas de 4 caracteres")
	private String password;
	private int activo;
	private int reg_estado;
	
	public int getReg_estado() {
		return reg_estado;
	}
	public void setReg_estado(int reg_estado) {
		this.reg_estado = reg_estado;
	}
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
	public String getMatricula() {
		return matricula;
	}
	public void setMatricula(String matricula) {
		this.matricula = matricula;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public int getActivo() {
		return activo;
	}
	public void setActivo(int activo) {
		this.activo = activo;
	}
}

