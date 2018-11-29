package com.um.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ServiceResponse<T> {
	
	public ServiceResponse(String string, int i) {
		// TODO Auto-generated constructor stub
	}
	private String status;
	private int data;

}