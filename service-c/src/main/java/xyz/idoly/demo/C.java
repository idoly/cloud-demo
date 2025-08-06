package xyz.idoly.demo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.micrometer.tracing.annotation.NewSpan;

@RefreshScope
@RestController
@Configuration
@EnableDiscoveryClient
@SpringBootApplication
public class C {

	@Value("${k2}")
	private String k2;

	@NewSpan("k2")
	@GetMapping("/k2")
	public String k2() {
		return k2;
	}

	public static void main(String[] args) {
		SpringApplication.run(C.class, args);
	}

}
