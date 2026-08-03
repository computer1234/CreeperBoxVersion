package helper.creeperbox.clickgui;

import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import helper.creeperbox.clients.CreeperBox;

public class KeyBindingButton extends PopupWindow {
    private Activity activity;
    private String name;
    private boolean enable = false;
    private LinearLayout mainLine;
    private TextView title;
    private float lastX;

    private float lastY;

    private float nowX;

    private float nowY;

    private float tranX;

    private float tranY;

    private float X;

    private float Y;
    private boolean isOpen;
    private View.OnClickListener on;
    public KeyBindingButton(String text, boolean enable, View.OnClickListener v) {
        super(CreeperBox.INSTANCE.activity);
        this.activity = CreeperBox.INSTANCE.activity;
        this.enable = enable;
        this.name = text;
        this.on = v;
        initUI();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initUI() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setCornerRadii(new float[]{19, 19, 19, 19, 19, 19, 19, 19});
        drawable.setColor(0x69000000);
        drawable.setStroke(6, enable ? 0xFFA5A5A5 : 0x00000000);
        mainLine = new LinearLayout(activity);
        mainLine.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
        mainLine.setOrientation(LinearLayout.VERTICAL);
        mainLine.setGravity(Gravity.CENTER);
        mainLine.setBackground(drawable);


        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.topMargin =  (int) (1080 * 0.0137037037037037f);
        layoutParams.bottomMargin = (int) (1080 * 0.0137037037037037f);
        layoutParams.leftMargin = (int) (2400 * 0.0088020833333333f);
        layoutParams.rightMargin = (int) (2400 * 0.0088020833333333f);
        title = new TextView(activity);
        title.setText(name);
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12.f);
        title.setLayoutParams(layoutParams);
        title.setTextColor(Color.WHITE);
        mainLine.addView(title);

        mainLine.setOnClickListener(v -> {
            setEnable(!isEnable());
            on.onClick(v);
        });

        mainLine.setOnLongClickListener(v -> true);
        mainLine.setOnTouchListener((view, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                lastX = event.getRawX();
                lastY = event.getRawY();
            } else if (action == MotionEvent.ACTION_UP) {

            } else if (action == MotionEvent.ACTION_MOVE) {
                nowX = event.getRawX();
                nowY = event.getRawY();
                tranX = nowX - lastX;
                tranY = nowY - lastY;
                X += tranX;
                Y += tranY;
                update(Math.round(X), Math.round(Y), -1, -1);
                lastX = nowX;
                lastY = nowY;
            }
            return false;
        });


        setContentView(mainLine);
        setBackgroundDrawable(new ColorDrawable(0));
        setTouchable(true);
        setFocusable(false);
        setOutsideTouchable(false);
    }


    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getY() {
        return Y;
    }


    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean z) {
        activity.runOnUiThread(()->{
            if (z) {
                showAtLocation(getContentView(),Gravity.CENTER, 0, 0);
                update(Math.round(this.X), Math.round(this.Y), -1, -1);
            }else {
                dismiss();
            }
            isOpen = z;
        });
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;

        activity.runOnUiThread(()->{
            GradientDrawable drawable = (GradientDrawable) mainLine.getBackground();

            int strokeStartColor = enable ? 0x00000000 : 0xFFA5A5A5;
            int strokeEndColor = enable ? 0xFFA5A5A5 : 0x00000000;

            int bgStartColor = enable ? 0x69000000 : 0x664e4e4e;
            int bgEndColor = enable ? 0x664e4e4e : 0x69000000;

            ValueAnimator strokeAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), strokeStartColor, strokeEndColor);
            strokeAnimator.setDuration(300);

            ValueAnimator bgAnimator = ValueAnimator.ofObject(new ArgbEvaluator(), bgStartColor, bgEndColor);
            bgAnimator.setDuration(300);

            strokeAnimator.addUpdateListener(animator -> {
                drawable.setStroke(6, (int) animator.getAnimatedValue());
            });

            bgAnimator.addUpdateListener(animator -> {
                drawable.setColor((int) animator.getAnimatedValue());
                mainLine.setBackground(drawable);
            });

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(strokeAnimator, bgAnimator);
            animatorSet.start();
        });

    }


}
