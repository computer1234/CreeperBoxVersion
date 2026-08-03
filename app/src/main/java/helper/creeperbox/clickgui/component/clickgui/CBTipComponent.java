package helper.creeperbox.clickgui.component.clickgui;

import helper.creeperbox.clickgui.font.CustomFontRenderer;
import helper.creeperbox.clickgui.font.Fonts;

public class CBTipComponent extends CBSettingComponent {

    private static CustomFontRenderer font = Fonts.getFontRenderer("pingfang-thin.ttf",20);
    public CBTipComponent() {
        super(20f);
    }

    @Override
    public void render() {
        font.drawCenteredString("PS:网络服务器建议使用默认参数游玩,长摁功能可以开启配置编辑哦",x+373f,this.y+font.getMiddle(height),0xffa9a9a9);
    }
}
