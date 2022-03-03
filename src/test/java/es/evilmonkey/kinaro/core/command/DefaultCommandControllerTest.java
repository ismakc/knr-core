package es.evilmonkey.kinaro.core.request;

import java.util.List;

import com.github.valfirst.slf4jtest.Assertions;
import com.github.valfirst.slf4jtest.TestLogger;
import com.github.valfirst.slf4jtest.TestLoggerFactory;
import es.evilmonkey.kinaro.core.request.DefaultRequestControllerTest.SpyRequestHandler.SpyRequest;
import es.evilmonkey.kinaro.core.request.PingRequestHandler.PingRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jext.Level;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultRequestControllerTest {

	DefaultRequestController defaultRequestController;

	SpyRequestHandler spyRequestHandler = new SpyRequestHandler();

	TestLogger logger = TestLoggerFactory.getTestLogger(DefaultRequestChainHandler.class);

	@BeforeEach
	void setUp() {
		List<RequestHandler<? extends Request>> handlerList = List.of(new PingRequestHandler(), this.spyRequestHandler);
		var defaultRequestHandlerMapping = new DefaultRequestHandlerMapping(handlerList);
		var defaultRequestChainHandler = new DefaultRequestChainHandler(defaultRequestHandlerMapping);
		this.defaultRequestController = new DefaultRequestController(defaultRequestChainHandler);
	}

	@Test
	void test_handleChainRequest() {
		Request firstRequestStub = new PingRequest("First request");
		Request secondRequestStub = new SpyRequest();

		this.defaultRequestController.handle(new Request[] { firstRequestStub, secondRequestStub });

		assertThat(this.spyRequestHandler.rq).isEqualTo(secondRequestStub);
	}

	@Test
	void test_handleRequest() {
		Request secondRequestStub = new SpyRequest();
		this.defaultRequestController.handle(secondRequestStub);

		assertThat(this.spyRequestHandler.rq).as("Request was not the expected one").isEqualTo(secondRequestStub);
	}

	@Test
	void test_unhandledRequest_isLogged() {
		Request unhandledRequestHandler = new UnhandledRequest();
		this.defaultRequestController.handle(unhandledRequestHandler);

		Assertions.assertThat(this.logger).hasLogged(Level.WARN,
				"Not found command handler for class {}. Command will be ignored: {}",
				unhandledRequestHandler.getClass().getName(), unhandledRequestHandler);
	}

	static class SpyRequestHandler implements RequestHandler<SpyRequest> {

		Request rq = null;

		@Override
		public void invoke(SpyRequest rq) {
			this.rq = rq;
		}

		record SpyRequest() implements Request {
		}

	}

	record UnhandledRequest() implements Request {
	}

}
