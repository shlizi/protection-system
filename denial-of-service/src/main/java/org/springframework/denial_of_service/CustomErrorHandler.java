package org.springframework.denial_of_service;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

/**
 * Custom error handler, for indication that {@link HttpStatus.SERVICE_UNAVAILABLE} doesn't consider
 * to be an error, and the program should continue to run.
 * 
 * @author Lizi
 *
 */
public class CustomErrorHandler implements ResponseErrorHandler {

	@Override
	public boolean hasError(ClientHttpResponse response) throws IOException {
		HttpStatus status = response.getStatusCode();
		if (status.equals(HttpStatus.SERVICE_UNAVAILABLE) || status.equals(HttpStatus.OK)) {
			return false;
		}
		return true;
	}

	@Override
	public void handleError(ClientHttpResponse response) throws IOException {}

}
