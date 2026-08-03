package helper.creeperbox.clickgui.component;

import helper.creeperbox.clickgui.component.interfaces.TouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;

public class BasicComponent implements IComponent{

    protected float x;
    protected float y;

    protected BooleanSupplier hideIf = new BooleanSupplier() {
        @Override
        public boolean getAsBoolean() {
            return false;
        }
    };


    public BasicComponent hideIf(BooleanSupplier hideIf) {
        this.hideIf = hideIf;
        return this;
    }

    protected float width;
    protected float height;

    protected boolean visible = false;



    private long pressTime;
    protected boolean isPress = false;

    protected boolean isDrag = false;

    protected float dragX,dragY;

    protected List<BasicComponent> child;

    private List<TouchListener> touchListeners;

    public void addTouchListener(TouchListener listener){
        touchListeners.add(listener);
    }


    public boolean canDrag(float x,float y){
        return false;
    }


    public void onClick(float x,float y,long time){

    }


    public BasicComponent(float x, float y) {
        this.x = x;
        this.y = y;
        visible = true;
        isPress = false;
        isDrag = false;
        touchListeners = new ArrayList<>();
        child = new ArrayList<>();

        preInit();

        addTouchListener(new TouchListener() {
            @Override
            public boolean onPress(float x, float y) {
                if(canDrag(x,y)){
                    isDrag = true;
                    dragX = x - BasicComponent.this.x;
                    dragY = y - BasicComponent.this.y;
                    return false;
                }
                return true;
            }

            @Override
            public void onRelese(float x, float y) {
                if(isDrag){
                    isDrag = false;
                }
            }

            @Override
            public boolean onMove(float x, float y) {
                if(isDrag){
                    setPos(x - dragX,y- dragY);
                    return false;
                }
                return true;
            }
        });
    }

    protected void preInit(){

    }


    public void addChildComponent(BasicComponent c){
        child.add(c);
    }

    @Override
    public void measure() {

    }

    public void setPos(float x,float y){
        this.x = x;
        this.y = y;
    }

    @Override
    public final void draw(){
        if(visible && !hideIf.getAsBoolean()){
            measure();
            render();
        }
    }


    public void render(){

    }


    public void setVisible(boolean visible) {
        this.visible = visible;
    }


    public void setSize(float width,float height){
        this.width = width;
        this.height = height;
    }

    public void setHeight(float height) {
        this.height = height;
    }


    public void setWidth(float width) {
        this.width = width;
    }

    public final boolean onPress(float x, float y){
        if(!isInSide(x,y) || !visible || hideIf.getAsBoolean()) return true;

        isPress = true;
        pressTime = System.currentTimeMillis();
        for(BasicComponent c : child){
            if(!c.onPress(x,y)) return false;
        }

        for(TouchListener listener : touchListeners){
            if(!listener.onPress(x,y)) return false;
        }

        return true;
    }


    public final void onRelease(float x,float y,boolean prevent){

        if(!isPress) return;

        isPress = false;

        long time = System.currentTimeMillis() - pressTime;

        for(BasicComponent c : child){
            c.onRelease(x,y,preventChildClick(x,y,time) || prevent);
        }


        for(TouchListener listener : touchListeners){
            listener.onRelese(x,y);
        }

        if(isInSide(x,y) && !prevent){
            onClick(x,y,time);
        }


    }

    protected boolean preventChildClick(float x,float y,long time){
        return false;
    }

    public final boolean onMove(float x,float y){
        if(!isPress) return true;

        for(BasicComponent c : child){
            if(!c.onMove(x,y)) return false;
        }

        for(TouchListener listener : touchListeners){
            if(!listener.onMove(x,y)) return false;
        }
        return true;
    }


    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }


    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getY() {
        return y;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isInSide(float posX,float posY){
        return posX >= x && posX <= x+width && posY >= y && posY <= y+height;
    }
}
