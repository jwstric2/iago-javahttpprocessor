package com.cisco.iago.javahttpprocessor.data;

import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

/**
 * Gson representation of a http load request
 * @author jwstric2
 *
 */

public class LoadRequest {
	
	public static enum METHOD {
		GET,
		POST,
		PUT,
		DELETE
	};
	
	@SerializedName("headers")
	private List<Header> headers = new LinkedList<Header>();	
	@SerializedName("method")
	private METHOD method = LoadRequest.METHOD.GET;
	@SerializedName("uri")
	private String uri = "";
	@SerializedName("data_file")
	private String dataFile = "";
	
	public LoadRequest() {
		
	}

	public List<Header> getHeaders() {
		return headers;
	}

	public void setHeaders(List<Header> headers) {
		this.headers = headers;
	}

	public METHOD getMethod() {
		return method;
	}

	public void setMethod(METHOD method) {
		this.method = method;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getDataFile() {
		return dataFile;
	}

	public void setDataFile(String dataFile) {
		this.dataFile = dataFile;
	}
}
