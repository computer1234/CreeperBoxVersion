package helper.creeperbox.clickgui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import helper.creeperbox.utils.render.TextureUtil;

public class ClickIcon extends PopupWindow {
    private Activity activity;
    private boolean enable;
    private LinearLayout mainLine;
    private ImageView imageView;
    private boolean hasMove;

    private boolean CanMove;

    private float lastX;

    private float lastY;

    private float nowX;

    private float nowY;

    private float tranX;

    private float tranY;

    private float X;

    private float Y;

    private boolean isAnimation = false;

    public ClickIcon(Activity activity) {
        super(activity);
        this.activity = activity;
        initUI();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadius(20.f);
        drawable.setColor(0x00000000);
        mainLine = new LinearLayout(activity);
        mainLine.setOrientation(LinearLayout.VERTICAL);
        mainLine.setGravity(Gravity.CENTER);
        mainLine.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        mainLine.setBackground(drawable);

        imageView = new ImageView(activity);
        imageView.setBackground(drawable);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
        try {
            Bitmap bitmap = BitmapFactory.decodeStream(TextureUtil.class.getClassLoader().getResourceAsStream("assets/icon/logo.png"));
            imageView.setImageBitmap(bitmap);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        imageView.setOnClickListener(v -> {
            if (hasMove) return;
            ClickGUIRenderer.toggleGUI = !ClickGUIRenderer.toggleGUI;
        });
        imageView.setOnLongClickListener(v -> {
            CanMove = true;
//            LinearLayout m = new LinearLayout(activity);
//            m.setBackgroundColor(0x50777778);
//            m.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
//            final PopupWindow bw = new PopupWindow();
//            bw.setAnimationStyle(android.R.style.Animation_Dialog);
//            bw.setWidth(-1);
//            bw.setHeight(-1);
//            bw.setFocusable(false);
//            bw.setTouchable(false);
//            bw.setOutsideTouchable(false);
//            bw.setClippingEnabled(false);
//            bw.setBackgroundDrawable(new ColorDrawable(0));
//            bw.setContentView(m);
//            bw.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//            AnimationHelper.UIReveal(m, (int) lastX, (int) lastY, 0, (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 1.2f), 1000);
//            AnimationHelper.UIFadein(m, 100, 0, 1000);
//            new Handler(Looper.getMainLooper()).postDelayed(bw::dismiss, 1000);
//            AnimationHelper.startVibrate(activity);
            return true;
        });
        imageView.setOnTouchListener((view, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                view.animate().scaleX(0.8f).scaleY(0.8f).setDuration(100).start();
                lastX = event.getRawX();
                lastY = event.getRawY();
            } else if (action == MotionEvent.ACTION_UP) {
                view.animate().scaleX(1f).scaleY(1f).setDuration(300).start();
                CanMove = false;
                hasMove = false;
            } else if (action == MotionEvent.ACTION_MOVE && CanMove) {
                nowX = event.getRawX();
                nowY = event.getRawY();
                tranX = nowX - lastX;
                tranY = nowY - lastY;
                X += tranX;
                Y += tranY;
                update(Math.round(X), Math.round(Y), -1, -1);
                lastX = nowX;
                lastY = nowY;
                hasMove = true;
            }
            return false;
        });
        mainLine.addView(imageView);
        setContentView(mainLine);
        setWidth(80);
        setHeight(80);
        setBackgroundDrawable(new ColorDrawable(0));
        setTouchable(true);
        setFocusable(false);
        setOutsideTouchable(false);
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

}
