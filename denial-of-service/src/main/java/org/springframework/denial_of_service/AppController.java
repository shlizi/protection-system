package org.springframework.denial_of_service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * Application Rest Controller
 * 
 * This controller limits the client for maximum 5 requests per 5 seconds
 * for each client id that received in the request.
 * 
 * In case of more then 5 requests per 5 seconds for the same client id, the controller will 
 * return {@link HttpStatus.SERVICE_UNAVAILABLE}. otherwise {@link HttpStatus.OK} 
 * 
 * @author Lizi
 */
@RestController
public class AppController {
	
	private static final long TTL = 5000;
	private static final long THRESHOLD = 5;
	
	private final ConcurrentMap<String, Frame> map = new ConcurrentHashMap<>();

    @RequestMapping("/")
    public ResponseEntity<?> endpoint(@RequestParam(value = "clientId") String clientId) {
        if (this.map.compute(clientId, this::getFrame).requestCount > THRESHOLD) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        } else {
            return ResponseEntity.ok().build();
		}
    }

    //this function will execute in the compute so it guaranteed thread safety
    Frame getFrame(String clientId, Frame frame) {
    	Frame f = frame != null && Instant.now().isBefore(frame.creationDate.plusMillis(TTL)) ?
                new Frame(frame.creationDate, frame.requestCount+1) :
                new Frame(Instant.now(), 1l);
        return f;
    }

    //this class is immutable
    final class Frame {
        final Instant creationDate;
        final Long requestCount;

        public Frame(Instant creationDate, Long requestCount) {
            this.creationDate = creationDate;
            this.requestCount = requestCount;
        }
    }
}
