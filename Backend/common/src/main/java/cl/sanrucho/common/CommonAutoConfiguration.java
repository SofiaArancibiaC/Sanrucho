package cl.sanrucho.common;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

import cl.sanrucho.common.security.JwtProperties;

@AutoConfiguration
@ComponentScan(basePackages = "cl.sanrucho.common")
@EnableConfigurationProperties(JwtProperties.class)
public class CommonAutoConfiguration {

}