package helper.creeperbox.sdk.network;

import com.google.gson.GsonBuilder;
import helper.creeperbox.hook.HopeHookEntrance;
import helper.creeperbox.utils.ResourceUtil;

import java.io.InputStreamReader;
import java.util.HashMap;

public class ItemDataMap {
    public static HashMap<String,String> breakToolMap = new HashMap<>();

    public static HashMap<String,Number> itemSizeMap = new HashMap<>();

    public static int getMaxStackSize(String id){
        return ItemDataMap.itemSizeMap.getOrDefault(id,64).intValue();
    }

    public static String getBreakTool(String id){
        return ItemDataMap.breakToolMap.getOrDefault(id,"none");
    }


    static {
//        try(InputStreamReader reader = new InputStreamReader(HopeHookEntrance.class.getClassLoader().getResourceAsStream("assets/network/blocktool.json"))){
//            breakToolMap = new GsonBuilder().create().fromJson(reader,HashMap.class);
//        }catch(Exception ex){
//            ex.printStackTrace();
//        }

        try(InputStreamReader reader = new InputStreamReader(ResourceUtil.decryptStream(HopeHookEntrance.class.getClassLoader().getResourceAsStream("assets/encrypt/d.vmp")))){
            itemSizeMap = new GsonBuilder().create().fromJson(reader,HashMap.class);
        }catch(Exception ex){
            ex.printStackTrace();
        }


    }



}
