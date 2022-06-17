package h07.transformer;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

final class BytecodeUtils {

    static final Type OBJECT_TYPE = Type.getObjectType("Ljava/lang/Object;");
    private static final String ASSIGNMENT_ID = "h07";

    private BytecodeUtils() {}

    static void store(MethodVisitor visitor, Type type, int i) {
        if (type == Type.BOOLEAN_TYPE
            || type == Type.BYTE_TYPE
            || type == Type.CHAR_TYPE
            || type == Type.SHORT_TYPE
            || type == Type.INT_TYPE
        ) {
            visitor.visitVarInsn(Opcodes.ISTORE, i);
        } else if (type == Type.LONG_TYPE) {
            visitor.visitVarInsn(Opcodes.LSTORE, i);
        } else if (type == Type.FLOAT_TYPE) {
            visitor.visitVarInsn(Opcodes.FSTORE, i);
        } else if (type == Type.DOUBLE_TYPE) {
            visitor.visitVarInsn(Opcodes.DSTORE, i);
        } else {
            visitor.visitVarInsn(Opcodes.ASTORE, i);
        }
    }

    static void store(MethodVisitor visitor, Type[] types, int start) {
        for (int i = 0; i < types.length; i++) {
            store(visitor, types[types.length - i - 1], i + start);
        }
    }

    static void load(MethodVisitor visitor, Type type, int i) {
        if (type == Type.BOOLEAN_TYPE
            || type == Type.BYTE_TYPE
            || type == Type.CHAR_TYPE
            || type == Type.SHORT_TYPE
            || type == Type.INT_TYPE
        ) {
            visitor.visitVarInsn(Opcodes.ILOAD, i);
        } else if (type == Type.LONG_TYPE) {
            visitor.visitVarInsn(Opcodes.LLOAD, i);
        } else if (type == Type.FLOAT_TYPE) {
            visitor.visitVarInsn(Opcodes.FLOAD, i);
        } else if (type == Type.DOUBLE_TYPE) {
            visitor.visitVarInsn(Opcodes.DLOAD, i);
        } else {
            visitor.visitVarInsn(Opcodes.ALOAD, i);
        }
    }

    static void load(MethodVisitor visitor, Type[] types, int start) {
        for (int i = 0; i < types.length; i++) {
            load(visitor, types[i], i + start);
        }
    }

    static void injectMethodInterceptorCall(MethodVisitor visitor, String signature, boolean hasContext, int localsIndex,
                                            Type... types) {
        // store original parameters and objectref
//        store(visitor, types, localsIndex); // store original parameters
//        if (hasContext) {
//            store(visitor, OBJECT_TYPE, localsIndex + types.length); // store original objectref
//        }

        // create new objectref for a MethodInterceptor$Invocation object
        visitor.visitTypeInsn(Opcodes.NEW, "%s/transformer/MethodInterceptor$Invocation".formatted(ASSIGNMENT_ID));
        visitor.visitInsn(Opcodes.DUP);

        // load parameters for MethodInterceptor$Invocation
        visitor.visitLdcInsn(signature); // push signature onto stack
//        if (hasContext) {
//            load(visitor, OBJECT_TYPE, localsIndex + types.length); // load original objectref
//        } else {
//            visitor.visitInsn(Opcodes.ACONST_NULL);
//        }
        visitor.visitInsn(Opcodes.ACONST_NULL);
        visitor.visitInsn(Opcodes.ICONST_0);
//        visitor.visitLdcInsn(types.length); // push parameter array length onto stack
        visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object"); // create new arrayref
//        for (int i = 0; i < types.length; i++) {
//            visitor.visitInsn(Opcodes.DUP); // duplicate arrayref
//            visitor.visitLdcInsn(i); // push array index onto stack
//            load(visitor, types[i], localsIndex + types.length - i - 1); // push value onto stack
//            visitor.visitInsn(Opcodes.AASTORE); // store value in array
//        }

        // initialize MethodInterceptor$Invocation object
        visitor.visitMethodInsn(Opcodes.INVOKESPECIAL,
            "%s/transformer/MethodInterceptor$Invocation".formatted(ASSIGNMENT_ID),
            "<init>",
            "(Ljava/lang/String;Ljava/lang/Object;[Ljava/lang/Object;)V",
            false);

        // add invocation to list of invocations
        visitor.visitMethodInsn(Opcodes.INVOKESTATIC,
            "%s/transformer/MethodInterceptor".formatted(ASSIGNMENT_ID),
            "addInvocation",
            "(L%s/transformer/MethodInterceptor$Invocation;)V".formatted(ASSIGNMENT_ID),
            false);

        // push original parameters and objectref back onto the stack
//        if (hasContext) {
//            load(visitor, OBJECT_TYPE, localsIndex + types.length);
//        }
//        load(visitor, types, localsIndex);
    }
}
