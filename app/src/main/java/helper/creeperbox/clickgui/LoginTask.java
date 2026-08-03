package helper.creeperbox.clickgui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.module.modules.build.Login4399;

class LoginTask extends AsyncTask<Void, Void, String> {
    private String account;
    private String pwd;
    private Login4399GUI gui;
    private String verifyCode;
    public LoginTask(String account, String pwd, String verifyCode,Login4399GUI gui) {
        this.account = account;
        this.pwd = pwd;
        this.gui = gui;
        this.verifyCode = verifyCode;
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            return Login4399.INSTANCE.login4399(account,pwd,verifyCode);
        } catch (Exception e) {
            return "请检查你的网络设置";
        }
    }

    @Override
    protected void onPostExecute(String sauth) {


        if(sauth.equals("需要获取验证码")) {
            gui.showVerifyCode();
            gui.mkMsg("登录失败,原因:请先输入验证码");
            return;
        }

        if(sauth.equals("验证码错误")) {
            gui.updateVerifyCode();
            gui.mkMsg("登录失败,原因:验证码错误");
            return;
        }

        if(!sauth.contains("sauth_json")) {
            gui.mkMsg("登录失败,原因:"+sauth);
            return;
        }

        JsonObject jsonObject = JsonParser.parseString(sauth).getAsJsonObject();
        String sauthJson = jsonObject.get("sauth_json").getAsString();
        CreeperBox.INSTANCE.b(sauthJson);
        PythonCallerComponent.addClientQueue("import link_server\n" +
                "link_server.instance.forceQuitGame(3)");

        gui.hide();
        gui.mkMsg("4399登录成功");

    }
}