package helper.creeperbox.sdk.item;

import helper.creeperbox.sdk.PointerHolder;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

public class ItemStack extends PointerHolder {
    public ItemStack(long pointer) {
        super(pointer);
    }

    public short getAuxValue(){
        return a();
    }
    public native short a();
    public boolean isValid(){
        return b();
    }
    public native boolean b();
    public int getCount(){
        return c();
    }
    public native int c();
    public int getMaxStackSize(){
        return d();
    }
    public native int d();

    public void setMaxStackSizeForAllItem(int size){
        e(size);
    }
    public native void e(int size);

    public String getNameSpace(){
        return f();
    }
    public native String f();
    public boolean isBlock(){
        return g();
    }
    public native boolean g();
    public int getItemID(){
        return h();
    }
    public native int h();
    public ItemData getItemData(){
        return i();
    }
    public native ItemData i();

    public int getMaxDamage(){
        return j();
    }
    public native int j();


}
