package ru.mustafa.messenger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point class for the Messenger application.
 *
 * @author Mustafa
 * @version 1.0.
 */
@SpringBootApplication
public class MessengerApplication {

	/**
	 * Standard main method that starts the Spring Boot application.
	 *
	 * @param args the command-line arguments array passed to the application
	 */
	public static void main(String[] args) {
		SpringApplication.run(MessengerApplication.class, args);
	}

}
