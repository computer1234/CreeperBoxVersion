package helper.creeperbox.clickgui.component.clickgui;

import android.graphics.Color;


import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.clickgui.component.BasicComponent;
import helper.creeperbox.clickgui.component.FixedComponent;
import helper.creeperbox.clickgui.component.interfaces.TouchListener;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.settings.BasicValue;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.MarginValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.StencilUtil;

public class CBSettingListComponent extends FixedComponent {
    private Module module;
    private List<CBSettingComponent> list = new ArrayList<>();


    private float downY,tempY;
    private static final float slideSlop = 30f;

    private float scrollY;
    private boolean isScroll;
    public CBSettingListComponent(Module module) {

        super(0,0,746,513);
        this.module = module;
        list.add(new CBKeyBindingSettingComponent(module));
        list.add(new CBSettingMarginButton());
        for(BasicValue value : module.getSettings()){
            if(value instanceof NumberValue){
                list.add(new CBNumberSettingComponent((NumberValue) value));
            }
            if(value instanceof ListValue){
                list.add(new CBListSettingComponent((ListValue) value));
            }
            if(value instanceof MarginValue) {
                list.add(new CBSettingMarginButton());
            }
            if(value instanceof BooleanValue) {
                list.add(new CBBoolSettingComponent((BooleanValue) value));
            }
        }
        if(!module.getSettings().isEmpty()){
            list.add(new CBSettingMarginButton());
        }
        list.add(new CBTipComponent());

        for(BasicComponent c : list) addChildComponent(c);

        addTouchListener(new TouchListener() {
            @Override
            public boolean onPress(float x, float y) {
                isPress = true;
                downY = tempY = y - CBSettingListComponent.this.y;
                return false;
            }

            @Override
            public void onRelese(float x, float y) {
                isPress = false;
                isScroll = false;
            }

            @Override
            public boolean onMove(float x, float y) {

                if(!isPress) return true;
                float deltaY = tempY - (y - CBSettingListComponent.this.y);
                tempY = y - CBSettingListComponent.this.y;

                if(Math.abs(downY+CBSettingListComponent.this.y-y)>slideSlop){
                    isScroll = true;
                    scrollY += deltaY;
                    return false;
                }
                return true;
            }
        });
    }

    private float moduleHeight;

    @Override
    public void measure() {

        x = (ClickGUIRenderer.width-width)/2f;
        y = (ClickGUIRenderer.height-height)/2f;

        scrollY = Math.min(Math.max(0,scrollY),Math.max(moduleHeight-height,0));
        moduleHeight = 20f;

        float posY = y + 20f - scrollY;
        for(BasicComponent c : list){
            c.setPos(x,posY);
            c.measure();
            posY += c.getHeight();
            moduleHeight += c.getHeight();
        }

        moduleHeight += 10f;

    }

    @Override
    public void render() {
        Render2DUtil.drawBorder(x,y,width,height,5f,30f,Color.WHITE);
        Render2DUtil.drawRound(x+5f,y+5f,width-10f,height-10f,30f,0xe51e1e1e);
        StencilUtil.initStencil();
        StencilUtil.writeStencilBuffer();
        Render2DUtil.drawRound(x+5f,y+5f,width-10f,height-10f,30f,Color.WHITE);
        StencilUtil.readStencilBuffer();
        for(BasicComponent c : list) c.render();
        StencilUtil.unUseStencil();
    }

}
