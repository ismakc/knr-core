package es.evilmonkey.kinaro.core.git.repository;

import javax.annotation.PostConstruct;

import es.evilmonkey.kinaro.core.util.KnrStringUtils;
import lombok.AllArgsConstructor;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
@EnableConfigurationProperties({ GitRepositoryGroup.class })
public class GitRepositoryProviderRegistry {

	public static final String BEAN_NAME = "gitRepositoryProviderRegistry";

	private final GitRepositoryGroup gitRepositoryGroup;

	private final ConfigurableListableBeanFactory beanFactory;

	@PostConstruct
	void registerBeans() {
		this.gitRepositoryGroup.repositoryGroups.values().forEach(v -> {
			// @formatter:off
			GitRepositoryProvider gitRepositoryProvider = GitRepositoryProvider.build()
					.authorEmail(v.getAuthorEmail())
					.authorName(v.getAuthorName())
					.directory(v.getDirectory())
					.name(v.getName())
					.password(v.getPassword())
					.sshKeyPath(v.getSshKeyPath())
					.username(v.getUsername())
					.uri(v.getUri())
					.build();
			// @formatter:on
			String beanName = KnrStringUtils.decapitalize(v.getName() + "Provider");
			this.beanFactory.initializeBean(gitRepositoryProvider, beanName);
			this.beanFactory.registerSingleton(beanName, gitRepositoryProvider);
		});
	}

}
