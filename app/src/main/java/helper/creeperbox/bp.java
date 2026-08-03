package helper.creeperbox;

import android.util.Log;

import java.io.IOException;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.LoginFinishEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class bp {

    public static boolean hasVerify = false;

    @SubscribeEvent
    public void onTick(LoginFinishEvent event){
//        if(!hasVerify){
//            hasVerify = true;
//            long req = event.randomIV;
//            OkHttpClient client = new OkHttpClient();
//            Request request = new Request.Builder()
//                    .url("http://103.40.14.120:15351")
//                    .post(RequestBody.create(String.valueOf(req), null))
//                    .build();
//            try (Response response = client.newCall(request).execute()) {
//                String rsp = response.body().string();
//                v(Long.parseLong(rsp));
//            } catch (IOException e) {
//
//            }
//        }
    }

}
