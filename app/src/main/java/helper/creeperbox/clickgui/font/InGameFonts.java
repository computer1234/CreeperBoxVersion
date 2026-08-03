package helper.creeperbox.clickgui.font;

import java.util.HashMap;

public class InGameFonts {

    private static final HashMap<InGameCustomFont, InGameCustomFontRenderer> CUSTOM_FONT_RENDERERS = new HashMap<>();

    public static InGameCustomFontRenderer getFontRenderer(String fontName, int size){
        return getFontRenderer(new InGameCustomFont(fontName,size));
    }


    public static InGameCustomFontRenderer getFontRenderer(InGameCustomFont font){
        if(CUSTOM_FONT_RENDERERS.containsKey(font)){
            return CUSTOM_FONT_RENDERERS.get(font);
        }
        InGameCustomFontRenderer renderer;
        if(font.getSize()<35){
            renderer = new InGameCustomFontRenderer(font,false);
        }else{
            renderer = new InGameCustomFontRenderer(font);
        }
        CUSTOM_FONT_RENDERERS.put(font,renderer);
        return renderer;
    }


}
