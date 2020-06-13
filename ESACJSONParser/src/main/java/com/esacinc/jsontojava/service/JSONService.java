package com.esacinc.jsontojava.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.esacinc.jsontojava.model.JsonRoot;
import com.esacinc.jsontojava.model.JsonRoot.Entry;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

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
		List<Entry> entriesWithRef = new ArrayList<>();
		List<String> referenceIds = new ArrayList<>();
		for (Entry entry : root.getEntry()) {
			System.out.println(entry.getResource().get("resourceType"));
			if (!resourceTypes.contains(entry.getResource().get("resourceType"))) {
				newList.add(entry);
			} else {
				referenceIds.add(entry.getFullUrl());
			}

		}

		for (Entry entry : newList) {
			if (entry.getResource().entrySet().stream()
					.filter(e -> isReferenceIdExists(referenceIds, e.getValue().toString())).findFirst().isPresent()) {
				entriesWithRef.add(entry);
			}
		}

		for (int i = 0; i < entriesWithRef.size(); i++) {
			Entry entry = entriesWithRef.get(i);
			List<String> keys = new ArrayList<>(entry.getResource().keySet());
			for (int j = 0; j < keys.size(); j++) {
				String k = keys.get(j);
				Object v = entry.getResource().get(k);

				if (v instanceof LinkedTreeMap<?, ?>) {

					Map<String, String> map = (LinkedTreeMap<String, String>) v;
					if (map.containsKey("reference")) {
						if (referenceIds.contains(map.get("reference"))) {
							System.out.println("Before LinkedTreeMap ::" + entry.getResource().get(k).toString());
							System.out.println("Before LinkedTreeMap ::" + entry.getResource().toString());
							System.out.println(map.get("reference"));
							if (map.entrySet().size() > 1) {
								entry.getResource().remove(k);
							} else {
								map.remove("reference");
							}
						}
					}

				}

			}
		}
		for (int i = 0; i < entriesWithRef.size(); i++) {
			Entry entry = entriesWithRef.get(i);
			List<String> keys = new ArrayList<>(entry.getResource().keySet());
			for (int j = 0; j < keys.size(); j++) {
				String k = keys.get(j);
				Object v = entry.getResource().get(k);

				if (v instanceof ArrayList<?>) {

					for (Object obj : (ArrayList<LinkedTreeMap<String, Object>>) v) {

						LinkedTreeMap<String, Object> innerMap = (LinkedTreeMap<String, Object>) obj;

						List<String> innerKeys = new ArrayList<>(innerMap.keySet());
						for (int l = 0; l < innerKeys.size(); l++) {
							String key = innerKeys.get(l);
							Object value = innerMap.get(key);

							if (value instanceof LinkedTreeMap<?, ?>) {

								Map<String, String> map = (LinkedTreeMap<String, String>) value;
								if (map.containsKey("reference")) {
									if (referenceIds.contains(map.get("reference"))) {
										System.out.println("Before LinkedTreeMap ::" + innerMap.get(key).toString());
										System.out.println("Before LinkedTreeMap ::" + innerMap.toString());
										if (innerMap.entrySet().size() > 1) {
											innerMap.remove(key);
										} else {
											map.remove("reference");
										}
									}
								}

							} else if (value instanceof ArrayList<?>) {

								for (Object obj1 : (ArrayList<LinkedTreeMap<String, Object>>) value) {
									System.out.println(obj1.toString());
									if (obj1 instanceof LinkedTreeMap<?, ?>) {
										LinkedTreeMap<String, Object> innerMap1 = (LinkedTreeMap<String, Object>) obj1;

										List<String> innerKeys1 = new ArrayList<>(innerMap1.keySet());
										for (int l1 = 0; l1 < innerKeys1.size(); l1++) {
											String key1 = innerKeys1.get(l1);
											Object value1 = innerMap1.get(key1);

											if (value1 instanceof LinkedTreeMap<?, ?>) {

												Map<String, String> map1 = (LinkedTreeMap<String, String>) value1;
												if (map1.containsKey("reference")) {
													if (referenceIds.contains(map1.get("reference"))) {
														System.out.println("Before LinkedTreeMap ::"
																+ innerMap1.get(key1).toString());
														System.out.println(
																"Before LinkedTreeMap ::" + innerMap1.toString());
														if (innerMap1.entrySet().size() > 1) {
															innerMap1.remove(key1);
														} else {
															map1.remove("reference");
														}
													}
												}

											} else if (value1 instanceof String) {
												if (referenceIds.contains((String) value1)) {
													innerMap1.remove(key1);
												}
											}

										}
									}
								}

							}
						}

					}
				}
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

	private boolean isReferenceIdExists(List<String> referenceIds, String content) {
		for (String id : referenceIds) {
			if (content.contains(id)) {
				return true;
			}
		}
		return false;
	}

}