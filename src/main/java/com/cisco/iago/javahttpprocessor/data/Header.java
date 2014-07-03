package com.cisco.iago.javahttpprocessor.data;

import com.google.gson.annotations.SerializedName;

/**
 * Gson representation of http header object
 * @author jwstric2
 *
 */
public class Header {
	@SerializedName("name")
	private String name = "";
	@SerializedName("value")
	private String value = "";
	
	public Header() {
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
