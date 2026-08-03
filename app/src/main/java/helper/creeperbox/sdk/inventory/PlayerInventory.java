package helper.creeperbox.sdk.inventory;

import helper.creeperbox.sdk.PointerHolder;

public class PlayerInventory extends PointerHolder {
    public PlayerInventory(long pointer) {
        super(pointer);
    }
    public Container getContainer(){
        return a();
    }
    public native Container a();

    public int getSelected(){
        return b();
    }
    public native int b();

    public void setSelected(int slot){
        c(slot);
    }

    public native void c(int slot);

}
