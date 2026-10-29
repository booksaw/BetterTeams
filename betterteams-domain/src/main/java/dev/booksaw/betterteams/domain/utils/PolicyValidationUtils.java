package dev.booksaw.betterteams.domain.utils;

import dev.booksaw.betterteams.domain.exception.InvalidPolicyConfigurationException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.function.Function;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PolicyValidationUtils {

	public static void checkArgument(
			boolean condition,
			Function<String, ? extends InvalidPolicyConfigurationException> exceptionFactory,
			String message, Object... replacements
	) {

		if (!condition) {
			throw exceptionFactory.apply(message.formatted(replacements));
		}

	}

	public static void checkArgument(boolean condition, String message, Object... replacements) {
		checkArgument(condition, InvalidPolicyConfigurationException::new, message, replacements);
	}

	public static void checkNotNull(Object value, String key) {
		checkArgument(value != null, "'%s' cannot be null", key);
	}

}
