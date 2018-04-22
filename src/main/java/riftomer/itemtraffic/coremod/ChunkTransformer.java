package riftomer.itemtraffic.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import net.minecraft.launchwrapper.IClassTransformer;

import java.util.ListIterator;

public class ChunkTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node,0);
        if(transformedName.equals("net.minecraft.world.chunk.Chunk")) {
            boolean result = false;
            System.out.println("Found Chunk class");
            for (MethodNode method : node.methods) {
                if (method.name.equals("<init>")) {
                    System.out.println("Found constructor");
                    InsnList list = method.instructions;
                    ListIterator<AbstractInsnNode> iterator = list.iterator();
                    while (iterator.hasNext()) {
                        AbstractInsnNode insnNode = iterator.next();
                        if (insnNode instanceof MethodInsnNode) {
                            MethodInsnNode callMethod = (MethodInsnNode) insnNode;
                            if (callMethod.getOpcode() == Opcodes.INVOKESTATIC
                                    && callMethod.owner.equals("com/google/common/collect/Maps")
                                    && callMethod.name.equals("newHashMap")
                                    && callMethod.desc.equals("()Ljava/util/HashMap;")
                                    && !callMethod.itf) {
                                System.out.println("Found map initialization");
                                iterator.set(new MethodInsnNode(Opcodes.INVOKESTATIC, "com/google/common/collect/Maps", "newConcurrentMap", "()Ljava/util/concurrent/ConcurrentMap;", false));
                                System.out.println("Patched map initialization");
                                result = true;
                            }
                        }
                    }
                }
            }
            if(!result){
                throw new IllegalStateException("Unable to patch chunk class");
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        node.accept(writer);
        return writer.toByteArray();
    }
}
