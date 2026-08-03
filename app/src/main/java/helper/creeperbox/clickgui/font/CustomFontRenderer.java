package helper.creeperbox.clickgui.font;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import helper.creeperbox.utils.render.ColorUtil;
import helper.creeperbox.utils.render.Render2DUtil;
import helper.creeperbox.utils.render.TextureUtil;

public class CustomFontRenderer {

    private final Glyph[] glyphs = new Glyph[65536];

    private static final int SHADOW_COLOR = Color.argb(180,0,0,0);

    private final int imageSize;

    private final Canvas canvas;

    private final Paint paint;

    private final Bitmap bitmap;
    private final CustomFont font;
    private final boolean antiAliasing;

    private final int fontSize;

    private final float fontHeight;

    public CustomFontRenderer(CustomFont font){
        this(font,true);
    }

    public CustomFontRenderer(CustomFont font,boolean antiAliasing){
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

    public void drawGradientScaleString(String s,float x,float y,float height,float scale,int firstColor,int secondColor,boolean shadow,boolean vertical){
        Render2DUtil.startScale(x,y+height/2f,scale);
        drawGradientString(s,x,y+getMiddle(height),firstColor,secondColor,shadow,vertical);
        Render2DUtil.endScale();
    }

    public void drawGradientString(String s,float x,float y,int firstColor,int secondColor,boolean shadow,boolean vertical){
        if (s == null || s.isEmpty()) return;

        float currentWidth = 0f;
        float maxWidth = 0f;
        if(!vertical){
            maxWidth = getStringWidth(s);
        }

        for(int i = 0 ; i < s.length() ; i ++){
            char c = s.charAt(i);
            final Glyph glyph = getGlyph(c);
            if(shadow){
                int alpha = (int) (Color.alpha(firstColor)*0.5 + Color.alpha(secondColor)*0.5);
                int shadowColor = Color.argb(alpha<200?alpha:Color.alpha(SHADOW_COLOR),Color.red(SHADOW_COLOR),Color.green(SHADOW_COLOR),Color.blue(SHADOW_COLOR));
                drawGlyph(glyph,x+imageSize*0.0225f,y+imageSize*0.0225f, shadowColor);
            }
            if(vertical){
                glyph.drawGradient(x,y,firstColor,secondColor,true);
            }else{
                int first = ColorUtil.interpolateColor(firstColor,secondColor,currentWidth/maxWidth);
                int second =  ColorUtil.interpolateColor(firstColor,secondColor,(currentWidth+glyph.width)/maxWidth);
                glyph.drawGradient(x,y,first,second,false);
                currentWidth += glyph.width;
            }
            x+=glyph.width;
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
            Render2DUtil.image(textureID,x,y,imageSize,imageSize,color);
        }

        public void drawGradient(float x,float y,int firstColor,int secondColor,boolean vertical){
            Render2DUtil.gradientImage(textureID,x,y,imageSize,imageSize,firstColor,secondColor,vertical);
        }
    }
}
