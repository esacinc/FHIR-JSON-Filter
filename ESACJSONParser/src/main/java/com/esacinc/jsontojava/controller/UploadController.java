package com.esacinc.jsontojava.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.esacinc.jsontojava.service.JSONService;
/**
 * 
 * @author saibabu
 */
@RestController
public class UploadController {

	private final JSONService jsonService;

	public UploadController(JSONService jsonService) {
		this.jsonService = jsonService;
	}

	@RequestMapping("/home")
	public String welcome(Map<String, Object> model) {
		return "index.html";
	}

	@PostMapping("/upload")
	public void upload(@RequestParam("file") MultipartFile file, @RequestParam("resourceType") String resourceType,
			HttpServletResponse response) throws Exception {
		jsonService.upload(file, resourceType,response);
	}
}
