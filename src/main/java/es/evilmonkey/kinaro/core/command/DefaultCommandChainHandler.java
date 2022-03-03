package es.evilmonkey.kinaro.core.request;

import java.util.Arrays;
import java.util.function.Consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class DefaultRequestChainHandler implements RequestChainHandler {

	private final RequestHandlerMapping handlerMapping;

	@Override
	public void handle(Request[] requests) {
		Arrays.stream(requests).forEachOrdered((request) -> this.handlerMapping.getHandler(request)
				.ifPresentOrElse(invokeHandler(request), ignore(request)));
	}

	private Consumer<? super RequestHandler<Request>> invokeHandler(Request request) {
		return (handler) -> handler.invoke(request);
	}

	private Runnable ignore(Request cmd) {
		return () -> log.warn("Not found command handler for class {}. Command will be ignored: {}",
				cmd.getClass().getName(), cmd);
	}

}
