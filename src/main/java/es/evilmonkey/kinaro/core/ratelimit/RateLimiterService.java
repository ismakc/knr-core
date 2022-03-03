package es.evilmonkey.kinaro.core.ratelimit;

import java.time.Duration;
import java.util.Map;

import javax.annotation.PostConstruct;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class RateLimiterService {

	private final Map<String, Integer> knrRateLimiterConfigurations;

	private RateLimiterRegistry rateLimiterRegistry;

	@PostConstruct
	public void init() {
		this.knrRateLimiterConfigurations.forEach((k, v) -> {
			RateLimiterConfig config = RateLimiterConfig.custom().limitRefreshPeriod(Duration.ofSeconds(5))
					.limitForPeriod(1).timeoutDuration(Duration.ofSeconds(3600)).build();
			this.rateLimiterRegistry = RateLimiterRegistry.of(config);
		});
	}

	@SneakyThrows
	public void waitUntilAcquire(String tokenKey) {
		RateLimiter rateLimiter = this.rateLimiterRegistry.rateLimiter(tokenKey);
		rateLimiter.acquirePermission();
		log.debug("Acquired token from bucket. [tokenKey={}]", tokenKey);
	}

}
