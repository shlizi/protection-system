package org.springframework.denial_of_service;

import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.BasicConfigurator;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

/**
 * Main Spring Boot Application Class
 * 
 *  Creates Thread pool, and execute simultaneous requests by client id. 
 *  requests will keep sending until key press  by the user.
 *  It will gracefully drain all the requests before shutting down. 

 * @author Lizi
 *
 */
@SpringBootApplication
public class Application {
	
	public static final int MAX_THREAD = 10;
	public static ScheduledExecutorService  executor = Executors.newScheduledThreadPool(MAX_THREAD);
	public static void main(String[] args) {
		BasicConfigurator.configure();
		SpringApplication.run(Application.class, args);
  }

  @Bean
  public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    return args -> {
    	
    	Scanner scanner = new Scanner(System.in);
    	System.out.println("Please enter the number for HTTP clients to simulate: ");
        while (!scanner.hasNextInt()) {
            System.out.println("That's not a number! please enter a number: ");
            scanner.next();
        }
        int numberOfClients = scanner.nextInt();

        for (int i = 1; i <= numberOfClients; i++) {
        	Runnable apiClient = new RestAPIClient(i);
        	executor.execute(apiClient);
        }
        System.out.println("Press any key to finish...");
        scanner.next();

        System.out.println("Shutting down gracefully");
        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);
        scanner.close();
    };	
  }
}
