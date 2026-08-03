package helper.creeperbox.clickgui.font;

import java.util.HashMap;

public class Fonts {

    private static final HashMap<CustomFont, CustomFontRenderer> CUSTOM_FONT_RENDERERS = new HashMap<>();

    public static CustomFontRenderer getFontRenderer(String fontName,int size){
        return getFontRenderer(new CustomFont(fontName,size));
    }

    public static CustomFontRenderer getFontRenderer(CustomFont font){
        if(CUSTOM_FONT_RENDERERS.containsKey(font)){
            return CUSTOM_FONT_RENDERERS.get(font);
        }
        CustomFontRenderer renderer;
        if(font.getSize()<35){
            renderer = new CustomFontRenderer(font,false);
        }else{
            renderer = new CustomFontRenderer(font);
        }
        CUSTOM_FONT_RENDERERS.put(font,renderer);
        return renderer;
    }

}
