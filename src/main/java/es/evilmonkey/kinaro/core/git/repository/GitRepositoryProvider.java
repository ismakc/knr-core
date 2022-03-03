package es.evilmonkey.kinaro.core.git.repository;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.experimental.Accessors;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;

import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

@Slf4j
@Getter
public class GitRepositoryProvider {

	private final Path directory;

	private final CredentialsProvider credentials;

	private final PersonIdent personIdent;

	private final String name;

	private final String uri;

	private final TransportConfigCallback transportConfigCallback;

	private final @Getter(AccessLevel.PRIVATE) CompletableFuture<Void> initialized;

	public static Builder build() {
		return new Builder();
	}

	GitRepositoryProvider(// @formatter:off
			String name,
			String uri,
			String username,
			String password,
			Path directory,
			String authorName,
			String authorEmail,
			String sshKeyPath // @formatter:on
	) {

		this.name = name;
		this.uri = uri;
		this.directory = directory;
		this.credentials = new UsernamePasswordCredentialsProvider(username, password);

		StringBuilder nameBuilder = new StringBuilder();
		PersonIdent.appendSanitized(nameBuilder, authorName);
		StringBuilder emailBuilder = new StringBuilder();
		PersonIdent.appendSanitized(emailBuilder, authorEmail);
		this.personIdent = new PersonIdent(nameBuilder.toString(), emailBuilder.toString());

		File key = null;
		if (StringUtils.hasText(sshKeyPath)) {
			try {
				key = ResourceUtils.getFile(sshKeyPath);
			}
			catch (FileNotFoundException ex) {
				log.warn(String.format(
						"There was an error reading ssh key path [%s]. Process will continue without ssh transport",
						sshKeyPath));
			}
		}
		this.transportConfigCallback = (key != null) ? new SshTransportConfigCallback(key.getAbsolutePath()) : null;
		this.initialized = CompletableFuture.runAsync(this::cloneRepository);
	}

	@SneakyThrows
	public <T> T withGit(Function<Git, T> sourceSupplier) {
		waitUntilGitStoreIsInitialized();
		try (Git git = Git.open(this.directory.toFile())) {
			return sourceSupplier.apply(git);
		}
	}

	@SneakyThrows
	private void waitUntilGitStoreIsInitialized() {
		this.initialized.get();
	}

	@SneakyThrows
	private void cloneRepository() {
		// Open existing repository if any
		try (Git git = Git.open(this.directory.toFile())) {
			git.reset().setMode(ResetCommand.ResetType.HARD).call();
			git.pull().setTransportConfigCallback(this.transportConfigCallback).setCredentialsProvider(this.credentials)
					.setStrategy(MergeStrategy.THEIRS).call();
			log.info("Git repository {} was successfully cloned in {}", this.uri, this.directory);
		}
		catch (IOException ex) {
			cloneRepositoryFromScratch();
		}
	}

	@SneakyThrows
	private void cloneRepositoryFromScratch() {
		cleanGitDirectory();
		try (Git ignored = Git.cloneRepository().setURI(this.uri)
				.setTransportConfigCallback(this.transportConfigCallback).setCredentialsProvider(this.credentials)
				.setDirectory(this.directory.toFile()).call()) {
			log.info("Git repository {} was successfully cloned in {}", this.uri, this.directory);
		}
	}

	@SneakyThrows
	private void cleanGitDirectory() {
		Files.createDirectories(this.directory);
		try (Stream<Path> walk = Files.walk(this.directory)) {
			// noinspection ResultOfMethodCallIgnored
			walk.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
		}
		Files.createDirectories(this.directory);
	}

	@Value
	@AllArgsConstructor
	@RequiredArgsConstructor
	private static class SshTransportConfigCallback implements TransportConfigCallback {

		String privateKey;

		@NonFinal
		byte[] passphrase;

		@Override
		public void configure(Transport transport) {
			SshTransport sshTransport = (SshTransport) transport;
			sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
				@Override
				protected void configure(OpenSshConfig.Host hc, Session session) {
					session.setConfig("StrictHostKeyChecking", "no");
				}

				@Override
				protected JSch createDefaultJSch(FS fs) throws JSchException {
					JSch jSch = super.createDefaultJSch(fs);
					jSch.addIdentity(SshTransportConfigCallback.this.privateKey,
							SshTransportConfigCallback.this.passphrase);
					return jSch;
				}
			});
		}

	}

	@NoArgsConstructor(access = AccessLevel.PRIVATE)
	@Accessors(fluent = true)
	@Setter
	static class Builder {

		String name;

		String uri;

		String username;

		String password;

		Path directory;

		String authorName;

		String authorEmail;

		String sshKeyPath;

		GitRepositoryProvider build() {
			return new GitRepositoryProvider(this.name, this.uri, this.username, this.password, this.directory,
					this.authorName, this.authorEmail, this.sshKeyPath);
		}

	}

}
