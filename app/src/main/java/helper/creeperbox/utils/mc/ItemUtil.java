package helper.creeperbox.utils.mc;

import helper.creeperbox.sdk.network.ItemDef;
import helper.creeperbox.sdk.network.ItemTags;

import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.List;

public class ItemUtil {

    private static final short PROTECTION_ALL = 0;
    private static final short DAMAGE_ALL = 9;

    private static final short EFFICIENCY = 15;

    private static final short BOW_POWER = 19;

    public static boolean isBlock(ItemData itemData){
        return itemData.getBlockDefinition()!=null && itemData.getBlockDefinition().getRuntimeId()!=0;
    }

    public static float getLevel(ItemData item, ItemDef def){
        float basic = 0f;
        if (def.tags.contains(ItemTags.TAG_LEATHER_TIER)) {
            basic = 10f;
        } else if (def.tags.contains(ItemTags.TAG_WOODEN_TIER)) {
            basic = 15f;
        } else if (def.tags.contains(ItemTags.TAG_GOLDEN_TIER)) {
            basic = 20f;
        } else if (def.tags.contains(ItemTags.TAG_STONE_TIER)) {
            basic = 25f;
        } else if (def.tags.contains(ItemTags.TAG_CHAINMAIL_TIER)) {
            basic = 25f;
        } else if (def.tags.contains(ItemTags.TAG_IRON_TIER)) {
            basic = 30f;
        } else if (def.tags.contains(ItemTags.TAG_DIAMOND_TIER)) {
            basic = 35f;
        } else if (def.tags.contains(ItemTags.TAG_NETHERITE_TIER)) {
            basic = 40f;
        } else if (def.name.equals("minecraft:crossbow")) {
            basic = 5f;
        }
        float enchant = getEnchant(item,DAMAGE_ALL) * 1.25f + getEnchant(item,PROTECTION_ALL) * 1f + getEnchant(item,EFFICIENCY) * 0.3f + getEnchant(item,BOW_POWER) * 1f;
        return basic + enchant;
    }



    public static short getEnchant(ItemData item,short id){
        if (item.getTag() != null) {
            List<NbtMap> enchantments = item.getTag().getList("ench", NbtType.COMPOUND);

            for (int i = 0; i < enchantments.size(); i++) {
                NbtMap en = enchantments.get(i);

                if (en.getShort("id") == id) {
                    return en.getShort("lvl");
                }
            }
        }
        return 0;
    }



    public static boolean isUseFul(ItemData item, ItemDef def){
        if(item == ItemData.AIR) return false;
        if(def.getIdentifier().equalsIgnoreCase("minecraft:tnt")) return false;
        if(def.getIdentifier().equalsIgnoreCase("minecraft:web")) return false;
        if (isBlock(item)) return true;
        List<String> itemTags = def.tags;
        String identifier = def.getIdentifier();
        return itemTags.contains(ItemTags.TAG_IS_HELMET)
                || itemTags.contains(ItemTags.TAG_IS_CHESTPLATE)
                || itemTags.contains(ItemTags.TAG_IS_LEGGINGS)
                || itemTags.contains(ItemTags.TAG_IS_BOOTS)
                || itemTags.contains(ItemTags.TAG_IS_SWORD)
                || itemTags.contains(ItemTags.TAG_IS_PICKAXE)
                || itemTags.contains(ItemTags.TAG_IS_AXE)
                || itemTags.contains(ItemTags.TAG_IS_HOE)
                || itemTags.contains(ItemTags.TAG_IS_SHOVEL)
                || itemTags.contains(ItemTags.TAG_IS_BOW)
                || itemTags.contains(ItemTags.TAG_IS_FOOD)
                || identifier.equalsIgnoreCase("minecraft:shield")
                || identifier.equalsIgnoreCase("minecraft:trident")
                || identifier.equalsIgnoreCase("minecraft:flint_and_steel")
                || identifier.equalsIgnoreCase("minecraft:totem_of_undying")
                || identifier.equalsIgnoreCase("minecraft:end_crystal")
                || identifier.equalsIgnoreCase("minecraft:ender_pearl")
                || identifier.equalsIgnoreCase("minecraft:arrow");
    }


}
