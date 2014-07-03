package com.cisco.iago.javahttpprocessor.data;

import junit.framework.Assert;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class HeaderTest {
	
	private final static String TEST_NAME = "content-type";
	private final static String TEST_VALUE = "application/json";
	
	@Test
	public void testNotNullGsonInstantiation() {
	    GsonBuilder objGsonBuilder = new GsonBuilder();
	    Gson gson = objGsonBuilder.create();

	    //Value should not be null if not given
	    String json = String.format("{\"name\":\"%s\"}", HeaderTest.TEST_NAME);
	    Header header = gson.fromJson(json, Header.class);
	    Assert.assertEquals(HeaderTest.TEST_NAME, header.getName());
	    Assert.assertNotNull(header.getValue());
	    Assert.assertEquals("", header.getValue());
	    
	    //Name should not be null if not given
	    json = String.format("{\"value\":\"%s\"}", HeaderTest.TEST_VALUE);
	    header = gson.fromJson(json, Header.class);
	    Assert.assertNotNull(header.getName());
	    Assert.assertEquals("",header.getName());
	    Assert.assertEquals(HeaderTest.TEST_VALUE, header.getValue());

	    
	}
	
	@Test
	public void testSetGetHeaderName() {
		Header header = new Header();
		
		header.setName(HeaderTest.TEST_NAME);
		header.setValue(HeaderTest.TEST_VALUE);
		Assert.assertEquals(TEST_NAME, header.getName());
		
	}

	@Test
	public void testSetGetHeaderValue() {
		Header header = new Header();
		
		header.setValue(HeaderTest.TEST_VALUE);
		header.setName(HeaderTest.TEST_NAME);
		
		Assert.assertEquals(TEST_VALUE, header.getValue());
		
	}
	
	
}
