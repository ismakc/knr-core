package es.evilmonkey.kinaro.core.request;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;

/**
 * Implementación por defecto de {@link RequestHandlerMapping}.
 *
 * <p>
 * Este mapeador de manejadores devuelve el primer manejador que encuentre que soporte el
 * comando especificado.
 *
 * <p>
 * Al arrancar el contexto de la aplicación se registran todos los manejadores que
 * implementen {@link RequestHandler}. El orden en el que se registran dependerá de la
 * inyección realizada por el <i>contenedor de IoC</i>
 *
 * @author Ismael El Kadaoui Calvo
 */
@RequiredArgsConstructor
@Component
public class DefaultRequestHandlerMapping implements RequestHandlerMapping {

	private final List<RequestHandler<? extends Request>> handlerList;

	private final Map<String, RequestHandler<Request>> handlerCache = new HashMap<>();

	@SuppressWarnings("unchecked")
	private static RequestHandler<Request> forceCasting(RequestHandler<? extends Request> obj) {
		return (RequestHandler<Request>) obj;
	}

	@Override
	public Optional<RequestHandler<Request>> getHandler(final Request cmd) {
		String cacheKey = cmd.getClass().getName();
		return Optional.ofNullable(this.handlerCache.computeIfAbsent(cacheKey, (k) -> lookupHandler(cmd)));
	}

	private RequestHandler<Request> lookupHandler(Request cmd) {
		return this.handlerList.stream().filter((handler) -> handler.supports(cmd)).findFirst()
				.map(DefaultRequestHandlerMapping::forceCasting).orElse(null);
	}

}
