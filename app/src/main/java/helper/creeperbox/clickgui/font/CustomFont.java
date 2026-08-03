package helper.creeperbox.clickgui.font;

import android.content.Context;
import android.graphics.Typeface;

import androidx.annotation.Nullable;

import helper.creeperbox.clients.CreeperBox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomFont {

    static {
        String[] fontFiles = new String[] {
                "pingfang.ttf",
                "pingfang-medium.ttf",
                "pingfang-thin.ttf",
                "anta.ttf"
        };


        Context context = CreeperBox.INSTANCE.context;


        for(String fontFile:fontFiles) {
            InputStream inputStream = null;
            FileOutputStream outputStream = null;

            try {
                inputStream = CustomFont.class.getClassLoader().getResourceAsStream("assets/fonts/" + fontFile);
                if (inputStream == null) {
                    continue;
                }

                File outFile = new File(context.getCacheDir(), fontFile);
                outputStream = new FileOutputStream(outFile);

                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }

                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (outputStream != null) {
                        outputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Typeface typeface;
    private final int size;
    private final String fontName;
    public CustomFont(String fontName,int size){
        this.fontName = fontName;
        this.size = size;

        try {
            this.typeface = Typeface.createFromFile(CreeperBox.INSTANCE.context.getCacheDir()+"/"+fontName);
        }catch (Exception e){
            e.printStackTrace();
            this.typeface = null;
        }
    }


    @Override
    public boolean equals(@Nullable Object obj) {
        if(!(obj instanceof CustomFont)){
            return false;
        }
        CustomFont o = (CustomFont) obj;
        return o.fontName != null && fontName != null && o.fontName.equals(fontName) && size == o.size;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public int getSize() {
        return size;
    }

}
