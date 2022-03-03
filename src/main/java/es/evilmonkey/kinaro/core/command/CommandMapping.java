package es.evilmonkey.kinaro.core.request;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación requerida por las implementaciones de {@link Request} para poder mapear un
 * comando en formato json con el subtipo de {@link Request} anotado.
 *
 * <p>
 * El json del comando <strong>debe</strong> contener una propiedad <i>@path</i>
 *
 * @author Ismael El Kadaoui Calvo
 * @see RequestMappingConfiguration.DefaultRequestMapper
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMapping {

	String value();

}
