package helper.creeperbox.clickgui;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.modules.build.Login4399;

public class Login4399GUI extends PopupWindow {
    public Activity activity;
    private EditText input1, input2, input3;
    private ImageView tvVerifyCode;
    private LinearLayout verifyCodeLayout;
    public void mkMsg(String msg) {
        activity.runOnUiThread(() -> Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_SHORT).show());
    }

    public Login4399GUI(Context context) {

        super(context);
        this.activity = CreeperBox.INSTANCE.activity;

        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setPadding(10, 10, 10, 10);
        mainLayout.setBackgroundColor(Color.WHITE);

        TextView titleView = new TextView(context);
        titleView.setText("4399登录");
        titleView.setTextSize(15);
        titleView.setTextColor(Color.BLACK);
        titleView.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams titleParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        titleParams.setMargins(0, 0, 0, 20);
        titleView.setLayoutParams(titleParams);

        input1 = createStyledEditText(context, "UserName");
        input2 = createStyledEditText(context, "PassWord");

        verifyCodeLayout = new LinearLayout(context);
        verifyCodeLayout.setOrientation(LinearLayout.HORIZONTAL);
        verifyCodeLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        input3 = createStyledEditText(context, "输入验证码");
        LinearLayout.LayoutParams inputParams = new LinearLayout.LayoutParams(
                0,
                80,
                1.0f);
        inputParams.setMargins(0, 0, 10, 0);
        input3.setLayoutParams(inputParams);

        tvVerifyCode = new ImageView(context);

        LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(
                dpToPx(context, 80),
                dpToPx(context, 40));
        tvVerifyCode.setLayoutParams(codeParams);

        verifyCodeLayout.addView(input3);
        verifyCodeLayout.addView(tvVerifyCode);

        LinearLayout buttonLayout = new LinearLayout(context);
        buttonLayout.setOrientation(LinearLayout.HORIZONTAL);
        buttonLayout.setGravity(Gravity.CENTER);
        buttonLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        Button positiveButton = createStyledButton(context, "确定");
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String account = input1.getText().toString().trim();
                String pwd = input2.getText().toString().trim();
                String verifyCode = input3.getText().toString().trim();

                if(account.isEmpty() || pwd.isEmpty()){
                    mkMsg("请输入有效的账号或密码");
                    return;
                }

                new LoginTask(account,pwd, verifyCode,Login4399GUI.this).execute();
            }
        });

        Button negativeButton = createStyledButton(context, "取消");
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                dpToPx(context, 100),
                dpToPx(context, 40));
        buttonParams.setMargins(20, 0, 20, 0);
        positiveButton.setLayoutParams(buttonParams);
        negativeButton.setLayoutParams(buttonParams);

        buttonLayout.addView(negativeButton);
        buttonLayout.addView(positiveButton);

        mainLayout.addView(titleView);
        mainLayout.addView(input1);
        mainLayout.addView(input2);
        mainLayout.addView(verifyCodeLayout);
        mainLayout.addView(buttonLayout);

        setContentView(mainLayout);

        verifyCodeLayout.setVisibility(View.GONE);
        setFocusable(true);
        setOutsideTouchable(false);
        showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private EditText createStyledEditText(Context context, String hint) {
        EditText editText = new EditText(context);
        editText.setHint(hint);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                dpToPx(context, 40));
        params.setMargins(0, 0, 0, 15);
        editText.setTextSize(12);
        editText.setLayoutParams(params);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        setViewBackground(editText, Color.TRANSPARENT, 20, Color.parseColor("#3ec1d3"));
        editText.setPadding(15, 0, 15, 0);
        return editText;
    }

    private Button createStyledButton(Context context, String text) {
        Button button = new Button(context);
        button.setText(text);
        setViewBackground(button, "#3ec1d3", 20, Color.TRANSPARENT);
        button.setTextColor(Color.WHITE);
        return button;
    }

    private void setViewBackground(View view, String bgColor, float radius, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(Color.parseColor(bgColor));
        if (strokeColor != Color.TRANSPARENT) {
            drawable.setStroke(2, strokeColor);
        }
        view.setBackground(drawable);
    }
    private void setViewBackground(View view, int bgColor, float radius, int strokeColor) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(bgColor);
        if (strokeColor != Color.TRANSPARENT) {
            drawable.setStroke(2, strokeColor);
        }
        view.setBackground(drawable);
    }
    private int dpToPx(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    public void showVerifyCode(){
        Bitmap decodedByte = BitmapFactory.decodeByteArray(Login4399.INSTANCE.verifyData, 0, Login4399.INSTANCE.verifyData.length);
        tvVerifyCode.setImageBitmap(decodedByte);
        verifyCodeLayout.setVisibility(View.VISIBLE);
    }


    public void updateVerifyCode(){
        Bitmap decodedByte = BitmapFactory.decodeByteArray(Login4399.INSTANCE.verifyData, 0, Login4399.INSTANCE.verifyData.length);
        tvVerifyCode.setImageBitmap(decodedByte);
    }

    public void hide(){
        activity.runOnUiThread(this::dismiss);
    }
}