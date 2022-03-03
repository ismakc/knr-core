package es.evilmonkey.kinaro.core.git.repository;

import java.util.List;
import java.util.Map;

import es.evilmonkey.kinaro.core.git.repository.fixtures.GitRepositoryDetailsFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;

import static org.assertj.core.api.Assertions.assertThat;

class GitRepositoryGroupTest {

	GitRepositoryGroup gitRepositoryGroup;

	GitRepositoryDetails gitRepositoryDetails = GitRepositoryDetailsFixture.fixture1();

	@BeforeEach
	void setUp() {
		this.gitRepositoryGroup = new GitRepositoryGroup(
				Map.of(this.gitRepositoryDetails.getName(), this.gitRepositoryDetails));
	}

	@Test
	void test_gitRepositoryDetailsAreReturnedByName() {
		assertThat(this.gitRepositoryGroup.get(this.gitRepositoryDetails.getName()))
				.isEqualTo(this.gitRepositoryDetails);
	}

	@Test
	void tes_onlyGitRepositoryGroupIsSupported() {
		assertThat(this.gitRepositoryGroup.supports(GitRepositoryGroup.class)).isTrue();
		assertThat(this.gitRepositoryGroup.supports(String.class)).isFalse();
	}

	@Test
	void test_gitRepositoryGroupIsNotValidIfKeyMapNotMatchGitRepositoryDetailsName() {
		GitRepositoryDetails fixture2 = GitRepositoryDetailsFixture.fixture2();
		String nameOfFixture1 = this.gitRepositoryDetails.getName();
		String nameOfFixture2 = fixture2.getName();
		GitRepositoryGroup gitRepositoryGroupFixture2 = new GitRepositoryGroup(Map.of(nameOfFixture1, fixture2));
		Errors errors = new BeanPropertyBindingResult(gitRepositoryGroupFixture2, "gitRepositoryGroup");

		gitRepositoryGroupFixture2.validate(gitRepositoryGroupFixture2, errors);

		List<ObjectError> rejectedValues = errors.getAllErrors();
		assertThat(rejectedValues).isNotEmpty();
		rejectedValues.forEach((objectError) -> assertThat(objectError.getDefaultMessage()).isEqualTo(
				"Repository key [" + nameOfFixture1 + "] should be equal to repository name [" + nameOfFixture2 + "]"));
	}

	@Test
	void test_gitRepositoryGroupIsValid() {
		Errors errors = new BeanPropertyBindingResult(this.gitRepositoryGroup, "gitRepositoryGroup");
		this.gitRepositoryGroup.validate(this.gitRepositoryGroup, errors);
		List<ObjectError> rejectedValues = errors.getAllErrors();
		assertThat(rejectedValues).isEmpty();
	}

}
