package com.esacinc.jsontojava.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.esacinc.jsontojava.model.JsonRoot;
import com.esacinc.jsontojava.model.JsonRoot.Entry;
import com.google.gson.Gson;

/**
 * 
 * @author saibabu
 */
@Service
public class JSONService {

	public JSONService() {
	}

	public void upload(MultipartFile file, String resourceType, HttpServletResponse response) throws Exception {

		BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"));
		JsonRoot root = new Gson().fromJson(br, JsonRoot.class);

		System.out.println(root.getResourceType());
		List<String> resourceTypes = new ArrayList<String>();
		if (resourceType != null) {
			resourceTypes = Arrays.asList(resourceType.split(","));
		}

		List<Entry> newList = new ArrayList<>();
		for (Entry entry : root.getEntry()) {
			System.out.println(entry.getResource().get("resourceType"));
			if (!resourceTypes.contains(entry.getResource().get("resourceType"))) {
				newList.add(entry);
			}

		}
		root.setEntry(newList);

		String jsonContent = new Gson().toJson(root);
		String filename = file.getName() + "_updated";
		try {
			response.setHeader("Pragma", "public");
			response.setHeader("Expires", "0");
			response.setHeader("cache-control", "must - revalidate, post - check = 0, pre - check = 0");
			response.setHeader("content-type", "application/json;charset=utf-8");
			response.setHeader("content-disposition", "attachment; filename = " + filename + ".json");
			response.setHeader("Content - Transfer - Encoding", "binary");

			byte[] zipBytes = jsonContent.getBytes();
			OutputStream outStream = response.getOutputStream();
			outStream.write(zipBytes);
			outStream.close();
			response.flushBuffer();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
