package com.um.controller;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.um.model.File;
import com.um.service.FileService;

@Controller
public class FileUploadController {
	
	@Autowired
	private FileService fileService;
	@RequestMapping(value={"/uploadFile"}, method = RequestMethod.POST)
	public ModelAndView upload(@RequestParam ("file") MultipartFile filerecieved) throws IllegalStateException, IOException{
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		ModelAndView modelAndView = new ModelAndView();
		File file = new File();
		if(!filerecieved.isEmpty()){
			String setFile_name = (filerecieved.getOriginalFilename());
			System.out.println("With Path:" + setFile_name);
			int i= setFile_name.length();
			String nameAlreves="";
			while (i > 0) {
				if((Character.toString(setFile_name.charAt(i-1))).equals("\\")){
					  break;
				}
				nameAlreves = nameAlreves + setFile_name.charAt(i-1);
				i--;
			}
			System.out.println("Al reves:" + nameAlreves);
			int j= nameAlreves.length();
			String realFileName= "";
			while (j > 0) {
				realFileName = realFileName + nameAlreves.charAt(j-1);
				j--;
			}
			System.out.println("Without Path:" + realFileName);
			file.setFile_name(realFileName);
			Long size = filerecieved.getSize();
			size = size/1024;
			if(size < 1024) {
				file.setSize(Long.toString(size) + "KB");
			}else if((size < (1024*1024))) {
				size = size/(1024);
				file.setSize(Long.toString(size) + "MB");
			}else if((size < (1024*1024*1024))) {
				size = size/(1024*1024);
				file.setSize(Long.toString(size) + "GB");
			}
			Date date_added = new Date();
			file.setDate_added(date_added);
			
			Date expiry_date = new Date();
			Calendar cal = Calendar.getInstance();
			cal.setTime(expiry_date);
			cal.add(Calendar.DATE, 4); // add 4 days
			expiry_date = cal.getTime();
			file.setExpiry_date(expiry_date);
			file.setOwner_id(auth.getName());   //owner Id is retieved from some other class
			
			file.setFile_link(generateRandomString());
			String uploaded = "No";
			uploaded = saveToDir(filerecieved, file.getFile_name(), auth.getName());
			if(uploaded.equals("Si")) {
				fileService.guardarFile(file);
			}
			modelAndView.setViewName("uploadFile");
			modelAndView.addObject("uploaded", uploaded);
		}
		
		
		return modelAndView;
	}
	
	@RequestMapping(value={"/apple"}, method = RequestMethod.POST)
	public void z(){

	}
	
	public String saveToDir(MultipartFile fileOrg, String link, String owner) throws IllegalStateException, IOException {
		String uploadsDir = "/" + owner + "/";
		String realPathtoUploads = "D:\\uploadsE65\\" + uploadsDir;
		java.io.File directory = new java.io.File(realPathtoUploads);
		if (! directory.exists()){
	        directory.mkdir();
	        // If you require it to make the entire directory path including parents,
	        // use directory.mkdirs(); here instead.
	    }
		System.out.println(realPathtoUploads);
		String orgName = link;
        String filePath = realPathtoUploads + orgName;
        java.io.File dest = new java.io.File(filePath);
        FileOutputStream fos = new FileOutputStream(dest);
        fos.write(fileOrg.getBytes());
        fos.close();
        return "Si";
    }
	
	
	public static String generateRandomString() {
        String uuid = UUID.randomUUID().toString();
        return uuid;
    }
	
	public java.io.File convert(MultipartFile file) throws IOException
	{    
		java.io.File convFile = new java.io.File(file.getOriginalFilename());
	    convFile.createNewFile(); 
	    FileOutputStream fos = new FileOutputStream(convFile); 
	    fos.write(file.getBytes());
	    fos.close(); 
	    return convFile;
	}
}
