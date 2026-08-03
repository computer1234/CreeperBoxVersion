package helper.creeperbox.clickgui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import helper.creeperbox.VerifyManager;
import helper.creeperbox.clickgui.component.clickgui.CBCategoryComponent;
import helper.creeperbox.clients.CreeperBox;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.TimeZone;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class VerifyGUI {

    private OkHttpClient okHttpClient;
    private Activity activity;


    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private static final String DATA = "account.json";

    private void showLoginPopup(Activity activity) {

        LinearLayout mainLayout = new LinearLayout(activity);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        EditText cardEditText = new EditText(activity);
        cardEditText.setHint("卡密");
        mainLayout.addView(cardEditText);


        AlertDialog loginDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("验证")
                .setCancelable(false)
                .setView(mainLayout)
                .setPositiveButton("登录", null)
                .create();

        loginDialog.setOnShowListener(dialog -> {
            loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CCCCCC")));
        });


        final boolean[] isVerifying = {false}; // 验证状态标志
        
        loginDialog.setButton(AlertDialog.BUTTON_POSITIVE, "登录", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 防止重复点击
                if (isVerifying[0]) {
                    mkMsg("正在验证，请勿频繁点击");
                    return;
                }
                
                canCloseDialog(dialog, false);
                String card = cardEditText.getText().toString().trim();

                if (card.isEmpty()) {
                    mkMsg("请输入你的卡密");
                    return;
                }

                // 保存卡密
                writeData(card);
                
                // 设置验证中状态
                isVerifying[0] = true;

                // 在后台线程执行验证（阻塞调用）
                new Thread(() -> {
                    boolean success = CreeperBox.verify(card, "", "");
                    
                    activity.runOnUiThread(() -> {
                        isVerifying[0] = false; // 重置验证状态
                        if (success) {
                            VerifyManager.isVerify = true;
                            
                            // 更新到期时间显示
                            updateTimeLeft();
                            
                            mkMsg("验证成功");
                            canCloseDialog(dialog, true);
                            ((AlertDialog) dialog).dismiss();
                        } else {
                            String status = CreeperBox.getVerifyStatus();
                            String error = CreeperBox.getVerifyError();
                            mkMsg("验证失败: " + status + " - " + error);
                            canCloseDialog(dialog, true);
                        }
                    });
                }).start();
            }
        });

        loginDialog.show();
        File dataFile = new File(activity.getDataDir(),DATA);
        JsonObject jsonObject = null;
        try {
            final FileReader fileReader = new FileReader(dataFile);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            jsonObject = GSON.fromJson(bufferedReader, JsonObject.class);
            bufferedReader.close();
            fileReader.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        if(jsonObject!=null && jsonObject.has("card")){
            String card = jsonObject.get("card").getAsString();
            cardEditText.setText(card);
        }

    }

    private void canCloseDialog(DialogInterface dialogInterface, boolean close) {
        try {
            Field field = dialogInterface.getClass().getSuperclass().getDeclaredField("mShowing");

            field.setAccessible(true);

            field.set(dialogInterface, close);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void writeData(String card){
        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("card",card);
        final FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(new File(activity.getDataDir(),DATA));
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            GSON.toJson(jsonObject, bufferedWriter);

            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final String SECRET_KEY = "DQxIiwgIm9kaSI6I";
    private static final String INIT_VECTOR = "jRiMzY3zGViYzAS4";

    private void mkMsg(String msg) {
        activity.runOnUiThread(() -> Toast.makeText(activity.getBaseContext(), msg, Toast.LENGTH_LONG).show());
    }

    public VerifyGUI(Activity activity){
        this.activity = activity;
        okHttpClient = new OkHttpClient();
        showLoginPopup(activity);
    }

    /**
     * 更新到期时间显示
     */
    private void updateTimeLeft() {
        long remainingTime = CreeperBox.y(); // 获取剩余时间（毫秒）
        
        if (remainingTime == -1) {
            // 永久卡
            CBCategoryComponent.timeLeft = "永久有效";
        } else if (remainingTime <= 0) {
            // 已过期
            CBCategoryComponent.timeLeft = "已过期";
        } else {
            // 计算到期时间
            long expireTime = System.currentTimeMillis() + remainingTime;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            CBCategoryComponent.timeLeft = sdf.format(new Date(expireTime));
        }
    }

}
