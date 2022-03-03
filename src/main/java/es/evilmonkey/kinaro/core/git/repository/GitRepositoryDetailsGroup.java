package es.evilmonkey.kinaro.core.git.repository;

import java.util.Map;

import lombok.Value;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;

/**
 * Clase que contiene la configuración de los repositorios git que se utilizan para
 * almacenar los datos que requieren mantener un seguimiento de cambios.
 *
 * @author Ismael El Kadaoui Calvo
 */

@Value
@Validated
@ConfigurationProperties(prefix = "kinaro.git")
@ConstructorBinding
public class GitRepositoryGroup implements Validator {

	private final Map<String, GitRepositoryDetails> repositoryGroups;

	public GitRepositoryDetails get(String repositoryName) {
		return this.repositoryGroups.get(repositoryName);
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return GitRepositoryGroup.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		((GitRepositoryGroup) target).repositoryGroups.forEach((k, v) -> {
			if (!k.equals(v.getName())) {
				String msg = String.format("Repository key [%s] should be equal to repository name [%s]", k,
						v.getName());
				errors.rejectValue("repositoryGroups", "error.data.tracking.git.repository.key.invalid", msg);
			}
		});
	}

}
