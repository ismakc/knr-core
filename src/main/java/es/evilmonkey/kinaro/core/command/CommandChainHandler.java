package es.evilmonkey.kinaro.core.request;

/**
 * Interfaz para los objetos que implementen cadenas de controladores de peticiones.
 *
 * @author Ismael EL Kadaoui Calvo
 * @see DefaultRequestChainHandler
 */
public interface RequestChainHandler {

	/**
	 * Maneja la ejecución de una lista de peticiones.
	 * @param requests lista de peticiones
	 */
	void handle(Request[] requests);

}
