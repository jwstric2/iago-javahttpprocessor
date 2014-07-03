package com.cisco.iago.javahttpprocessor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.handler.codec.http.HttpResponse;

import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import com.cisco.iago.javahttpprocessor.data.Header;
import com.cisco.iago.javahttpprocessor.data.LoadRequest;
import com.cisco.iago.javahttpprocessor.data.LoadTestLine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.parrot.config.ParrotServerConfig;
import com.twitter.parrot.server.ParrotRequest;
import com.twitter.parrot.util.Uri;
import com.twitter.util.Promise;

/**
 * 
 * Parses a line and instantiates ParrotRequest and
 * post Response Validators
 * 
 * @author jwstric2
 */

public class LineProcessor {
	
	private LoadTestLine loadTestLine = null;
	private String line = "";
	private String hostName = "localhost";
	private int hostPort = 80;
	
	protected LineProcessor(String line) {
		this.line = line;
		this.loadTestLine = LineProcessor.parse(line);
	}
	
	public ParrotRequest getRequest() {
		ParrotRequest request;
		LoadRequest loadRequest = this.loadTestLine.getRequest();
		
		
		//Host Header; pull from configuration
		Tuple2<String, Object> hostHeaderTuple = new Tuple2<String,Object>(hostName, hostPort);
		Option<Tuple2<String, Object>> optHostHeader = new Some<Tuple2<String, Object>>(hostHeaderTuple);

		//Headers
		List <Tuple2 <String,String>> headersList = new LinkedList<Tuple2 <String,String>>();
		//TODO remove these lines later when making generic
		for (Header header : loadRequest.getHeaders()) {
			headersList.add(new Tuple2<String,String>(header.getName(),header.getValue()));
		}
		Seq <Tuple2 <String,String>> headerSeq = JavaConversions.asScalaBuffer(headersList).toSeq();
		
		//Uri and opt params		
		Seq<Tuple2<String, String>> uriOptParams = JavaConversions.asScalaBuffer(new LinkedList<Tuple2<String,String>>()).toSeq();
		Uri uri = new Uri(loadRequest.getUri(), uriOptParams);
		
		//Timestamp
		Option<Object> timeStamp = new Some<Object>(new Long(new Date().getTime()));

		//ThriftClientRequest
		ThriftClientRequest thriftClientRequest = new ThriftClientRequest(new byte[0], false);

		//Promise, the writable container which completes a future; in this case HttpResponse
		Promise<HttpResponse> response = new Promise<HttpResponse>();

		//Cookies
		Seq<Tuple2<String, String>> cookies = JavaConversions.asScalaBuffer(new LinkedList<Tuple2<String,String>>()).toSeq();

		//Method
		String method = loadRequest.getMethod().toString();
		
		//String body
		String body = "";
		
		Path path;
		byte[] data;
		if (! "".equals(loadRequest.getDataFile())) {
			//Rules of the game, if begins with @ as defined in documentation
			//we attempt to get it from the system user.dir, else we pull an
			//absolute path			
			if (loadRequest.getDataFile().startsWith("@")) {
				path = Paths.get(System.getProperty("user.dir"), loadRequest.getDataFile().replaceFirst("@", ""));
			} else {
				path = Paths.get(loadRequest.getDataFile());
			}			
			try {
				data = Files.readAllBytes(path);
			} catch(IOException e) {
				throw new RuntimeException("Unable to read file " + path.toFile().toString(), e);
			}
			body = new String(data);
		}
		
		request = new ParrotRequest(optHostHeader, headerSeq, 
				uri, line, timeStamp, thriftClientRequest, response, cookies, method,
				body, 1);
		
		return request;
		
	}

	/**
	 * Used to explicitly define the host header option used in forming
	 * a ParrotRequest.  If the line processed contains the Host header
	 * this will override the values given here
	 * 
	 * @param hostName
	 * @param hostPort
	 */
	public void setHostHeader(String hostName, int hostPort) {
		this.hostName = (hostName == null) ? "" : hostName;
		this.hostPort = hostPort;
	}
	
	protected static LoadTestLine parse(String line) {		
	    GsonBuilder objGsonBuilder = new GsonBuilder();
	    Gson gson = objGsonBuilder.create();
	    
	    LoadTestLine loadTestLine = gson.fromJson(line, LoadTestLine.class);
	    return loadTestLine;	
	}
	
	
	
}
