package helper.creeperbox.feature.module.modules.build;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import helper.creeperbox.VerifyManager;
import helper.creeperbox.clickgui.Login4399GUI;
import helper.creeperbox.clickgui.LoginGUI;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import io.netty.handler.codec.base64.Base64Encoder;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@ModuleInfo(name = "4399登录", category = Category.Build)
public class Login4399 extends Module {


    public static Login4399 INSTANCE;

    public Login4399(){
        INSTANCE = this;
    }
    @Override
    public void onEnable() {

        if(!VerifyManager.isVerify) return;

        CreeperBox.INSTANCE.activity.runOnUiThread(()->{
            Login4399GUI gui = new Login4399GUI(CreeperBox.INSTANCE.context);
        });
    }

    private final Gson gson = new Gson();


    private String encodeValue(String value) throws Exception {
        return URLEncoder.encode(value, StandardCharsets.UTF_8.name())
                .replace("+", "%20");
    }

    private final OkHttpClient client4399 = new OkHttpClient.Builder()
            .cookieJar(new CookieJar() {
                private final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();

                @Override
                public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    cookieStore.put(url.host(), cookies);
                }

                @Override
                public List<Cookie> loadForRequest(HttpUrl url) {
                    List<Cookie> cookies = cookieStore.get(url.host());
                    return cookies != null ? cookies : new ArrayList<>();
                }
            })
            .build();

    private String userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0";


    public byte[] verifyData = new byte[]{0};

    public String lastCaptchaID = "";


    public String login4399(String userName, String password,String verifyCode) throws Exception {

        boolean hasVerify = !verifyCode.isEmpty();

        //验证码
        String captcha = null;
        String captchaID = null;

        if(!hasVerify || lastCaptchaID.isEmpty()){
            //verify
            String currentTime = String.valueOf(System.currentTimeMillis());

            String verifyUrl = "https://ptlogin.4399.com/ptlogin/verify.do?" +
                    "username=" + userName + "&appId=kid_wdsj&t=" + currentTime + "&inputWidth=iptw2&v=1";

            Request verifyRequest = new Request.Builder()
                    .url(verifyUrl)
                    .header("User-Agent", userAgent)
                    .build();

            Response verifyResponse = client4399.newCall(verifyRequest).execute();

            if (!verifyResponse.isSuccessful()) {
                return "获取验证接口失败";
            }

            String verifyText = verifyResponse.body().string();

            if(!verifyText.equals("0")) {
                lastCaptchaID = getBetweenStrings(verifyText, "captchaId=", "'");

                if (lastCaptchaID == null || lastCaptchaID.isEmpty()) {
                    return "获取captchaID失败";
                }

                Request captchaRequest = new Request.Builder()
                        .url("https://ptlogin.4399.com/ptlogin/captcha.do?captchaId=" + lastCaptchaID)
                        .build();

                Response captchaResponse = client4399.newCall(captchaRequest).execute();
                if (!captchaResponse.isSuccessful()) {
                    return "获取验证码接口失败";
                }

                verifyData = captchaResponse.body().bytes();
                return "需要获取验证码";
            }
        }else{
            captchaID = lastCaptchaID;
            captcha = verifyCode.trim();
        }

        String loginData = buildLoginData(userName, password, captchaID, captcha);
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        RequestBody body = RequestBody.create(loginData, mediaType);
        Request loginRequest = new Request.Builder()
                .url("https://ptlogin.4399.com/ptlogin/login.do?v=1")
                .post(body)
                .header("User-Agent", userAgent)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();

        Response loginResponse = client4399.newCall(loginRequest).execute();
        String loginText = loginResponse.body().string();

        if(loginText.contains("验证码错误")) {
            lastCaptchaID = "";
            login4399(userName,password,"");
            return "验证码错误";
        }

        if(loginText.contains("密码错误")) {
            return "密码错误";
        }


        if(loginText.contains("用户不存在")) {
            return "用户不存在";
        }

        String randtime = extractBetween(loginText, "parent.timestamp = \"", "\"");
        if (randtime == null) {
            return "获取时间戳失败";
        }

        String cookieString = getCookies(loginResponse);

        if(cookieString.isEmpty()){
            return "Cookie获取失败";
        }

        URI targetUri = new URI("https://ptlogin.4399.com/ptlogin/checkKidLoginUserCookie.do");
        CookieManager cookieManager = new CookieManager();
        for (String cookie : cookieString.split(";")) {
            String[] cookieParts = cookie.trim().split("=", 2);
            if (cookieParts.length == 2) {
                HttpCookie httpCookie = new HttpCookie(cookieParts[0].trim(), cookieParts[1].trim());
                httpCookie.setPath("/");
                httpCookie.setDomain(targetUri.getHost());
                cookieManager.getCookieStore().add(targetUri, httpCookie);
            }
        }


        //login check
        String checkUrl = "https://ptlogin.4399.com/ptlogin/checkKidLoginUserCookie.do?" +
                "appId=kid_wdsj&gameUrl=http://cdn.h5wan.4399sj.com/microterminal-h5-frame?" +
                "game_id=500352&rand_time=" + randtime + "&nick=null&onLineStart=false&" +
                "show=1&isCrossDomain=1&retUrl=http%253A%252F%252Fptlogin.4399.com" +
                "%252Fresource%252Fucenter.html%253Faction%253Dlogin%2526appId%253Dkid_wdsj%2526" +
                "loginLevel%253D8%2526regLevel%253D8%2526bizId%253D2100001792%2526externalLogin%253D" +
                "qq%2526qrLogin%253Dtrue%2526layout%253Dvertical%2526level%253D101%2526" +
                "css%253Dhttp%253A%252F%252Fmicrogame.5054399.net%252Fv2%252Fresource%252F" +
                "cssSdk%252Fdefault%252Flogin.css%2526v%253D2018_11_26_16%2526" +
                "postLoginHandler%253Dredirect%2526checkLoginUserCookie%253Dtrue%2526" +
                "redirectUrl%253Dhttp%25253A%25252F%25252Fcdn.h5wan.4399sj.com%25252F" +
                "microterminal-h5-frame%25253Fgame_id%25253D500352%252526rand_time%25253D" + randtime;

        OkHttpClient redirectClient = new OkHttpClient.Builder()
                .followRedirects(false)
                .cookieJar(new CookieJar() {
                    @Override
                    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
                    }

                    @Override
                    public List<Cookie> loadForRequest(HttpUrl url) {
                        return new ArrayList<>();
                    }
                })
                .build();


        Request request = new Request.Builder()
                .url(checkUrl)
                .header("User-Agent", userAgent)
                .header("Cookie", cookieString)
                .build();

        Response response = redirectClient.newCall(request).execute();

        if (response.code() != 302) {
            return "检查登录状态失败";
        }



        String redirectUrl = response.header("Location");
        if(redirectUrl == null || redirectUrl.isEmpty()) {
            return "获取重定向地址失败";
        }

        URI uri = new URI(redirectUrl);
        if (!"cdn.h5wan.4399sj.com".equals(uri.getHost())) {
            return "重定向域名错误";
        }



        Map<String, String> queryParams = splitQuery(uri);
        String sig = queryParams.get("sig");
        String uid = queryParams.get("uid");
        String time = queryParams.get("time");
        String validateState = queryParams.get("validateState");

        if (sig == null || uid == null || time == null || validateState == null) {
            return "解析重定向参数失败";
        }

        //SDK Login
        String sdkUrl = "https://microgame.5054399.net/v2/service/sdk/info?" +
                "callback=&queryStr=game_id%3D500352%26nick%3Dnull%26sig%3D"+sig+"%26" +
                "uid%3D"+uid+"%26fcm%3D0%26show%3D1%26isCrossDomain%3D1%26rand_time%3D"+randtime+"%26" +
                "ptusertype%3D4399%26time%3D"+time+"%26validateState%3D"+validateState+"%26" +
                "username%3D"+userName.toLowerCase()+"&_="+time;

        Request sdkRequest = new Request.Builder()
                .url(sdkUrl)
                .header("User-Agent", userAgent)
                .build();
        Response sdkResponse = client4399.newCall(sdkRequest).execute();

        String responseBody = sdkResponse.body().string();
        JsonObject sdkJson = gson.fromJson(responseBody, JsonObject.class);
        JsonObject data = sdkJson.getAsJsonObject("data");

        if (data == null || !data.has("sdk_login_data")) {
            return "解析SDK数据失败";
        }

        String sdkLoginData = data.get("sdk_login_data").getAsString();
        if (sdkLoginData == null || sdkLoginData.isEmpty()) {
            return "解析SDK数据失败";
        }

        Map<String, String> sdkParams = splitQueryString(sdkLoginData);
        String sessionId = sdkParams.get("token");
        if (sessionId == null || sessionId.isEmpty()) {
            return "获取token失败";
        }


        String login_sn = generatorDeviceID();
        String deviceId = generatorDeviceID();
        Map<String, Object> sauth = new HashMap<>();
        sauth.put("aim_info", "{\"aim\":\"127.0.0.1\",\"country\":\"CN\",\"tz\":\"+0800\",\"tzid\":\"\"}");
        sauth.put("app_channel", "4399pc");
        sauth.put("client_login_sn", login_sn);
        sauth.put("deviceid", deviceId);
        sauth.put("gameid", "x19");
        sauth.put("gas_token", "");
        sauth.put("ip", "127.0.0.1");
        sauth.put("login_channel", "4399pc");
        sauth.put("platform", "pc");
        sauth.put("realname", "{\"realname_type\":\"0\"}");
        sauth.put("sdk_version", "1.0.0");
        sauth.put("sdkuid", uid);
        sauth.put("sessionid", sessionId);
        sauth.put("source_platform", "pc");
        sauth.put("timestamp", time);
        sauth.put("udid", deviceId);
        sauth.put("userid", userName.toLowerCase());

        Gson gson = new Gson();
        String sauthJsonValue = gson.toJson(sauth);
        JsonObject wrapper = new JsonObject();
        wrapper.addProperty("sauth_json", sauthJsonValue);
        String sauthJson = gson.toJson(wrapper);



        //Final Login
        Request uniRequest = new Request.Builder()
                .url("https://mgbsdk.matrix.netease.com/x19/sdk/uni_sauth")
                .header("User-Agent", "WPFLauncher/0.0.0.0")
                .post(RequestBody.create(sauthJsonValue, MediaType.parse("application/json; charset=utf-8")))
                .build();

        Response uniResponse = client4399.newCall(uniRequest).execute();
        if(uniResponse.code() != 200) {
            return "统一认证请求失败";
        }

        Request finalRequest = new Request.Builder()
                .url("https://x19obtcore.nie.netease.com:8443/login-otp")
                .header("User-Agent", "WPFLauncher/0.0.0.0")
                .post(RequestBody.create(sauthJson, MediaType.parse("text/plain; charset=utf-8")))
                .build();

        Response finalResponse = client4399.newCall(finalRequest).execute();

        JsonObject finalJson = JsonParser.parseString(finalResponse.body().string()).getAsJsonObject();
        if (finalJson == null ||
                !finalJson.has("entity") ||
                !finalJson.getAsJsonObject("entity").has("aid")) {
            return "获取aid失败";
        }

        return sauthJson;
    }

    private String getBetweenStrings(String str, String start, String end) {
        int startIndex = str.indexOf(start);
        if (startIndex == -1) {
            return null;
        }

        startIndex += start.length();
        int endIndex = str.indexOf(end, startIndex);
        return endIndex == -1 ? null : str.substring(startIndex, endIndex);
    }


    private static String extractBetween(String text, String start, String end) {
        int startIdx = text.indexOf(start);
        if (startIdx == -1) return null;
        startIdx += start.length();
        int endIdx = text.indexOf(end, startIdx);
        return endIdx != -1 ? text.substring(startIdx, endIdx) : null;
    }


    public static String generatorDeviceID() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            hexString.append(String.format("%02X", b));
        }
        return hexString.toString();
    }


    private String getCookies(Response response) {
        List<String> cookies = response.headers("Set-Cookie");
        if (cookies.isEmpty()) {
            return "";
        }
        return cookies.stream()
                .map(cookie -> cookie.split(";"))
                .filter(parts -> parts.length > 0)
                .map(parts -> parts[0])
                .collect(Collectors.joining("; "));
    }


    private Map<String, String> splitQueryString(String query) {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? pair.substring(0, idx) : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? pair.substring(idx + 1) : null;
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    private static Map<String, String> splitQuery(URI uri) throws IOException {
        Map<String, String> queryPairs = new LinkedHashMap<>();
        String query = uri.getQuery();
        if (query == null) {
            return queryPairs;
        }

        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            String value = idx > 0 && pair.length() > idx + 1 ?
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    private String buildLoginData(String username,
                                  String password,
                                  String captchaID,
                                  String captcha) throws Exception {
        return new StringBuilder()
                .append("loginFrom=uframe")
                .append("&postLoginHandler=default")
                .append("&layoutSelfAdapting=true")
                .append("&externalLogin=qq")
                .append("&displayMode=popup")
                .append("&layout=vertical")
                .append("&bizId=2100001792")
                .append("&appId=kid_wdsj")
                .append("&gameId=wd")
                .append("&css=http%3A%2F%2Fmicrogame.5054399.net%2Fv2%2Fresource%2FcssSdk%2Fdefault%2Flogin.css")
                .append("&redirectUrl=")
                .append("&sessionId=").append(captchaID != null ? captchaID : "")
                .append("&mainDivId=popup_login_div")
                .append("&includeFcmInfo=false")
                .append("&level=8")
                .append("&regLevel=8")
                .append("&userNameLabel=4399%E7%94%A8%E6%88%B7%E5%90%8D")
                .append("&userNameTip=%E8%AF%B7%E8%BE%93%E5%85%A54399%E7%94%A8%E6%88%B7%E5%90%8D")
                .append("&welcomeTip=%E6%AC%A2%E8%BF%8E%E5%9B%9E%E5%88%B04399")
                .append("&sec=1")
                .append("&password=").append(encodeValue(password))
                .append("&username=").append(encodeValue(username))
                .append("&inputCaptcha=").append(captcha != null ? encodeValue(captcha) : "")
                .toString();
    }




}
