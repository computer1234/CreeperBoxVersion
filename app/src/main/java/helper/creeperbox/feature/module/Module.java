package helper.creeperbox.feature.module;


import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import helper.creeperbox.clickgui.KeyBindingButton;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.module.modules.render.Notification;
import helper.creeperbox.feature.settings.BasicValue;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;


import java.util.ArrayList;
import java.util.List;

public class Module {
    private String name;
    protected List<BasicValue> settings;
    protected boolean enable;
    public Animation animation = new Animation(Easing.Decelerate,1500);
    private Category category;
    public List<BasicValue> getSettings() {
        return settings;
    }
    public boolean isKeyBindingToggle;
    private int keyCode;
    private KeyBindingButton btn;
    public Module() {
        settings = new ArrayList<>();
        if (this.getClass().isAnnotationPresent(ModuleInfo.class)) {
            ModuleInfo info = this.getClass().getAnnotation(ModuleInfo.class);
            this.name = info.name();
            this.category = info.category();
            this.keyCode = info.key();
        } else {
            throw new RuntimeException("ModuleInfo annotation not found on " + this.getClass().getSimpleName());
        }

        isKeyBindingToggle = false;

        if (!CreeperBox.INSTANCE.debug) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                this.btn = new KeyBindingButton(name, false, view -> {
                    toggle();
                });
                btn.setOpen(true);
                btn.setOpen(false);
            }, 0);
        }
    }

    public KeyBindingButton getBtn() {
        return btn;
    }

    public void setKeyBindingToggle(boolean keyBindingToggle) {
        isKeyBindingToggle = keyBindingToggle;
        if (this.btn != null) {
            this.btn.setOpen(isKeyBindingToggle);
        }
    }

    public Category getCategory() {
        return category;
    }


    public String getName() {
        return name;
    }

    public final void toggle(){
        enable = !enable;
        if(enable){
            if(!CreeperBox.INSTANCE.debug && this.btn!=null){
                btn.setEnable(true);
            }
            onEnable();
            if(Notification.toggle && GameDataComponent.tick > 5) CreeperBox.INSTANCE.runPython(String.format("from gui_2d import GUI\n" +
                    "GUI.ui_mgr.show_toast(('%s 开启'), %s)",name,"True"));
        }else{
            if(!CreeperBox.INSTANCE.debug && this.btn!=null){
                btn.setEnable(false);
            }
            if(Notification.toggle && GameDataComponent.tick > 5) CreeperBox.INSTANCE.runPython(String.format("from gui_2d import GUI\n" +
                    "GUI.ui_mgr.show_toast(('%s 关闭'), %s)",name,"False"));
            onDisable();
        }
    }

    public void setEnable(boolean enabled){
        if(enable != enabled){
            toggle();
        }
    }

    public final void onKeyDown(int key){
        if(key == 0) return;
        if(key == keyCode){
            toggle();
        }
    }


    public String getTag(){
        return "";
    }

    public boolean hasTag(){
        return !getTag().isEmpty();
    }


    public void onEnable(){}

    public void onDisable(){}

    public boolean isEnable() {
        return enable;
    }
}
