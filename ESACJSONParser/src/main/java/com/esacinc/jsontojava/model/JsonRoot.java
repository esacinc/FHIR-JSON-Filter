package com.esacinc.jsontojava.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author saibabu
 */
public class JsonRoot implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String resourceType;
	private String type;
	private List<Entry> entry;

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Entry> getEntry() {
		return entry;
	}

	public void setEntry(List<Entry> entry) {
		this.entry = entry;
	}

	public class Entry {
		private String fullUrl;
		private Map<String, Object> resource;
		private Request request;

		public String getFullUrl() {
			return fullUrl;
		}

		public void setFullUrl(String fullUrl) {
			this.fullUrl = fullUrl;
		}

		public Map<String, Object> getResource() {
			return resource;
		}

		public void setResource(Map<String, Object> resource) {
			this.resource = resource;
		}

		public Request getRequest() {
			return request;
		}

		public void setRequest(Request request) {
			this.request = request;
		}

	}

	public class Request {
		private String method;
		private String url;

		public String getMethod() {
			return method;
		}

		public void setMethod(String method) {
			this.method = method;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

	}

}
