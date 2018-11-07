package com.um.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.um.model.File;
import com.um.model.Share_info;
import com.um.model.Usuario;
import com.um.service.FileService;
import com.um.service.UsuarioService;

@Controller
public class FileController {
	
	 @Autowired
	 	private HttpServletResponse response;
	 
	 	private Authentication auth;
	 	private String matricula;
	@Autowired
	private FileService fileService;
	@Autowired
	private UsuarioService usuarioService;
	@RequestMapping(value={"/uploadFile"}, method = RequestMethod.GET)
	public ModelAndView registro(){
		File file = new File();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("file", file);
		modelAndView.setViewName("uploadFile");
		return modelAndView;
	}
	
	@RequestMapping(value={"/listFiles"}, method = RequestMethod.GET)
	public ModelAndView listAllFiles() throws IllegalStateException, IOException{
		dumpOldFiles();
		auth = SecurityContextHolder.getContext().getAuthentication();
		matricula = auth.getName();
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("filesbrought", listMyFiles() );  //this will return a list and the list will be named files in the view.
		modelAndView.addObject("sharedfiles", listMySharedFiles());
		modelAndView.addObject("user", infoUsuario().getNombre());
		modelAndView.setViewName("inicio");
		return modelAndView;
	}
	
	@RequestMapping(value = {"/borrar/{id}"}, method = RequestMethod.GET)
	public ModelAndView borrar(@PathVariable int id) throws IllegalStateException, IOException {
			ModelAndView modelAndView = new ModelAndView();
			//modelAndView.addObject("fileParaborrar", fileService.buscaFilePorId(id));  //this will return a list and the list will be named files in the view.
			auth = SecurityContextHolder.getContext().getAuthentication();
			String matriculaEnTexto = auth.getName();
			int matriculaInt = Integer.parseInt(matriculaEnTexto);
			String erased = "No";
			File temp =  fileService.buscaFilePorId(id);
			if((Integer.parseInt(temp.getOwner_id()))==(matriculaInt)) {
				erased = fileService.borrarFile(temp);
				delFileFromDir(temp);
			}
			modelAndView.addObject("filesbrought", listMyFiles());  //this will return a list and the list will be named files in the view.
			modelAndView.addObject("sharedfiles", listMySharedFiles());
			modelAndView.addObject("user", infoUsuario().getNombre());
			modelAndView.addObject("erased", erased);
			modelAndView.setViewName("inicio");
			return modelAndView;
	}
	
	
	@RequestMapping(value={"/shareFile/{name}/{id}"}, method = RequestMethod.GET)
	public ModelAndView  shareFile(@PathVariable String name,@PathVariable int id ){
		auth = SecurityContextHolder.getContext().getAuthentication();
		String matriculaEnTexto = auth.getName();
		int matriculaInt = Integer.parseInt(matriculaEnTexto);
		String shared= "No";
		File temp =  fileService.buscaFilePorId(id);
		if((Integer.parseInt(temp.getOwner_id()))==(matriculaInt)) {
			name = name.replaceAll("\\s","");
			String[] parts = name.split(",");
			int[] peopleToShareWith = new int[parts.length];
			for (int i = 0; i < parts.length; i++) {
				peopleToShareWith[i] = Integer.parseInt(parts[i]);
			}
			int owner = Integer.parseInt(auth.getName());
			for (int i = 0; i < peopleToShareWith.length; i++) {
				shared = fileService.shareFile(id ,owner, peopleToShareWith[i]);
			}
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("filesbrought", listMyFiles() );  //this will return a list and the list will be named files in the view.
		modelAndView.addObject("sharedfiles", listMySharedFiles());
		modelAndView.addObject("user", infoUsuario().getNombre());
		modelAndView.addObject("shared", shared);
		modelAndView.setViewName("inicio");
		return modelAndView;
		
	}
	
	@RequestMapping(value={"/r/{link}/{idfile}"}, method = RequestMethod.GET)
	public ModelAndView  downloadFile(@PathVariable String link, @PathVariable int idfile ) throws IllegalStateException, IOException, ServletException{
				auth = SecurityContextHolder.getContext().getAuthentication();
				String matriculaEnTexto = auth.getName();
				String fileAccess = "Denied";
				int matriculaInt = Integer.parseInt(matriculaEnTexto);
				File temp =  fileService.buscaFilePorId(idfile);
				if((Integer.parseInt(temp.getOwner_id()))==(matriculaInt)) {
					getFileFromDir(temp);
					fileAccess = "Granted";
				} else {
					int isSharedwith = fileService.isItSharedWith(idfile, matriculaInt);
					if(isSharedwith==1) {
						getFileFromDir(temp);
						fileAccess = "Granted";
					}
				}
				ModelAndView modelAndView = new ModelAndView();
				modelAndView.addObject("filesbrought", listMyFiles() );  //this will return a list and the list will be named files in the view.
				modelAndView.addObject("sharedfiles", listMySharedFiles());
				modelAndView.addObject("user", infoUsuario().getNombre());
				modelAndView.addObject("fileAccess", fileAccess);
				modelAndView.setViewName("inicio");
				return modelAndView;
	}
	
	public ArrayList<File> listMyFiles(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		int matricula = Integer.parseInt(auth.getName());
		ArrayList<File> resultedfiles = fileService.listaFiles(matricula);
		return resultedfiles;
	}
	public ArrayList<Share_info> listMySharedFiles(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		int matricula = Integer.parseInt(auth.getName());
		ArrayList<Share_info> sharedfiles = fileService.listmySharedFiles(matricula);
		return sharedfiles;
	}
	
	public Usuario infoUsuario(){
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		Usuario user = usuarioService.buscaUsuarioPorMatricula(auth.getName());
		return user;
	}
	
	
	
	public void getFileFromDir(File file) throws IllegalStateException, IOException {
		String uploadsDir = "/" + file.getOwner_id() + "/";
		
		String realPathtoUploads = "D:\\uploadsE65\\" + uploadsDir;
		String orgName = file.getFile_name();
        String filePath = realPathtoUploads + orgName;
        java.io.File file2 = new java.io.File(filePath);
		InputStream fis = new FileInputStream(file2);
		response.setContentType(realPathtoUploads != null? realPathtoUploads:"application/octet-stream");
		response.setContentLength((int) file2.length());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getFile_name() + "\"");	    
		ServletOutputStream os = response.getOutputStream();
		Path path = Paths.get(filePath);
		byte[] bufferData = Files.readAllBytes(path) ;
		int read=0;
		while((read = fis.read(bufferData))!= -1){
			os.write(bufferData, 0, read);
		}
		os.flush();
		os.close();
		fis.close();
		System.out.println("File downloaded at client successfully");
    }
	
	public boolean delFileFromDir(File file) throws IllegalStateException, IOException {
		String uploadsDir = "/" + file.getOwner_id() + "/";
		String realPathtoUploads = "D:\\uploadsE65\\" + uploadsDir;
		String orgName = file.getFile_name();
        String filePath = realPathtoUploads + orgName;
        java.io.File file2 = new java.io.File(filePath);
        boolean result = file2.delete();
        return result;
	}
	
	public void dumpOldFiles() throws IllegalStateException, IOException{
		ArrayList<File> filestodelete = fileService.deleteOldFiles();
		if(filestodelete.size()> 0){
		for(int i=0; i<filestodelete.size(); i++ ) {
			delFileFromDir(filestodelete.get(i));
			}
		}
	}
	
	@RequestMapping(value={"/changePassword"}, method = RequestMethod.GET)
	public ModelAndView changePasswordRequest(){
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("filesbrought", listMyFiles() );  //this will return a list and the list will be named files in the view.
		modelAndView.addObject("sharedfiles", listMySharedFiles());
		modelAndView.addObject("user", infoUsuario().getNombre());
		modelAndView.addObject("requestingPasswordChange", "Si");
		modelAndView.setViewName("inicio");
		return modelAndView;
	}
	
	
}