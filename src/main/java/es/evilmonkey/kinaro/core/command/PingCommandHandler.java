package es.evilmonkey.kinaro.core.request;

import es.evilmonkey.kinaro.core.request.PingRequestHandler.PingRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

/**
 * Controlador de una petición ping que escribe en el log de la aplicación.<br>
 * Ejemplo:
 *
 * <p>
 *
 * <pre>
 * {
 *   "@path": "/ping",
 *   "text": "Hola Mundo!"
 * }
 * </pre>
 *
 * @author Ismael El Kadaoui Calvo
 */
@Slf4j
@Component
public class PingRequestHandler implements RequestHandler<PingRequest> {

	/**
	 * Escribe en el log de aplicación el texto especificado en el comando.
	 * @param cmd comando ping con un texto
	 */
	@Override
	public void invoke(PingRequest cmd) {
		log.info("You have successfully ping the server: {}", cmd.text());
	}

	/**
	 * Comando ping.
	 * @param text texto de echo.
	 */
	@RequestMapping("/ping")
	public record PingRequest(String text) implements Request {
	}

}
