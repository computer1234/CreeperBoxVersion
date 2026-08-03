package helper.creeperbox.clickgui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.modules.build.ChatHelper;
import helper.creeperbox.feature.module.modules.build.TeleportHelper;

public class TeleportGUI {
    private TeleportHelper helper;
    public TeleportGUI(TeleportHelper helper){
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
                .setTitle("请输入你要传送的坐标 用空格分开")
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
