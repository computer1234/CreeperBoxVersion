package helper.creeperbox.sdk.network;

import org.cloudburstmc.nbt.NBTInputStream;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.protocol.bedrock.data.definitions.BlockDefinition;
import org.cloudburstmc.protocol.common.DefinitionRegistry;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

public class BlockMap implements DefinitionRegistry<BlockDefinition> {
    private HashMap<Integer,BlockDef> map;

    public BlockMap(InputStream stream){
        map = new HashMap<>();
        try {
            GZIPInputStream inputStream = new GZIPInputStream(stream);
            NBTInputStream nbtStream = new NBTInputStream(new DataInputStream(inputStream));
            while (inputStream.available()>0){
                NbtMap nbtMap = (NbtMap)nbtStream.readTag();
                String name = nbtMap.getString("name");
                int id = nbtMap.getInt("runtimeId");
                NbtMap states = nbtMap.getCompound("states");
                map.put(id,new BlockDef(name,id,states));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public BlockDefinition getDefinition(int i) {
        return map.getOrDefault(i,new UnknownBlockDef(i));
    }

    @Override
    public boolean isRegistered(BlockDefinition blockDefinition) {
        return true;
    }

}
