package cl.sanrucho.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.validation.autoconfigure.ValidationAutoConfiguration;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication(exclude = {ValidationAutoConfiguration.class})
@EnableEurekaServer
public class TiendaEurekaApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaEurekaApplication.class, args);
	}

}
