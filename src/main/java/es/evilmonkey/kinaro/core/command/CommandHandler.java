package es.evilmonkey.kinaro.core.request;

import java.util.Optional;

import org.springframework.data.util.ClassTypeInformation;

/**
 * Interface para componentes que manejen comandos {@link Request}.
 *
 * @param <T> implementación de {@link Request} que es capaz de manejar este manejador. No
 * es obligatorio especificarla, dado que los criterios para que este manejador soporte un
 * comando, no necesariamente están ligados al tipo del commando. Sin embargo, en la
 * implementación por defecto (ver documentación del método {@link #supports(Request)}
 * @author Ismael EL Kadaoui Calvo
 */
public interface RequestHandler<T extends Request> {

	/**
	 * Indica si la clase soporta el manejo del comando especificado.
	 *
	 * <p>
	 * La implementación ofrecida por defecto en esta interface valida si la clase es una
	 * instancia del parámetro <i>T</i> de la clase
	 * @param cmd comando sobre el que se consulta el soporte
	 * @return <strong>true</strong> si esta clase es capaz de manejar el comando
	 * especificado.<br>
	 * <strong>false</strong> en cualquier otro cosa
	 */
	default boolean supports(Request cmd) {
		return Optional
				.ofNullable(ClassTypeInformation.from(this.getClass()).getSuperTypeInformation(RequestHandler.class))
				.map((typeInformation) -> typeInformation.getTypeArguments().get(0).getType().isInstance(cmd))
				.orElse(false);
	}

	void invoke(T cmd);

}
