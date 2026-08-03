package helper.creeperbox.clickgui.font;

import android.content.Context;
import android.graphics.Typeface;
import helper.creeperbox.clients.CreeperBox;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class InGameCustomFont {

    private Typeface typeface;
    private final int size;
    private final String fontName;
    public InGameCustomFont(String fontName, int size){
        this.fontName = fontName;
        this.size = size;

        try {
            StringBuffer sb = new StringBuffer();
            sb.append(CreeperBox.INSTANCE.context.getCacheDir());
            sb.append("/");
            sb.append(fontName);
            this.typeface = Typeface.createFromFile(sb.toString());
        }catch (Exception e){
            e.printStackTrace();
            this.typeface = null;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof InGameCustomFont)){
            return false;
        }
        InGameCustomFont o = (InGameCustomFont) obj;
        return o.fontName != null && fontName != null && o.fontName.equals(fontName) && size == o.size;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getSize() {
        return size;
    }
}
