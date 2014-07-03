package com.cisco.iago.javahttpprocessor;

import com.twitter.parrot.server.ParrotRequest;
import com.twitter.parrot.server.ParrotService;
import com.twitter.util.FutureEventListener;
import com.twitter.util.Promise;

import org.jboss.netty.handler.codec.http.HttpResponse;

import scala.Option;
import scala.Some;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.convert.WrapAsJava$;

import com.twitter.parrot.processor.RecordProcessor;
import com.twitter.parrot.util.Uri;
import com.twitter.finagle.thrift.ThriftClientRequest;
import com.twitter.logging.Logger;
import com.twitter.parrot.config.ParrotServerConfig;

import scala.collection.JavaConversions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * parrotService: ParrotService[ParrotRequest, HttpResponse],
                  configuration: ParrotServerConfig[ParrotRequest, HttpResponse]
 * @author jwstric2
 *
 */

public class ComplexHttpRecordProcessor implements RecordProcessor {

	private static Logger log = Logger.get(ComplexHttpRecordProcessor.class);
	
	private ParrotService<ParrotRequest, HttpResponse> parrotService; 
	private ParrotServerConfig<ParrotRequest, HttpResponse> configuration;
	
	public ComplexHttpRecordProcessor(ParrotService<ParrotRequest, HttpResponse> parrotService,
			ParrotServerConfig<ParrotRequest, HttpResponse> configuration) {
		this.parrotService = parrotService;
		this.configuration = configuration;
	}
	
	@Override
	public void processLines(Seq<String> sequenceLines) {
		List<String> listLines = WrapAsJava$.MODULE$.seqAsJavaList(sequenceLines);
		
		String strHostHeader = configuration.httpHostHeader();
		Integer intHostHeaderPort = configuration.httpHostHeaderPort();		
	
		
		for (String strLine : listLines) {
			
			log.debug(String.format("Processing line %s\n", strLine), JavaConversions.asScalaBuffer(new LinkedList<Object>()).toSeq());
			LineProcessor lineProcessor = new LineProcessor(strLine);
			lineProcessor.setHostHeader(strHostHeader, intHostHeaderPort);
			
			ParrotRequest request = lineProcessor.getRequest();
			parrotService.queue().addRequest(request).addEventListener(new FutureEventListener<HttpResponse>() {

				@Override
				public void onFailure(Throwable arg0) {
					log.debug("Failure callback\n", JavaConversions.asScalaBuffer(new LinkedList<Object>()).toSeq());
					
				}

				@Override
				public void onSuccess(HttpResponse arg0) {
					log.debug("Success callback\n", JavaConversions.asScalaBuffer(new LinkedList<Object>()).toSeq());
					
				}
				
			});
			
		}
		
		
	}

	@Override
	public void shutdown() {
		log.debug("Preparing to shutdown", JavaConversions.asScalaBuffer(new LinkedList<Object>()).toSeq());
	}

	@Override
	public void start() {
		
		log.debug("Preparing to start", JavaConversions.asScalaBuffer(new LinkedList<Object>()).toSeq());
	}

}
