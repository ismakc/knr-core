package es.evilmonkey.kinaro.core.request;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
@EnableConfigurationProperties({ RequestMappingConfiguration.KnrCommandProperties.class })
public class RequestMappingConfiguration {

	@Bean
	public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer(KnrCommandProperties knrCommandProperties) {
		return new DefaultRequestMapper(knrCommandProperties);
	}

	@ConfigurationProperties(prefix = "kinaro.command")
	public record KnrCommandProperties(String basePackage) {
	}

	/**
	 * Mapeador por defecto de comandos en formato json con su subtipo {@link Request}
	 * correspondiente.
	 *
	 * <p>
	 * Este mapeador compara la propiedad <i>@path</i> que <strong>debe</strong> contener
	 * el comando en formato json con el valor de la anotación {@link RequestMapping} del
	 * subtipo {@link Request}
	 *
	 * @author Ismael El Kadaoui Calvo
	 * @see RequestMapping
	 * @see Request
	 */
	@Slf4j
	@RequiredArgsConstructor
	public static class DefaultRequestMapper implements Jackson2ObjectMapperBuilderCustomizer {

		private final KnrCommandProperties knrCommandProperties;

		@Override
		public void customize(Jackson2ObjectMapperBuilder builder) {
			builder.postConfigurer(((objectMapper) -> {
				var provider = new ClassPathScanningCandidateComponentProvider(false);
				provider.addIncludeFilter(new AnnotationTypeFilter(RequestMapping.class));
				List<NamedType> namedTypes = new LinkedList<>();
				for (BeanDefinition beanDefinition : provider
						.findCandidateComponents(this.knrCommandProperties.basePackage())) {
					try {
						Class<?> annotatedClass = Class.forName(beanDefinition.getBeanClassName());
						String pathMapping = annotatedClass.getAnnotation(RequestMapping.class).value();
						namedTypes.add(new NamedType(annotatedClass, pathMapping));
						log.debug("Added command mapping for class {} with path {}", annotatedClass.getName(),
								pathMapping);
					}
					catch (ClassNotFoundException ex) {
						log.warn("Class {} not found, it will be ignore", beanDefinition.getBeanClassName());
					}
				}
				objectMapper.registerSubtypes(namedTypes.toArray(NamedType[]::new));
			}));
		}

	}

}
