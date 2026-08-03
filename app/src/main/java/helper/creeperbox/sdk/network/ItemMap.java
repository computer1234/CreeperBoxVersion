package helper.creeperbox.sdk.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.cloudburstmc.protocol.bedrock.data.definitions.ItemDefinition;
import org.cloudburstmc.protocol.common.DefinitionRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class ItemMap implements DefinitionRegistry<ItemDefinition> {
    public HashMap<Integer, ItemDef> map = new HashMap<>();
    public ItemMap(InputStreamReader reader) {
        try (BufferedReader bufferedReader = new BufferedReader(
                reader)) {
            JsonParser jsonParser = new JsonParser();
            JsonArray jsonArray = jsonParser.parse(bufferedReader).getAsJsonArray();

            for (JsonElement element : jsonArray) {
                JsonObject obj = element.getAsJsonObject();
                int id = obj.get("id").getAsInt();
                String name = obj.get("name").getAsString();
                map.put(id, new ItemDef(id, name,ItemTags.tags(name),ItemDataMap.getMaxStackSize(name),ItemDataMap.getBreakTool(name)));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public ItemDefinition getDefinition(int i) {
        return map.getOrDefault(i,new UnknownItemDef(i));
    }

    @Override
    public boolean isRegistered(ItemDefinition itemDefinition) {
        return true;
    }
}
