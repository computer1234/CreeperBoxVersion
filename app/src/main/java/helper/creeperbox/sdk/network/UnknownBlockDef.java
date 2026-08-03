package helper.creeperbox.sdk.network;

import org.cloudburstmc.nbt.NbtMap;

public class UnknownBlockDef extends BlockDef{
    public UnknownBlockDef(int runtimeID) {
        super("minecraft:unknown", runtimeID, NbtMap.EMPTY);
    }
}
