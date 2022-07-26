package h07.transformer;

import org.objectweb.asm.*;
import org.sourcegrade.jagr.api.testing.ClassTransformer;

public class BytecodeTransformations implements ClassTransformer {

        @Override
        public String getName() {
            return null;
        }

        @Override
        public void transform(ClassReader reader, ClassWriter writer) {
            reader.accept(new ClassTransformer(writer), ClassReader.EXPAND_FRAMES);
        }

        @Override
        public int getWriterFlags() {
            return ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES;
        }

        private static class ClassTransformer extends ClassVisitor {

            public ClassTransformer(ClassWriter classWriter) {
                super(Opcodes.ASM9, classWriter);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                return new MethodVisitor(Opcodes.ASM9, super.visitMethod(access, name, descriptor, signature, exceptions)) {

                    private int maxVar = 0;

                    @Override
                    public void visitIincInsn(int var, int increment) {
                        maxVar = Math.max(maxVar, var);
                        super.visitIincInsn(var, increment);
                    }

                    @Override
                    public void visitVarInsn(int opcode, int var) {
                        maxVar = Math.max(maxVar, var);
                        super.visitVarInsn(opcode, var);
                    }

                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                        String signature = "%s %s%s".formatted(owner, name, descriptor);
                        if (opcode == Opcodes.INVOKEVIRTUAL) {
                            BytecodeUtils.injectMethodInterceptorCall(this,
                                signature,
                                true,
                                maxVar + 1,
                                Type.getArgumentTypes(descriptor));
                        } else if (opcode == Opcodes.INVOKESTATIC && !signature.equals("h07/transformer/MethodInterceptor "
                            + "addInvocation(Lh07/transformer/MethodInterceptor$Invocation;)V")) {
                            BytecodeUtils.injectMethodInterceptorCall(this,
                                signature,
                                false,
                                maxVar + 1,
                                Type.getArgumentTypes(descriptor));
                        }
                        // TODO: add INVOKESPECIAL and INVOKEDYNAMIC

                        // original method invocation
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                };
            }

            @Override
            public void visitSource(String source, String debug) {
                super.visitSource(source, debug);
            }
        }
    }


