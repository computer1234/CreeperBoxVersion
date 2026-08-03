package helper.creeperbox.clickgui.component.clickgui;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clickgui.component.BasicComponent;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class CBListSettingComponent extends CBSettingComponent{
    private ListValue value;
    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang-thin.ttf",25);

    private ArrayList<CBListSelectComponent> list;

    public CBListSettingComponent(ListValue value) {
        super(110f);
        this.value = value;
        list = new ArrayList<>();
        List<String> list = value.getSubLists();
        for(int i = 0 ; i < list.size() ; i ++){
            this.list.add(new CBListSelectComponent(this,list.get(i),i == value.getIndex(),i));
        }
        for(BasicComponent c : this.list) addChildComponent(c);
    }

    @Override
    public void measure() {
        float posX = this.x + 40f;
        for(BasicComponent c : list){
            c.setPos(posX,this.y+55f);
            c.measure();
            posX += c.getWidth() + 10f;
        }
    }

    @Override
    public void render() {
        font.drawString(value.getName(),x+40f,y+17f,0xffa9a9a9);
        for(BasicComponent c : list) c.render();
    }

    public void setIndex(int index){
        for(int i = 0 ; i< list.size();i++){
            if(i == index) list.get(i).selected = true;
            else list.get(i).selected = false;
        }
    }


    private class CBListSelectComponent extends BasicComponent {

        private CBListSettingComponent parent;
        private boolean selected;
        private String name;

        private Animation toggleAnim = new Animation(Easing.Decelerate,500);
        private int index;
        public CBListSelectComponent(CBListSettingComponent parent,String name,boolean selected,int index) {
            super(0,0);
            this.height = 50f;
            this.parent = parent;
            this.name = name;
            this.selected = selected;
            this.index = index;
        }

        @Override
        public void render() {
            float value = toggleAnim.getValue();
            Render2DUtil.drawRound(x,y,width,height,20f, ColorUtil.interpolateColor(0x1effffff,0x0,value));
            font.drawString(name,x+20f,y+font.getMiddle(height), 0xffa9a9a9);
        }

        @Override
        public void onClick(float x, float y, long time) {
            parent.setIndex(index);
            parent.value.setValue(name);
        }

        @Override
        public void measure() {
            this.width = font.getStringWidth(name) + 40f;
            toggleAnim.run(selected?0f:1f);
        }
    }
}
