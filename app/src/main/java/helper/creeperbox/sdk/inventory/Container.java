package helper.creeperbox.sdk.inventory;

import helper.creeperbox.sdk.PointerHolder;
import helper.creeperbox.sdk.item.ItemStack;

import org.cloudburstmc.protocol.bedrock.data.inventory.ContainerType;

public class Container extends PointerHolder {
    public Container(long pointer) {
        super(pointer);
    }

    public ContainerType getContainerType(){
        try {
            return ContainerType.from(a());
        }catch (Exception e){
            e.printStackTrace();
        }
        return ContainerType.NONE;
    }

    public native int a();

    public int getRuntimeID(){
        return b();
    }
    public native int b();

    public boolean hasCustomName(){
        return c();
    }
    public native boolean c();

    public String getCustomName(){
        return d();
    }
    public native String d();

    public int getSize(){
        return e();
    }
    public native int e();
    public ItemStack getItemStack(int slot){
        return f(slot);
    }
    public native ItemStack f(int slot);

}
