package org.springframework.denial_of_service;


import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Rest API Client
 * 
 * perform rest API request with client id, and after completion, wait a random time
 * and perform another request with the same client id. 
 * 
 * The operation will stop, after the user will press a key. 
 * 
 * @author lizi
 *
 */
public class RestAPIClient implements Runnable, RestAPICallback {
	
	final static Logger logger = Logger.getLogger(RestAPIClient.class);
	private static final String URL = "http://localhost:8080?clientId=";
	private int clientId;
	final RestTemplate template = new RestTemplate();

	public RestAPIClient(int clientId) {
		template.setErrorHandler(new CustomErrorHandler());
		this.clientId = clientId;	
	}
	
	@Override
	public void run() {
		ResponseEntity<String> response = template.getForEntity(URL+clientId, String.class);
		logger.debug("client id: "+ clientId + " response code " +  response.getStatusCode());
		notifyCompletion();
	}

	@Override
	public void notifyCompletion() {
		Application.executor.schedule(this, (long)(Math.random() * 1000), TimeUnit.MILLISECONDS);		
	}
}
