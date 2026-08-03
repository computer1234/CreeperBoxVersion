package helper.creeperbox.sdk.render;

import helper.creeperbox.sdk.InstanceGenerator;
import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.item.ItemStack;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

public class UIRenderContext extends PointerHolder {

    public UIRenderContext(long pointer) {
        super(pointer);
    }

    public void renderItem(ItemStack item, float x, float y, float scale,float opacity,float idk,boolean enchantment){
        a(item,x,y,scale,opacity,idk,enchantment);
    }

    public native void a(ItemStack item, float x, float y, float scale,float opacity,float idk,boolean enchantment);

    public int renderItem(ItemData data, EntityLocalPlayer player, float x, float y, float scale, float opacity, float idk, boolean enchantment){
        return b(InstanceGenerator.decodeItem(data),player,x,y,scale,opacity,idk,enchantment);
    }

    public native int b(byte[] b, EntityLocalPlayer player, float x, float y, float scale, float opacity, float idk, boolean enchantment);

}

