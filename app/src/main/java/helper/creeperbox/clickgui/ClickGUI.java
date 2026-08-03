package helper.creeperbox.clickgui;

import android.app.Activity;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.ColorDrawable;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

public class ClickGUI extends PopupWindow {

    private GLSurfaceView mGLSurfaceView;

    private Activity activity;


    private int Width;

    private int Height;


    @Override
    public boolean isTouchable() {
        return super.isTouchable();
    }

    public ClickGUI(final Activity activity) {
        super(activity);
        this.activity = activity;

        Width = W();
        Height = H();

        if (Width < Height) {
            Width = H();
            Height = W();
        }

        mGLSurfaceView = new GLSurfaceView(activity);

        mGLSurfaceView.setEGLContextClientVersion(3);
        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        mGLSurfaceView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        mGLSurfaceView.setZOrderOnTop(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            mGLSurfaceView.setBackgroundTintBlendMode(BlendMode.MULTIPLY);
        }
        mGLSurfaceView.setRenderer(new ClickGUIRenderer(Width,Height));
        setContentView(mGLSurfaceView);

        setBackgroundDrawable(new ColorDrawable(0));
        setOutsideTouchable(false);
        setClippingEnabled(false);
        setFocusable(false);
        setTouchable(false);

        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);

        this.showAtLocation(this.activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }


    private int W() {
        return (int) (activity.getResources().getDisplayMetrics().widthPixels);
    }
    private int H() {
        return (int) (activity.getResources().getDisplayMetrics().heightPixels);
    }





}
