package es.evilmonkey.kinaro.core.util;

import java.util.Optional;

public final class KnrOptionals {

	private KnrOptionals() {
	}

	public static <T> Optional<T> or(Optional<T> optional, Optional<T> fallback) {
		return optional.isPresent() ? optional : fallback;
	}

}
