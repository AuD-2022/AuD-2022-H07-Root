package h07;

import h07.transformer.MethodInterceptor;
import org.junit.jupiter.api.Test;
import org.sourcegrade.jagr.api.testing.extension.JagrExecutionCondition;

import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;

public class IllegalMethodsCheck {

    public static final boolean IS_RUN_WITH_JAGR = !new JagrExecutionCondition().evaluateExecutionCondition(null).isDisabled();

    @Test
    public void checkJagr() {
        fail("""
            Results may not be accurate as the submission could use illegal methods but the tests are not run with Jagr.
            You can disable this message by annotating the method with @Disabled""");
    }

    public static void checkMethods(String... acceptedSignatures) {
        if (IS_RUN_WITH_JAGR) {
            for (MethodInterceptor.Invocation invocation : MethodInterceptor.getInvocations()) {
                Stream<String> defaultAcceptedSignatures = Stream.of(
                    "^h07/.+",
                    "^org/sourcegrade/jagr/core/executor/TimeoutHandler checkTimeout\\(\\)V$",
                    ".+? (clone|equals|hashCode|toString)\\(.*\\).+",
                    "^java/io/PrintStream .+",
                    ".+? valueOf\\(.+\\).+",
                    ".+? toString\\(.*\\)Ljava/lang/String;$",
                    "^java/lang/String format(ting)?\\(.*\\)Ljava/lang/String;$",
                    "^java/lang/.+? (boolean|byte|short|char|int|long|float|double)Value\\(\\).+"
                );

                if (Stream.concat(defaultAcceptedSignatures, Arrays.stream(acceptedSignatures))
                    .noneMatch(acceptedSignature -> invocation.signature().matches(acceptedSignature))) {
                    fail("Illegal invocation used: " + invocation.signature());
                }
            }
        }
    }
}
