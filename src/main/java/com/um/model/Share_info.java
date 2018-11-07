package com.um.model;

public class Share_info {
	int id;
	int file_id;
	int file_owner;
	int shared_with;
	File file;
	String file_owner_name;
	
	public String getFile_owner_name() {
		return file_owner_name;
	}
	public void setFile_owner_name(String file_owner_name) {
		this.file_owner_name = file_owner_name;
	}
	public File getFile() {
		return file;
	}
	public void setFile(File file) {
		this.file = file;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getFile_id() {
		return file_id;
	}
	public void setFile_id(int file_id) {
		this.file_id = file_id;
	}
	public int getFile_owner() {
		return file_owner;
	}
	public void setFile_owner(int file_owner) {
		this.file_owner = file_owner;
	}
	public int getShared_with() {
		return shared_with;
	}
	public void setShared_with(int shared_with) {
		this.shared_with = shared_with;
	}

}
