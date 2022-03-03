package es.evilmonkey.kinaro.core.request;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Interfaz base para las clases que implementen un comando.
 *
 * <p>
 * Un comando en formato json <strong>debe</strong> contener la propiedad <i>@path</i>
 * para permitir el mapeo con el subtipo correspondiente.
 *
 * @author Ismael El Kadaoui Calvo
 * @see RequestMappingConfiguration.DefaultRequestMapper
 * @see RequestMapping
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "@path")
public interface Request extends Serializable {

}
