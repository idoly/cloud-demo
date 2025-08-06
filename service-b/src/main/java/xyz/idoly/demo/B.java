package xyz.idoly.demo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.tracing.annotation.NewSpan;
import jakarta.annotation.Resource;

@RefreshScope
@RestController
@Configuration
@EnableDiscoveryClient
@SpringBootApplication
public class B {

	@Resource
	private RestClient restClient;

	@Value("${k1}")
	private String k1;

	@GetMapping("/k1")
	public String k1() {
		return k1;
	}

	@NewSpan("k2")
	@GetMapping("/k2")
    @RateLimiter(name = "", fallbackMethod = "handleRateLimit")
	public String k2() {
		return restClient.get().uri("http://service-c/k2").retrieve().body(String.class);
	}

	public String handleRateLimit(Throwable t) {
		return "custom rateLimiter fallback";
	}

	public static void main(String[] args) {
		SpringApplication.run(B.class, args);
	}

	@Configuration
	public class restClientBuilderConfig {
		
		@LoadBalanced
		@Bean
		public RestClient.Builder restClientBuilder(RestClientBuilderConfigurer restClientBuilderConfigurer) {
			return restClientBuilderConfigurer.configure(RestClient.builder());
		}

		@Bean
		public RestClient restClient(RestClient.Builder restClientBuilder) {
			return restClientBuilder.build();
		}

	}

}


