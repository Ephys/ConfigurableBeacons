package be.ephys.configurablebeacons.asm;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.List;

public class ASMTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformedName.equals("net.minecraft.tileentity.TileEntityBeacon")) {
            return patchBeacon(name, basicClass, !name.equals(transformedName));
        }

        return basicClass;
    }

    public byte[] patchBeacon(String name, byte[] bytes, boolean obfuscated) {
        String addEffectsToPlayers;
        if (obfuscated) {
            addEffectsToPlayers = "func_178003_a"; // TODO
        } else {
            addEffectsToPlayers = "updateBeacon";
        }

        ClassNode classNode = new ClassNode();
        ClassReader classReader = new ClassReader(bytes);
        classReader.accept(classNode, 0);

        List<MethodNode> methods = classNode.methods;

        for (MethodNode m : methods) {
            if (m.name.equals(addEffectsToPlayers)) {
                patchUpdateBeacon(m, name);
                break;
            }
        }

        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);

        return writer.toByteArray();
    }

    public void patchUpdateBeacon(MethodNode methodNode, String beaconClassName) {
        InsnList code = methodNode.instructions;

        // Better ways to do it: Directly update the method itself (but super hard)
        // Or, make the method itself redirect to our method (tried but failed, should try again if issues occur)
        MethodInsnNode method = new MethodInsnNode(
                Opcodes.INVOKESTATIC,
                "be/ephys/configurablebeacons/BeaconHook",
                "addEffectsToPlayers",
                "(Lnet/minecraft/tileentity/TileEntityBeacon;)V",
                false
        );

        for (int i = 0; i < code.size(); i++) {
            AbstractInsnNode node = code.get(i);

            if (node.getOpcode() != Opcodes.INVOKESPECIAL) {
                continue;
            }

            if (!(node instanceof MethodInsnNode)) {
                continue;
            }

            MethodInsnNode callNode = (MethodInsnNode) node;

            // replace
            // INVOKESPECIAL net/minecraft/tileentity/TileEntityBeacon.addEffectsToPlayers ()V
            // with our hook
            if (callNode.name.equals("addEffectsToPlayers")) {
                code.set(callNode, method);
            }
        }
    }
}
