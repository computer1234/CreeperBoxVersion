package helper.creeperbox.clickgui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.InputType;
import android.util.Base64;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import helper.creeperbox.VerifyManager;
import helper.creeperbox.clickgui.component.clickgui.CBCategoryComponent;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.module.modules.build.Login4399;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginGUI {
    public Activity activity;
    public LoginGUI(Activity activity){
        this.activity = activity;
        showPopup(activity);
    }

    public void mkMsg(String msg) {
        activity.runOnUiThread(() -> Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_LONG).show());
    }

    private void showPopup(Activity activity) {

        LinearLayout mainLayout = new LinearLayout(activity);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        EditText cardEditText = new EditText(activity);
        cardEditText.setHint("账号");
        mainLayout.addView(cardEditText);
        EditText pwdEditText = new EditText(activity);
        pwdEditText.setHint("密码");
        mainLayout.addView(pwdEditText);





        LinearLayout imageCodeLayout = new LinearLayout(activity);
        imageCodeLayout.setOrientation(LinearLayout.HORIZONTAL);
        imageCodeLayout.setGravity(Gravity.CENTER);
        ImageView logoImage = new ImageView(activity);
        String base64Image =
                "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";
        byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        logoImage.setImageBitmap(decodedByte);

        int imageSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                150,
                activity.getResources().getDisplayMetrics()
        );

        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                imageSize,
                imageSize
        );
        imageParams.setMargins(0, 0, 20, 0);
        logoImage.setLayoutParams(imageParams);
        imageCodeLayout.addView(logoImage);

        EditText codeEditText = new EditText(activity);
        codeEditText.setHint("验证码");
        codeEditText.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout.LayoutParams codeParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        codeParams.width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                120,
                activity.getResources().getDisplayMetrics()
        );
        codeEditText.setLayoutParams(codeParams);
        imageCodeLayout.addView(codeEditText);
        mainLayout.addView(imageCodeLayout);

        AlertDialog loginDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("4399登录")
                .setCancelable(false)
                .setView(mainLayout)
                .setNegativeButton("登录", null)
                .setPositiveButton("取消",null)
                .create();

        loginDialog.setOnShowListener(dialog -> {
            loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CCCCCC")));
        });


        loginDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canCloseDialog(dialog,false);

                String account = cardEditText.getText().toString().trim();
                String pwd = pwdEditText.getText().toString().trim();

                if(account.isEmpty() || pwd.isEmpty()){
                    mkMsg("请输入有效的账号或密码");
                    return;
                }

//                new LoginTask(account,pwd,LoginGUI.this,dialog).execute();
            }
        });

        loginDialog.setButton(AlertDialog.BUTTON_POSITIVE, "取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                activity.runOnUiThread(() -> {
                    canCloseDialog(dialog, true);
                    dialog.dismiss();
                });
            }
        });

        loginDialog.show();
    }


    public void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");

            field.setAccessible(true);

            field.set(dialogInterface, close);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
