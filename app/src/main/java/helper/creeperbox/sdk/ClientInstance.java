package helper.creeperbox.sdk;

public class ClientInstance extends PointerHolder{
    public ClientInstance(long pointer) {
        super(pointer);
    }

    public static float getGuiScale(){
        return a();
    }


    public static native float a();
}
