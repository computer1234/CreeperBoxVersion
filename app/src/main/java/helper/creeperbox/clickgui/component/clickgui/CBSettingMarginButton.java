package helper.creeperbox.clickgui.component.clickgui;

import helper.creeperbox.utils.render.Render2DUtil;

public class CBSettingMarginButton extends CBSettingComponent {
    public CBSettingMarginButton() {
        super(20f);
    }
    @Override
    public void render() {
        Render2DUtil.rectangle(x+40f,y+12f,width-80f,2f,0xffa9a9a9);
    }

}
