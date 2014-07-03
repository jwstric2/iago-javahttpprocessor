package com.cisco.iago.javahttpprocessor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junit.framework.Assert;

import org.junit.Test;

import scala.Tuple2;
import scala.collection.Seq;

import com.cisco.iago.javahttpprocessor.data.LoadRequest;
import com.cisco.iago.javahttpprocessor.data.LoadTestLine;
import com.twitter.parrot.server.ParrotRequest;

public class LineProcessorTest {
	
	public static final String TEST_HEADER_NAME1 = "name1";
	public static final String TEST_HEADER_NAME2 = "name2";	
	public static final String TEST_HEADER_VALUE1 = "value1";
	public static final String TEST_HEADER_VALUE2 = "value2";	

	public static final LoadRequest.METHOD TEST_METHOD = LoadRequest.METHOD.POST;
	
	public static final String TEST_URI = "/sample/uri";	
	
	public static final String TEST_FILE_DATA = "this is test body data";	
	
	
	@Test
	public void testInvalidParseLine() {
		String line = "{{}";
		boolean exceptionReceived = false;
		try {		
			LineProcessor.parse(line);
		} catch(Exception e) {
			exceptionReceived = true;
		}		
		Assert.assertEquals(true, exceptionReceived);
	}

	@Test
	public void testValidParseLine() {
		String line = "{\"request\":{\"uri\":\"/my/uri\"}}";
		LoadTestLine loadTestLine = LineProcessor.parse(line);
		Assert.assertEquals("/my/uri", loadTestLine.getRequest().getUri());
	}	
	
	@Test
	public void testGetValidParrotRequest() throws IOException {

	    File temp = File.createTempFile("mydata", ".log");
	    // Delete the file when program exits.
	    temp.deleteOnExit();

	    // Write to temp file
	    BufferedWriter out = null;
        out = new BufferedWriter(new FileWriter(temp));
	    out.write(LineProcessorTest.TEST_FILE_DATA);
        out.close();
		
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("{");
	    strBuilder.append("\"request\": {");
	    strBuilder.append(String.format("\"headers\": [{\"name\":\"%s\", \"value\":\"%s\"}, {\"name\":\"%s\", \"value\":\"%s\"}],", LineProcessorTest.TEST_HEADER_NAME1, LineProcessorTest.TEST_HEADER_VALUE1, LineProcessorTest.TEST_HEADER_NAME2, LineProcessorTest.TEST_HEADER_VALUE2));
	    strBuilder.append(String.format("\"method\":\"%s\",", LineProcessorTest.TEST_METHOD.toString()));
	    strBuilder.append(String.format("\"uri\":\"%s\",", LineProcessorTest.TEST_URI));
	    strBuilder.append(String.format("\"data_file\":\"%s\"", temp.toString()));
	    strBuilder.append("}");
	    strBuilder.append("}");
	    
	    
		LineProcessor parser = new LineProcessor(strBuilder.toString());
		ParrotRequest request = parser.getRequest();
		
		//Verify the headers of the request; will add a host header onto the 2 we have
		Seq<Tuple2<String,String>> headers = request.headers();
	    Assert.assertEquals(3, headers.size());
	    scala.collection.Iterator<Tuple2<String,String>> iter = headers.toList().iterator();
	    while (iter.hasNext()) {
	    	Tuple2<String,String> nextTuple = iter.next();
	    	if (nextTuple._1().equals(LineProcessorTest.TEST_HEADER_NAME1)) {
	    		Assert.assertEquals(LineProcessorTest.TEST_HEADER_VALUE1, nextTuple._2());
	    	} else if (nextTuple._1().equals(LineProcessorTest.TEST_HEADER_NAME2)) {
	    		Assert.assertEquals(LineProcessorTest.TEST_HEADER_VALUE2, nextTuple._2());
	    	} else if (nextTuple._1().equals("Host")) {
	    		Assert.assertEquals("localhost:80", nextTuple._2());
	    	} else {
	    		Assert.fail("Unknown tuple header name " + nextTuple._1());
	    	}
	    }
	    
	    //Verify the method
	    Assert.assertEquals(LineProcessorTest.TEST_METHOD.toString(), request.method());

	    //Verify the uri
	    Assert.assertEquals(LineProcessorTest.TEST_URI, request.uri().path());
	
	    //Verify the data body
	    Assert.assertEquals(LineProcessorTest.TEST_FILE_DATA, request.body());	    
	}	
	
	@Test
	public void testNoFileString() {
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("{");
	    strBuilder.append("\"request\": {");
	    strBuilder.append("}");
	    strBuilder.append("}");

	    LineProcessor parser = new LineProcessor(strBuilder.toString());
		ParrotRequest request = parser.getRequest();

		Assert.assertEquals("", request.body());	    
	}
	
	@Test
	public void testRelativeFilePath() throws IOException {
		File file = new File("testRelativeFilePath.log");
		file.deleteOnExit();
		
	    BufferedWriter out = null;
        out = new BufferedWriter(new FileWriter(file));
	    out.write(LineProcessorTest.TEST_FILE_DATA);
        out.close();		
		
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("{");
	    strBuilder.append("\"request\": {");
	    strBuilder.append(String.format("\"data_file\": \"@%s\"", file.toString()));
	    strBuilder.append("}");
	    strBuilder.append("}");		
		
		LineProcessor parser = new LineProcessor(strBuilder.toString());
		ParrotRequest request = parser.getRequest();
	    
		Assert.assertEquals(LineProcessorTest.TEST_FILE_DATA, request.body());	    
	    
	}

	@Test
	public void testAbsoluteFilePath() throws IOException {
		File file = File.createTempFile("testAbsoluteFilePath", "log");
		file.deleteOnExit();
		
	    BufferedWriter out = null;
        out = new BufferedWriter(new FileWriter(file));
	    out.write(LineProcessorTest.TEST_FILE_DATA);
        out.close();		
		
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("{");
	    strBuilder.append("\"request\": {");
	    strBuilder.append(String.format("\"data_file\": \"%s\"", file.toString()));
	    strBuilder.append("}");
	    strBuilder.append("}");		
		
		LineProcessor parser = new LineProcessor(strBuilder.toString());
		ParrotRequest request = parser.getRequest();
	    
		Assert.assertEquals(LineProcessorTest.TEST_FILE_DATA, request.body());	    
	    
	}
	
	
	@Test
	public void testOverrideHostHeader() {
		
		String hostName = "hello.mydomain.com";
		int hostPort = 10;
		
	    StringBuilder strBuilder = new StringBuilder();
	    strBuilder.append("{");
	    strBuilder.append("\"request\": {");
	    strBuilder.append("}");
	    strBuilder.append("}");
		
		LineProcessor parser = new LineProcessor(strBuilder.toString());
		parser.setHostHeader(hostName, hostPort);
		ParrotRequest request = parser.getRequest();
		//Verify the headers of the request; will add a host header onto the 2 we have
		Seq<Tuple2<String,String>> headers = request.headers();
	    Assert.assertEquals(1, headers.size());
	    scala.collection.Iterator<Tuple2<String,String>> iter = headers.toList().iterator();
	    while (iter.hasNext()) {
	    	Tuple2<String,String> nextTuple = iter.next();
	    	if (nextTuple._1().equals("Host")) {
	    		Assert.assertEquals(hostName + ":" + hostPort, nextTuple._2());
	    	} else {
	    		Assert.fail("Unknown tuple header name " + nextTuple._1());
	    	}
	    }

	
		
	}
	
}
