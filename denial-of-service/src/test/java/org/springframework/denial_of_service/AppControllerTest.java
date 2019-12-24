package org.springframework.denial_of_service;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class AppControllerTest {
	
	public static ScheduledExecutorService  executor = Executors.newScheduledThreadPool(Application.MAX_THREAD);
	AppController app = new AppController();

  @Test
  public void TestOneRequest() {
	  
	  ResponseEntity<?> response = app.endpoint("1");
	  Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
  }
  
  @Test
  public void TestMultipleRequestsFromSameClient() {
	  
	  runMultipleRequests(5, "1", HttpStatus.OK);
	  runMultipleRequests(1, "1", HttpStatus.SERVICE_UNAVAILABLE);
  }
  
  @Test
  public void TestMultipleRequestsFromSameClientWithDelay() throws InterruptedException {
	  
	  runMultipleRequests(5, "1", HttpStatus.OK);
	  //sleep 5 seconds
	  Thread.sleep(5000l);
	  runMultipleRequests(5, "1", HttpStatus.OK);
	  runMultipleRequests(1, "1", HttpStatus.SERVICE_UNAVAILABLE);
  }
  
  @Test
  public void TestMultipleRequestsFromMultipleClientIds() {
	  runMultipleRequests(5, "1", HttpStatus.OK);
	  runMultipleRequests(4, "2", HttpStatus.OK);
	  runMultipleRequests(5, "3", HttpStatus.OK);
	  
  }
  
  @Test
  public void TestMultiThreaded() throws InterruptedException {
	  execute(10);
	  Thread.sleep(5000l);
	  executor.shutdown();
	  executor.awaitTermination(10, TimeUnit.SECONDS);
	  
  }	
  
  private ResponseEntity<?> runMultipleRequests(int numberOfRequests, String clientId, HttpStatus status) {
	  ResponseEntity<?> response = null;
	  
	  for (int i = 0; i< numberOfRequests; i++) {
		  response = app.endpoint(clientId);
		  Assert.assertEquals(response.getStatusCode(), status);
	  }
	  return response;
}
  
  private void execute(int numberOfClients) {
	  
      for (int i = 1; i <= numberOfClients; i++) {
      	Runnable apiClient = new RestAPIClient(i);
      	executor.execute(apiClient);
      }  
  } 
}
