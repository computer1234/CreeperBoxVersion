package helper.creeperbox.sdk.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helper.creeperbox.hook.HopeHookEntrance;
import helper.creeperbox.utils.ResourceUtil;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemTags {
    public static final String TAG_IS_TOOL = "minecraft:is_tool";
    public static final String TAG_IS_PICKAXE = "minecraft:is_pickaxe";
    public static final String TAG_NETHERITE_TIER = "minecraft:netherite_tier";
    public static final String TAG_IS_SHOVEL = "minecraft:is_shovel";
    public static final String TAG_IS_HOE = "minecraft:is_hoe";
    public static final String TAG_DIAMOND_TIER = "minecraft:diamond_tier";
    public static final String TAG_IS_SWORD = "minecraft:is_sword";
    public static final String TAG_IRON_TIER = "minecraft:iron_tier";
    public static final String TAG_IS_AXE = "minecraft:is_axe";
    public static final String TAG_WOODEN_TIER = "minecraft:wooden_tier";
    public static final String TAG_GOLDEN_TIER = "minecraft:golden_tier";
    public static final String TAG_STONE_TIER = "minecraft:stone_tier";
    public static final String TAG_LEATHER_TIER = "minecraft:leather_tier";
    public static final String TAG_IS_ARMOR = "minecraft:is_armor";
    public static final String TAG_IS_HELMET = "minecraft:is_helmet";
    public static final String TAG_IS_CHESTPLATE = "minecraft:is_chestplate";
    public static final String TAG_IS_LEGGINGS = "minecraft:is_leggings";
    public static final String TAG_IS_BOOTS = "minecraft:is_boots";
    public static final String TAG_CHAINMAIL_TIER = "minecraft:chainmail_tier";
    public static final String TAG_IS_FOOD = "minecraft:is_food";
    public static final String TAG_IS_TRIDENT = "minecraft:is_trident";
    public static final String TAG_IS_BOW = "minecraft:is_bow";
    private static final Map<String, List<String>> itemTags = new HashMap<>();

    static {
        try {
            loadItemTags();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    private static void loadItemTags() throws Exception {
        JsonObject json = JsonParser.parseReader(new InputStreamReader(ResourceUtil.decryptStream(HopeHookEntrance.class.getClassLoader().getResourceAsStream("assets/encrypt/a.vmp")), StandardCharsets.UTF_8)).getAsJsonObject();
        json.entrySet().forEach(entry -> {
            String item = entry.getKey();
            JsonArray tags = entry.getValue().getAsJsonArray();
            List<String> tagList = tags.asList().stream()
                    .map(JsonElement::getAsString)
                    .collect(Collectors.toList());
            itemTags.put(item, tagList);
        });
    }

    public static List<String> tags(String item) {
        return itemTags.getOrDefault(item, new ArrayList<>());
    }


}
