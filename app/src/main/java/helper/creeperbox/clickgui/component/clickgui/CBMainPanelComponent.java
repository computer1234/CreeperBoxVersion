package helper.creeperbox.clickgui.component.clickgui;

import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import helper.creeperbox.clickgui.ClickGUIRenderer;
import helper.creeperbox.clickgui.component.BasicComponent;
import helper.creeperbox.clickgui.component.FixedComponent;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.StencilUtil;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class CBMainPanelComponent extends FixedComponent {
    private List<CBCategoryChoiceComponent> selectList;
    private List<CBCategoryComponent> categoryList;

    private int lastIndex = 0;
    public CBMainPanelComponent() {
        super(0,0,1260,750);
        selected = 0;
        selectList = new ArrayList<>();
        categoryList = new ArrayList<>();
        selectList.add(new CBCategoryChoiceComponent(0,"icon/icon6.png","主页",this));
        selectList.add(new CBCategoryChoiceComponent(1,"icon/icon4.png","建筑破坏",this));
        selectList.add(new CBCategoryChoiceComponent(2,"icon/icon3.png","战斗",this));
        selectList.add(new CBCategoryChoiceComponent(3,"icon/icon1.png","移动传送",this));
        selectList.add(new CBCategoryChoiceComponent(4,"icon/icon5.png","生存脚本",this));
        selectList.add(new CBCategoryChoiceComponent(5,"icon/icon2.png","渲染",this));

        categoryList.add(new CBCategoryComponent(0,"主页"));
        categoryList.add(new CBCategoryComponent(Category.Build,"建筑破坏",1));
        categoryList.add(new CBCategoryComponent(Category.Combat,"战斗",2));
        categoryList.add(new CBCategoryComponent(Category.Movement,"移动传送",3));
        categoryList.add(new CBCategoryComponent(Category.Survival,"生存脚本",4));
        categoryList.add(new CBCategoryComponent(Category.Render,"渲染",5));

        for(BasicComponent c : selectList) addChildComponent(c);

        for(BasicComponent c : categoryList) addChildComponent(c);

    }
    private static CustomFontRenderer font50 = Fonts.getFontRenderer("pingfang.ttf",50);
    private static CustomFontRenderer font30 = Fonts.getFontRenderer("pingfang.ttf",30);
    private static CustomFontRenderer font40 = Fonts.getFontRenderer("pingfang.ttf",40);
    private int selected;
    private Animation moveAnim = new Animation(Easing.EASE_OUT_EXPO,500);

    @Override
    public void measure() {
        this.x = (ClickGUIRenderer.width-width)/2f;
        this.y = (ClickGUIRenderer.height-height)/2f;

        float choiceX = x+15f;
        float choiceY = y+206f;

        float panelY = 0;

        for(CBCategoryChoiceComponent c : selectList){
            if(c.isSelected()) panelY = choiceY;
            c.setPos(choiceX,choiceY);
            choiceY+=c.getHeight();
        }

        moveAnim.run(panelY);

        for(CBCategoryComponent c : categoryList){
            c.setPos(x+482f,y);
            c.setVisible(false);
        }

        categoryList.get(selected).setVisible(true);
        categoryList.get(selected).measure();
    }



    @Override
    public void render() {

        //Main Panel
        Render2DUtil.drawRound(x,y,width,height,80f,0xc0252525);

        //Left
        font50.drawString("Galaxy",x+35,y+40, Color.WHITE);
        font30.drawString("version 2.0.1",x+40,y+95, Color.WHITE);

        Render2DUtil.rectangle(x+121f,y+140f,359f,2f,0xffa9a9a9);
        font30.drawString("公告与账号",x+40,y+160, Color.WHITE);

        Render2DUtil.drawRound(x+15f,y+206f,448f,534f,43f,43f,43f,60f,0xc0121212);

        StencilUtil.initStencil();
        StencilUtil.writeStencilBuffer();
        Render2DUtil.drawRound(x+15f,y+206f,448f,534f,43f,43f,43f,60f,Color.WHITE);
        StencilUtil.readStencilBuffer();
        float destY = moveAnim.getValue();
        Render2DUtil.drawRound(x+15f,destY,448,89,20f,0xFF0062FF);
        StencilUtil.unUseStencil();

        for(BasicComponent c : selectList) c.render();

        //Margin
        Render2DUtil.rectangle(x+480f,y,2f,height,0xffa9a9a9);
        font40.drawString("◥",x+430f,y+10,Color.WHITE);

        for(CBCategoryComponent c : categoryList){
            if(c.isVisible()) c.render();
        }

        if(selected != lastIndex){
            lastIndex = selected;
            categoryList.get(selected).toggle();
        }

    }

    public void setSelected(int index) {
        if(index>=selectList.size()){
            return;
        }
        this.selected = index;
        for(CBCategoryChoiceComponent component : selectList){
            component.setSelected(false);
        }
        selectList.get(index).setSelected(true);
    }

}
