package com.booksaw.betterteams.domain.utils;

import com.booksaw.betterteams.domain.exception.InvalidPolicyConfigurationException;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

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

  public static void checkNotNull(Object value, String message, Object... replacements) {
    checkArgument(value != null, message, replacements);
  }

}
