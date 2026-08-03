package helper.creeperbox.clickgui.component;

public class FixedComponent extends BasicComponent{

    public FixedComponent(float x, float y,float width,float height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }


    public float getHeight() {
        return height;
    }

    public float getWidth() {
        return width;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public void setWidth(float width) {
        this.width = width;
    }

}
