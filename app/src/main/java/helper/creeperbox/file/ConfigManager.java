package helper.creeperbox.file;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import helper.creeperbox.clickgui.ClickGUIRenderer;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.settings.BasicValue;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.MarginValue;
import helper.creeperbox.feature.settings.NumberValue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {


    public static final File CONFIG_FILE = new File(CreeperBox.INSTANCE.context.getDataDir(),"configs");

    public static final File DEFAULT_FILE = new File(CONFIG_FILE,"config.json");


    protected static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    public ConfigManager()  {
        if(!CONFIG_FILE.exists()){
            CONFIG_FILE.mkdirs();
        }

        if(!DEFAULT_FILE.exists()){
            try {
                DEFAULT_FILE.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

//        load();
    }

    private void save(String name,boolean avoid){
        File file = new File(CONFIG_FILE,name);
        write(file);
    }


    public void save(String name){
        save(name,false);
    }


    public void save(){
        write(DEFAULT_FILE);
    }

    public void load(){
        read(DEFAULT_FILE);
    }

    private boolean read(File file) {

        if(!file.exists()){
            return false;
        }

        JsonObject configJsonObject = null;
        try {
            final FileReader fileReader = new FileReader(file);
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            configJsonObject = GSON.fromJson(bufferedReader, JsonObject.class);
            bufferedReader.close();
            fileReader.close();
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }

        if(configJsonObject == null) return false;

        for(Module module : CreeperBox.INSTANCE.getModuleManager().getRegisteredModule()) {

            if (!configJsonObject.has(module.getName())) {
                continue;
            }

            JsonObject moduleJsonObject = configJsonObject.getAsJsonObject(module.getName());
            if (moduleJsonObject.has("state")) {
                final boolean state = moduleJsonObject.get("state").getAsBoolean();
                module.setEnable(state);
            }else{
                module.setEnable(false);
            }

            if (moduleJsonObject.has("keybind")) {
                final boolean state = moduleJsonObject.get("keybind").getAsBoolean();
                module.setKeyBindingToggle(state);
            }

            if (moduleJsonObject.has("keybindX") && module.getBtn() != null) {
                final float x = moduleJsonObject.get("keybindX").getAsFloat();
                module.getBtn().setX(x);
            }

            if (moduleJsonObject.has("keybindY") && module.getBtn() != null) {
                final float y = moduleJsonObject.get("keybindY").getAsFloat();
                module.getBtn().setY(y);
            }

            Map<String,Integer> index = new HashMap<>();

            for(BasicValue value :module.getSettings()){
                JsonObject valueJsonObject;
                if(index.containsKey(value.getName())){
                    if (!moduleJsonObject.has(value.getName()+"$"+index.get(value.getName()))) {
                        continue;
                    }
                    valueJsonObject = moduleJsonObject.getAsJsonObject(value.getName()+"$"+index.get(value.getName()));
                    index.put(value.getName(),index.get(value.getName())+1);
                }else{
                    if (!moduleJsonObject.has(value.getName())) {
                        continue;
                    }
                    index.put(value.getName(),0);
                    valueJsonObject = moduleJsonObject.getAsJsonObject(value.getName());
                }
                if(value instanceof BooleanValue){
                    value.setValue(valueJsonObject.get("value").getAsBoolean());
                }
                if(value instanceof ListValue){
                    value.setValue(valueJsonObject.get("value").getAsString());
                }
                if(value instanceof NumberValue){
                    ((NumberValue) value).forceSet(valueJsonObject.get("value").getAsNumber());
                }
            }
        }


        return true;
    }

    private boolean write(File file) {

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        final JsonObject configJsonObject = new JsonObject();

        for(Module module : CreeperBox.INSTANCE.getModuleManager().getRegisteredModule()){

            JsonObject moduleJsonObject = new JsonObject();
            moduleJsonObject.addProperty("state",module.isEnable());
            moduleJsonObject.addProperty("keybind",module.isKeyBindingToggle);
            if (module.getBtn() != null) {
                moduleJsonObject.addProperty("keybindX",module.getBtn().getX());
                moduleJsonObject.addProperty("keybindY",module.getBtn().getY());
            }

            Map<String,Integer> index = new HashMap<>();

            for(BasicValue value : module.getSettings()){
                final JsonObject valueJsonObject = new JsonObject();

                if(value instanceof BooleanValue){
                    valueJsonObject.addProperty("value",((BooleanValue) value).getCurrentValue());
                }
                if(value instanceof ListValue){
                    valueJsonObject.addProperty("value",((ListValue) value).getCurrentValue());
                }
                if(value instanceof NumberValue){
                    valueJsonObject.addProperty("value",((NumberValue) value).getCurrentValue());
                }

                if(!(value instanceof MarginValue)){
                    if(index.containsKey(value.getName())){
                        moduleJsonObject.add(value.getName()+"$"+index.get(value.getName()),valueJsonObject);
                        index.put(value.getName(),index.get(value.getName())+1);
                    }else{
                        index.put(value.getName(),0);
                        moduleJsonObject.add(value.getName(),valueJsonObject);
                    }
                }
            }

            configJsonObject.add(module.getName(),moduleJsonObject);
        }



        final FileWriter fileWriter;
        try {
            fileWriter = new FileWriter(file);
            final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            GSON.toJson(configJsonObject, bufferedWriter);
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

}
