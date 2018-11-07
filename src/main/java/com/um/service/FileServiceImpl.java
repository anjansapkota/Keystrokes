package com.um.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;
import com.um.model.File;
import com.um.model.Share_info;
import com.um.model.Usuario;

@Service("fileService")
public class FileServiceImpl implements FileService {
	 @Autowired
		@Qualifier("postgresJdbcTemplate")
		private JdbcTemplate postgresTemplate;

	@Override
	public ArrayList<File> listaFiles(int matricula) {
		ArrayList<File> files = new ArrayList<File>();
		String query = "SELECT * FROM FILE where owner_id = ? ORDER BY file_name";
		postgresTemplate.query(query, new Object[]{matricula}, new RowCallbackHandler()	{
			public void processRow(ResultSet rs) throws SQLException {
				File file = new File();	
				file.setId(rs.getInt("ID"));
				file.setFile_name(rs.getString("file_name"));
				file.setSize(rs.getString("size"));
				file.setDate_added(rs.getDate("date_added"));
				file.setExpiry_date(rs.getDate("expiry_date"));
				file.setOwner_id(rs.getString("owner_id"));
				file.setFile_link(rs.getString("file_link"));
				file.setAa(rs.getString("uploaded_os"));
				file.setBb(rs.getString("browser_used"));
				file.setCc(rs.getString("browser_version"));
				file.setDd(rs.getString("uploader_ip_address"));
				files.add(file);
		   }
		});
		return files;
	}

	@Override
	public String guardarFile(File file) {
				String query = "INSERT INTO file (file_name, size, date_added, expiry_date, owner_id, file_link, uploaded_os, browser_used, browser_version, uploader_ip_address) VALUES ( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				Object[] parametros = new Object[]{
				file.getFile_name(), file.getSize(), file.getDate_added(), file.getExpiry_date(), Integer.parseInt(file.getOwner_id()), file.getFile_link(), file.getAa(), file.getBb(), file.getCc(), file.getDd() };
				String uploaded = "No";
				int a = postgresTemplate.update(query, parametros);
				if(a==1) {
					uploaded = "Si";
				}
				query = "SELECT MAX(ID) FROM FILE";
				postgresTemplate.queryForObject(query, Integer.class); //Integer.class is like telling what kind of file am I expecting the query to return. In this case an int
				return uploaded;
	}

	@Override
	public File buscaFilePorId(int id) {
				File file = new File();
				String query = "SELECT * FROM FILE WHERE ID = ?";
				postgresTemplate.query(query, new Object[]{id}, new RowCallbackHandler()	{
					public void processRow(ResultSet rs) throws SQLException {
						file.setId(rs.getInt("ID"));
						file.setFile_name(rs.getString("file_name"));
						file.setSize(rs.getString("size"));
						file.setDate_added(rs.getDate("date_added"));
						file.setExpiry_date(rs.getDate("expiry_date"));
						file.setOwner_id(rs.getString("owner_id"));
						file.setFile_link(rs.getString("file_link"));
						file.setAa(rs.getString("uploaded_os"));
						file.setBb(rs.getString("browser_used"));
						file.setCc(rs.getString("browser_version"));
						file.setDd(rs.getString("uploader_ip_address"));		
				   }
				});
				return file;
	}


		@Override
		public String borrarFile(File file) {
				String query = "DELETE FROM FILE WHERE ID = ?";
				Object parametros = new Object();
				parametros = file.getId();
				postgresTemplate.update(query, parametros);		
				String borrar = "Si";
				return borrar;
		}
	
		
		public ArrayList<File> deleteOldFiles() {
				ArrayList<File> files = new ArrayList<File>();
				String query = "SELECT * FROM file";
				postgresTemplate.query(query, new Object[]{}, new RowCallbackHandler()	{
					public void processRow(ResultSet rs) throws SQLException {
						File file = new File();
						file.setId(rs.getInt("ID"));
						file.setFile_name(rs.getString("file_name"));
						file.setOwner_id(rs.getString("owner_id"));
						Date date_today = new Date();
						Date expiry_date = rs.getDate("expiry_date");
						if(expiry_date.before(date_today)) {
							files.add(file);
							borrarFile(file);
						}
				   }
				});
				return files;
		}
		
		@Override
		public String shareFile(int file_id, int owner, int sharedwith) {
				String shared = "No";
				int sharing = isItSharedWith(file_id, sharedwith);
				if(sharing==0) {
						String query = "INSERT INTO public.file_sharing(file_id, file_owner, shared_with, file_owner_name) VALUES (?, ?, ?,?)";		
						String fileOwnername = whoseFileIsThis(file_id);
						Object[] parametros = new Object[]{
								file_id, owner, sharedwith,fileOwnername
							};
						postgresTemplate.update(query, parametros);	
						System.out.println("Fue compartido bien.......");
						shared = "Si";
						}
				if(sharing==1) {
					shared = "Si";
				}
				
				return shared;
		}
	
	
		@Override
		public int isItSharedWith(int file_id, int matricula) {
				ArrayList<Share_info> shared_files = new ArrayList<Share_info>();
				String query = "SELECT * FROM file_sharing where file_id = ? AND shared_with = ?";		
				int result = 0;
				postgresTemplate.query(query, new Object[]{file_id, matricula}, new RowCallbackHandler()	{
					public void processRow(ResultSet rs) throws SQLException {
						if(rs.getInt("shared_with") == matricula ) {
							Share_info share_info = new Share_info();
							share_info.setId(rs.getInt("ID"));
							share_info.setFile_id(rs.getInt("file_id"));
							share_info.setFile_owner(rs.getInt("file_owner"));
							share_info.setShared_with(rs.getInt("shared_with"));
							share_info.setFile_owner_name(rs.getString("file_owner_name"));
							shared_files.add(share_info);
						}
				   }
				});
				if(!shared_files.isEmpty()) {
						if(matricula==shared_files.get(0).getShared_with()) {
							result = 1;
						}
				}
				return result;
		}
	
	

		@Override
		public ArrayList<Share_info> listmySharedFiles(int matricula) {
				ArrayList<Share_info> shared_files = new ArrayList<Share_info>();
				String query = "SELECT * FROM file_sharing WHERE shared_with = ? ORDER BY id";
				postgresTemplate.query(query, new Object[]{matricula}, new RowCallbackHandler()	{
					public void processRow(ResultSet rs) throws SQLException {
						
						Share_info share_info = new Share_info();
						share_info.setId(rs.getInt("ID"));
						share_info.setFile_id(rs.getInt("file_id"));
						share_info.setFile_owner(rs.getInt("file_owner"));
						share_info.setShared_with(rs.getInt("shared_with"));
						share_info.setFile_owner_name(rs.getString("file_owner_name"));
						int aa=0;
							/*if(!shared_files.isEmpty()) {
								for (int j = 0; j < shared_files.size(); j++) {
								int a = shared_files.get(j).getFile_id();
								int b = share_info.getFile_id();
								if(a==b){
									aa=1;					
								}
							}
						}*/
						
						if(aa==0) {
							String query2 = "SELECT * FROM FILE WHERE id = ? ORDER BY file_name";
							postgresTemplate.query(query2, new Object[]{ share_info.getFile_id()}, new RowCallbackHandler()	{
								public void processRow(ResultSet rs) throws SQLException {
									File file = new File();	
									file.setId(rs.getInt("ID"));
									file.setFile_name(rs.getString("file_name"));
									file.setSize(rs.getString("size"));
									file.setDate_added(rs.getDate("date_added"));
									file.setExpiry_date(rs.getDate("expiry_date"));
									file.setOwner_id(rs.getString("owner_id"));
									file.setFile_link(rs.getString("file_link"));
									file.setAa(rs.getString("uploaded_os"));
									file.setBb(rs.getString("browser_used"));
									file.setCc(rs.getString("browser_version"));
									file.setDd(rs.getString("uploader_ip_address"));
									share_info.setFile(file);
									shared_files.add(share_info);
								}
							});
							
						}
					}			
				});
				
				//System.out.println(shared_files.get(0).getFile().getFile_name());
				return shared_files;
			
			
			} 
	
		@Override
		public String whoseFileIsThis(int file_id) {
				File file = buscaFilePorId(file_id);
				Usuario usr1 = new Usuario();
				
				String query = "SELECT * FROM USUARIO WHERE MATRICULA = ?";
				postgresTemplate.query(query, new Object[]{file.getOwner_id()}, new RowCallbackHandler()	{
					public void processRow(ResultSet rs) throws SQLException {
						usr1.setId(rs.getInt("ID"));
						usr1.setNombre(rs.getString("NOMBRE"));
						usr1.setMatricula(rs.getString("MATRICULA"));
						usr1.setPassword(rs.getString("PASSWORD"));
						usr1.setActivo(rs.getInt("ACTIVO"));	
				   }
				});
				String nameofthewoner = usr1.getNombre();
				return nameofthewoner;
		}
}
				
							
							

