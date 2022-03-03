package es.evilmonkey.kinaro.core.request;

import java.util.Optional;

/**
 * Interface para ser implementada por objetos que definan un mapeo entre comandos y
 * objetos manejadores.
 *
 * @author Ismael El Kadaoui Calvo
 * @see DefaultRequestHandlerMapping
 */
public interface RequestHandlerMapping {

	/**
	 * Devuelve un manejador para el comando.
	 * @param cmd comando
	 * @return un optional de un {@link RequestHandler} registrado que soporte el comando
	 * especificado.<br>
	 * Podría devolverse un optional vacío.
	 */
	Optional<RequestHandler<Request>> getHandler(Request cmd);

}
