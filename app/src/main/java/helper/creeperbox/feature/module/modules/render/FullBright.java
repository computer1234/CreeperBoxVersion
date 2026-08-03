package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.NumberValue;

@ModuleInfo(name = "修改发光", category = Category.Render)
public class FullBright extends Module {
    private final NumberValue gamma = new NumberValue("发光值", this, 16f, 1f, 16f, 0.1f);


    public FullBright(){
        gamma.setOnValueChange(value->{
            if(isEnable()) CreeperBox.setGamma(value.floatValue());
            return value;
        });
    }
    @Override
    public void onEnable() {
        CreeperBox.setGamma(gamma.getCurrentValue().floatValue());
    }

    @Override
    public void onDisable() {
        CreeperBox.setGamma(0f);
    }
}
