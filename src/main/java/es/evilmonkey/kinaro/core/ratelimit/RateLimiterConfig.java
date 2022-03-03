package es.evilmonkey.kinaro.core.ratelimit;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.validation.annotation.Validated;

@Configuration
@EnableConfigurationProperties
public class RateLimiterConfig {

	@Bean
	@Validated
	@ConfigurationProperties(prefix = "kinaro.rate-limiter")
	Map<String, Integer> knrRateLimiterConfigurations(Map<String, Integer> knrRateLimiterConfigurations) {
		return knrRateLimiterConfigurations;
	}

	@Bean
	ThreadPoolTaskScheduler rateLimiterTaskScheduler() {
		ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
		taskScheduler.setPoolSize(1);
		taskScheduler.setThreadNamePrefix("KnrRateLimiterThread-");
		return taskScheduler;
	}

}
