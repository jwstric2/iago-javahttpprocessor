package com.cisco.iago.javahttpprocessor.data;

import com.google.gson.annotations.SerializedName;

/**
 * Gson representation of http load test line
 * @author jwstric2
 *
 */

public class LoadTestLine {
    @SerializedName("request")
    private LoadRequest request = new LoadRequest();

    public LoadTestLine() {
    	
    }

	public LoadRequest getRequest() {
		return request;
	}

	public void setRequest(LoadRequest request) {
		this.request = request;
	}
}
