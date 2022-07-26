package h07.transformer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MethodInterceptor {

    private static boolean CALL_PASS_THROUGH = true;
    private static final LinkedList<Invocation> INVOCATIONS = new LinkedList<>();

    public static void reset() {
        INVOCATIONS.clear();
    }

    public static void addInvocation(Invocation invocation) {
        INVOCATIONS.add(invocation);
    }

    public static List<Invocation> getInvocations() {
        return Collections.unmodifiableList(INVOCATIONS);
    }

    public static void setCallPassThrough(boolean callPassThrough) {
        CALL_PASS_THROUGH = callPassThrough;
    }

    public record Invocation(String signature, Object objectRef, Object... params) {}
}
