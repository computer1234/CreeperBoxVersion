package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;

@ModuleInfo(name = "分离摄像机", category = Category.Render)
public class DepartCamera extends Module {

    @Override
    public void onEnable() {
        PythonCallerComponent.addQueue("import mod.client.extraClientApi as clientApi\n" +
                "comp = clientApi.GetEngineCompFactory().CreateCamera(clientApi.GetLevelId())\n" +
                "comp.DepartCamera()");
    }

    @Override
    public void onDisable() {
        PythonCallerComponent.addQueue("import mod.client.extraClientApi as clientApi\n" +
                "comp = clientApi.GetEngineCompFactory().CreateCamera(clientApi.GetLevelId())\n" +
                "comp.UnDepartCamera()");
    }
}
