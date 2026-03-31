package com.example.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@RestController
public class Controller {
	
	
	
	@Autowired
	SystemConverter file;
	
	
	
	@PostMapping("/word")
	public ResponseEntity<Resource> toword(@RequestParam("file") MultipartFile fil) throws IOException {


	    return file.enviar(fil, file);
	    
	    
	}


	
	
	
	

}
