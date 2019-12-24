package org.springframework.denial_of_service;

/**
 * 
 * Callback interface, to indicate when a task has finished executing. 
 * @author Lizi
 *
 */
public interface RestAPICallback {
	
	/**
	 * Call this function when a task is completed
	 */
	void notifyCompletion();

}
