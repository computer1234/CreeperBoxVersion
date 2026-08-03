package helper.creeperbox.clickgui.font;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.utils.render.MatrixUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.TextureUtil;

public class InGameCustomFontRenderer {

    public final Glyph[] glyphs = new Glyph[65536];

    public static final int SHADOW_COLOR = Color.argb(180,0,0,0);

    public final int imageSize;

    public final Canvas canvas;

    public final Paint paint;

    public final Bitmap bitmap;
    public final InGameCustomFont font;
    public final boolean antiAliasing;

    public final int fontSize;

    public final float fontHeight;

    public InGameCustomFontRenderer(InGameCustomFont font){
        this(font,true);
    }

    public InGameCustomFontRenderer(InGameCustomFont font, boolean antiAliasing){
        this.antiAliasing = antiAliasing;
        this.imageSize = (int) (font.getSize() * 1.25);
        this.font = font;
        this.fontSize = font.getSize();


        bitmap = Bitmap.createBitmap(imageSize,imageSize,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        bitmap.eraseColor(0);
        paint = new Paint();
        paint.setColor(Color.WHITE);
        paint.setAntiAlias(antiAliasing);
        paint.setDither(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setFilterBitmap(true);
        paint.setTextSize(fontSize);
        if(font.getTypeface()!=null){
            paint.setTypeface(font.getTypeface());
        }

        this.fontHeight = paint.getFontMetrics().bottom - paint.getFontMetrics().top;
    }


    public float getHeight(){
        return fontHeight;
    }

    public float drawCenteredString(String s,float x,float y,int color){
        return drawString(s,x - (getStringWidth(s) / 2.0F) , y,color);
    }
    public float drawCenteredString(String s,float x,float y,int color,boolean shadow){
        return drawString(s,x - (getStringWidth(s) / 2.0F) , y,color,shadow);
    }

    public float getMiddle(float height){
        return (height - getHeight()) /2f;
    }


    public float getStringWidth(String s){
        if (s == null || s.isEmpty()) return 0;
        int width = 0;
        for(int i = 0 ; i < s.length() ; i ++){
            char c = s.charAt(i);
            width+=getGlyph(c).width;
        }
        return width;
    }

    public void drawScaleString(String s,float x,float y,float height,float scale,int color){
        Render2DUtil.startScale(x,y+height/2f,scale);
        drawString(s,x,y+getMiddle(height),color);
        Render2DUtil.endScale();
    }

    public float drawString(String s,float x,float y,int color) {
        return drawString(s,x,y,color,false);
    }

    public void drawFixWidthString(String s,float x,float y,float totalWidth,int totalLine,int color){
        if (s == null || s.isEmpty()) return;
        int line = 0;
        float width = 0;
        float tempX = x;
        float dotCount = 0;
        for(int i = 0 ; i < s.length() ; i ++){
            char c = s.charAt(i);

            Glyph glyph = getGlyph(c);

            width+=glyph.width;

            if(width>totalWidth){
                line++;
                if(line>=totalLine) return;
                y+=fontHeight;
                width = 0f;
                x = tempX;
            }

            if(line==totalLine-1 && totalWidth-width<50f){
                glyph = getGlyph('.');
                dotCount++;
            }

            drawGlyph(glyph,x,y,color);
            x+=glyph.width;

            if(dotCount>3) return;
        }
    }


    public float drawString(String s,float x,float y,int color,boolean shadow){
        if (s == null || s.isEmpty()) return 0;

        for(int i = 0 ; i < s.length() ; i ++){
            char c = s.charAt(i);

            final Glyph glyph = getGlyph(c);
            if(shadow){
                int alpha = Color.alpha(color);
                int shadowColor = Color.argb(alpha<200?alpha:Color.alpha(SHADOW_COLOR),Color.red(SHADOW_COLOR),Color.green(SHADOW_COLOR),Color.blue(SHADOW_COLOR));
                drawGlyph(glyph,x+imageSize*0.0225f,y+imageSize*0.0225f, shadowColor);
                drawGlyph(glyph,x,y,color);
            }else{
                drawGlyph(glyph,x,y,color);
            }

            x+=glyph.width;
        }
        return x;
    }


    private void drawGlyph(Glyph glyph, float x, float y, int color) {
        glyph.draw(x,y,color);
    }

    private Glyph getGlyph(char c){
        Glyph glyph = glyphs[c];

        if (glyph == null) {
            glyphs[c] = glyph = createGlyph(c);
        }

        return glyph;
    }

    private Glyph createGlyph(char c) {
        final String s = String.valueOf(c);
        bitmap.eraseColor(0);
        canvas.drawText(s,0,-paint.getFontMetrics().top,paint);
        return new Glyph(bitmap, paint.measureText(s));
    }



    private final class Glyph {
        public final int textureID;
        public final float width;
        public Glyph(Bitmap bitmap,float width){
            this.textureID = TextureUtil.createTexture(bitmap);
            this.width = width;
        }

        public void draw(float x, float y,int color) {
            RenderHelperComponent.imageXY(textureID,x,y,0,imageSize,imageSize,color, MatrixUtil.mvpMatrix());
        }
    }
}
