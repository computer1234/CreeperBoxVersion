package helper.creeperbox.sdk.network;

import java.util.ArrayList;

public class UnknownItemDef extends ItemDef{
    public UnknownItemDef(int runtimeID) {
        super(runtimeID, "minecraft:unknown",new ArrayList<>(),0,"none");
    }
}
