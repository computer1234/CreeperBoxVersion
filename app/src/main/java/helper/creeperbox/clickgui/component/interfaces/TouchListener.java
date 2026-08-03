package helper.creeperbox.clickgui.component.interfaces;

public interface TouchListener {
    boolean onPress(float x,float y);

    void onRelese(float x,float y);

    boolean onMove(float x,float y);
}
