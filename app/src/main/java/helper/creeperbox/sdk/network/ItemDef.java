package helper.creeperbox.sdk.network;

import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;

import java.util.List;

public class ItemDef implements ItemDefinition {
    public final int runtimeID;
    public final String name;
    public final List<String> tags;
    public final int maxSize;
    public final String breakTool;
    public ItemDef(int runtimeID, String name, List<String> tags, int maxSize, String breakTool){
        this.runtimeID = runtimeID;
        this.name = name;
        this.tags = tags;
        this.maxSize = maxSize;
        this.breakTool = breakTool;
    }
    @Override
    public boolean isComponentBased() {
        return false;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String getIdentifier() {
        return name;
    }

    @Override
    public int getRuntimeId() {
        return runtimeID;
    }

}
