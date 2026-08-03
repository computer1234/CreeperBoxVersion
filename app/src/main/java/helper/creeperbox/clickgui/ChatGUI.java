package helper.creeperbox.clickgui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;

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
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.modules.build.ChatHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGUI  {
    private ChatHelper helper;
    public ChatGUI(ChatHelper helper){
        this.helper = helper;
        showChatPopup(CreeperBox.INSTANCE.activity);
    }
    private void showChatPopup(Activity activity) {

        LinearLayout mainLayout = new LinearLayout(activity);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        EditText cardEditText = new EditText(activity);
        cardEditText.setHint("内容");
        mainLayout.addView(cardEditText);


        AlertDialog loginDialog = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setTitle("请输入你的文本或指令")
                .setCancelable(false)
                .setView(mainLayout)
                .setPositiveButton("确定", null)
                .setNegativeButton("取消", null)
                .create();

        loginDialog.setOnShowListener(dialog -> {
            loginDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#CCCCCC")));
        });


        loginDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String msg = cardEditText.getText().toString().trim();
                canCloseDialog(dialog,true);
                helper.setMsg(msg);
            }
        });

        loginDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                canCloseDialog(dialog,true);
            }
        });

        loginDialog.show();
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

}
