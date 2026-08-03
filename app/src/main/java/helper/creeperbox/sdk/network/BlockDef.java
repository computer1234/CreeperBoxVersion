package helper.creeperbox.sdk.network;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;

public class BlockDef implements BlockDefinition {

    public final String name;
    public final int runtimeID;
    public final NbtMap states;

    public BlockDef(String name, int runtimeID, NbtMap states) {
        this.name = name;
        this.runtimeID = runtimeID;
        this.states = states;
    }

    @Override
    public int getRuntimeId() {
        return runtimeID;
    }


    @Override
    public String toString() {
        return "BlockDef{" +
                "name='" + name + '\'' +
                ", runtimeID=" + runtimeID +
                ", states=" + states +
                '}';
    }


}
