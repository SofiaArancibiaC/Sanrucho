package cl.sanrucho.catalogo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class TiendaCatalogoApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaCatalogoApplication.class, args);
	}

}
