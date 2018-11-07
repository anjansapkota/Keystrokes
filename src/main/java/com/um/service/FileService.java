package com.um.service;

import java.util.ArrayList;

import com.um.model.File;
import com.um.model.Share_info;

public interface FileService {
	
	public ArrayList<File> listaFiles(int matricula);
		
	public String guardarFile(File file);
	
	public File buscaFilePorId(int id);
	
	public String borrarFile(File file);
	
	public ArrayList<Share_info> listmySharedFiles(int matricula);

	public String shareFile(int file_id, int owner, int sharedwith);
	
	public int isItSharedWith(int file_id, int matricula);
	
	public ArrayList<File> deleteOldFiles();
	public String whoseFileIsThis(int file_id);

}
