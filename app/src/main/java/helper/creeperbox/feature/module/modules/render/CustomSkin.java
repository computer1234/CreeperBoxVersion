package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.settings.ListValue;


@ModuleInfo(name = "模型修改器", category = Category.Render)
public class CustomSkin extends Module {

    public final ListValue mode = new ListValue("模型种类",this,"丛雨")
            .addSubList("丛雨")
            .addSubList("白狐")
            .addSubList("亚托莉");
    private void setSkin(String geometry,String texture){
        PythonCallerComponent.addQueue(String.format("import mod.client.extraClientApi as clientApi\n" +
                "comp = clientApi.GetEngineCompFactory().CreateActorRender(clientApi.GetLocalPlayerId())\n" +
                "comp.RemovePlayerGeometry('default')\n" +
                "comp.AddPlayerGeometry('default', \"%s\")\n" +
                "comp.AddPlayerTexture('default', '%s')\n" +
                "comp.RebuildPlayerRender()",geometry,texture));
    }

    private void resetSkin(){
        PythonCallerComponent.addQueue("import mod.client.extraClientApi as clientApi\n" +
                "comp = clientApi.GetEngineCompFactory().CreateActorRender(clientApi.GetLocalPlayerId())\n" +
                "comp.RemovePlayerGeometry('default')\n" +
                "comp.AddPlayerGeometry('default', \"geometry.humanoid.custom\")\n" +
                "comp.RebuildPlayerRender()\n" +
                "clientApi.GetEngineCompFactory().CreateModel(clientApi.GetLocalPlayerId()).ResetSkin()\n");
    }


    @Override
    public void onEnable() {
        switch (mode.getCurrentValue()){
            case "丛雨":
                setSkin("geometry.94e8669ae721460cae461f2f48368c13","textures/entity/csm_sub/3a23a51112ad487dad43ed6b76b3405e/4b1a68de60334e3eb9649d557fb2b201");
                break;
            case "白狐":
                setSkin("geometry.csm.ef23f1c06ede4c10b076fc3b8a01c7e3.main","textures/entity/csm_sub/164d2046182c41eaabb1e7dae0bcb2a0/482c085d0e374a6993005f868869e7df");
                break;
            default:
                setSkin("geometry.06e14c201df24f809712e1aec36fe3cd","textures/entity/csm_sub/e563fbccf45e454fb02d535cf219ffb7/6bc25453b9b4419ab557a07605f52646");
                break;
        }
    }


    @Override
    public void onDisable() {
        resetSkin();
    }
}
