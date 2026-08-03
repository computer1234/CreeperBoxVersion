package helper.creeperbox;

public class NeteaseManager {


    public static String encryptHttpContent(String content){
        return a(content);
    }


    public static native String a(String content);



    public static String encryptMessage(String message){
        return b(message);
    }

    public static native String b(String message);


    public static String decryptHttpResponse(String response){
        return a(response);
    }

    public static native String c(String response);

    public static void setLoginUid(String uid){
        d(uid);
    }

    public static native void d(String uid);





}
