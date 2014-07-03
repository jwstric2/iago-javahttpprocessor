package com.cisco.iago.javahttpprocessor.data;

import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class LoadTestRequest {
	
	@Test
	public void testNotNullGsonInstantiation() {
	    GsonBuilder objGsonBuilder = new GsonBuilder();
	    Gson gson = objGsonBuilder.create();

	    String json = String.format("{ }");
	    LoadRequest request = gson.fromJson(json, LoadRequest.class);

	    //Headers should not be null if not given
	    Assert.assertNotNull(request.getHeaders());
	    Assert.assertEquals(0, request.getHeaders().size());	    
	 
	    //The default method should be GET
	    Assert.assertNotNull(request.getMethod());
	    Assert.assertEquals(LoadRequest.METHOD.GET, request.getMethod());
	    
	    //Default uri is ""
	    Assert.assertNotNull(request.getUri());
	    Assert.assertEquals("", request.getUri());	    
	
	    //Default data body is ""
	    Assert.assertNotNull(request.getDataFile());
	    Assert.assertEquals("", request.getDataFile());
	}

	@Test
	public void testValidGsonInstantiation() {
	    GsonBuilder objGsonBuilder = new GsonBuilder();
	    Gson gson = objGsonBuilder.create();

	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("{");
	    strBuilder.append("\"headers\": [{\"name\":\"name1\", \"value\":\"value1\"}, {\"name\":\"name2\", \"value\":\"value2\"}],");
	    strBuilder.append("\"method\":\"POST\",");
	    strBuilder.append("\"uri\":\"/sample/uri\",");
	    strBuilder.append("\"data_file\":\"config/data\"");
	    strBuilder.append("}");
	    
	    String json = strBuilder.toString();
	    LoadRequest request = gson.fromJson(json, LoadRequest.class);

	    //2 Headers given
	    Assert.assertEquals(2, request.getHeaders().size());
	    
	    int i=0;
	    for(Header header : request.getHeaders()) {
	    	i++;
	    	Assert.assertEquals("name" + i, header.getName());
	    	Assert.assertEquals("value" + i, header.getValue());
	    }
	    
	    Assert.assertEquals(LoadRequest.METHOD.POST, request.getMethod());
	    Assert.assertEquals("/sample/uri", request.getUri());	    
	    Assert.assertEquals("config/data", request.getDataFile());	    
	}	
	
	@Test
	public void testSetGetHeaders() {
		List<Header> headers = new LinkedList<Header>();
		LoadRequest request = new LoadRequest();
		
		//Add 2 headers and verify on get we set then get a list of 2
		Header header = new Header();
		header.setName("name1");
		header.setValue("value1");
		headers.add(header);
		
		header = new Header();
		header.setName("name2");
		header.setValue("value2");
		headers.add(header);
		
		request.setHeaders(headers);
		Assert.assertEquals(2, request.getHeaders().size());
		
	}
	
	@Test
	public void testSetGetMethod() {
		LoadRequest request = new LoadRequest();

		request.setMethod(LoadRequest.METHOD.GET);
		Assert.assertEquals(LoadRequest.METHOD.GET, request.getMethod());

		request.setMethod(LoadRequest.METHOD.POST);
		Assert.assertEquals(LoadRequest.METHOD.POST, request.getMethod());

		request.setMethod(LoadRequest.METHOD.PUT);
		Assert.assertEquals(LoadRequest.METHOD.PUT, request.getMethod());
		
		request.setMethod(LoadRequest.METHOD.DELETE);
		Assert.assertEquals(LoadRequest.METHOD.DELETE, request.getMethod());
	
	}
	
	@Test
	public void testSetGetUri() {
		LoadRequest request = new LoadRequest();
		
		request.setUri("/sample/uri");
		Assert.assertEquals("/sample/uri", request.getUri());
	}
	
}
