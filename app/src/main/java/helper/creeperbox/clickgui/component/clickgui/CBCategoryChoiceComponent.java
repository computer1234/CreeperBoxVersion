package helper.creeperbox.clickgui.component.clickgui;

import android.graphics.Color;

import helper.creeperbox.clickgui.component.FixedComponent;
import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.utils.render.Render2DUtil;

public class CBCategoryChoiceComponent extends FixedComponent {
    private boolean selected;
    private int index;
    private String icon;
    private String name;
    private Category category;
    private CBMainPanelComponent parent;
    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang.ttf",30);
    private int type = 0;     //0 category 1 main

    public CBCategoryChoiceComponent(int index, String icon, String name,CBMainPanelComponent parent) {
        super(0, 0, 448, 89);
        this.index = index;
        this.icon = icon;
        this.name = name;
        this.parent = parent;
        this.type = 1;
        this.selected = (index == 0);
    }

    public boolean isSelected() {
        return selected;
    }

    public CBCategoryChoiceComponent(int index, String icon, String name, Category category, CBMainPanelComponent parent) {
        super(0, 0, 448, 89);
        this.index = index;
        this.icon = icon;
        this.name = name;
        this.category = category;
        this.parent = parent;
        this.selected = (index == 0);
    }

    @Override
    public void onClick(float x, float y, long time) {
        parent.setSelected(index);
    }


    @Override
    public void render() {
        Render2DUtil.roundImage(icon,x+35,y+19.5f,50f,50f,10f);
        font.drawString(name,x+106,y+font.getMiddle(height),Color.WHITE);
        if(index != 5) Render2DUtil.rectangle(x+106,y+height-2f,342,2f,0xffa9a9a9);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

}
