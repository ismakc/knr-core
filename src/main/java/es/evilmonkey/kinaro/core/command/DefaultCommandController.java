package es.evilmonkey.kinaro.core.request;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para manejar peticiones de comandos.
 *
 * <p>
 * Este controlador es capaz de manejar un sólo comando o cadenas de comando
 *
 * @author Ismael El Kadaoui Calvo
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/commands")
public class DefaultRequestController {

	private final RequestChainHandler knrChainCommandHandler;

	/**
	 * Gestiona un comando.
	 * @param request comando
	 */
	@PostMapping
	public void handle(@RequestBody Request request) {
		this.knrChainCommandHandler.handle(new Request[] { request });
	}

	/**
	 * Gestiona una cadena ordenada de comandos.
	 * @param requests lista de comandos
	 */
	@PostMapping({ "/chain" })
	public void handle(@RequestBody Request[] requests) {
		this.knrChainCommandHandler.handle(requests);
	}

}
