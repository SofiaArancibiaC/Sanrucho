package cl.sanrucho.pedidos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient
@EnableFeignClients(basePackages = "cl.sanrucho.pedidos.client")
@SpringBootApplication
public class TiendaPedidosApplication {

	public static void main(String[] args) {
		SpringApplication.run(TiendaPedidosApplication.class, args);
	}

}
