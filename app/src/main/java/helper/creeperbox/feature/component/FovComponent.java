package helper.creeperbox.feature.component;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.Render2DEvent;
import helper.creeperbox.utils.render.animation.Animation;
import helper.creeperbox.utils.render.animation.Easing;

public class FovComponent {

    private Animation animation = new Animation(Easing.Decelerate, 200);

    private static boolean toggle;


    public static void setToggle(boolean toggle) {
        FovComponent.toggle = toggle;
    }

    @SubscribeEvent
    public void onTick(Render2DEvent event) {

        animation.run(toggle?1f:0f);
        if(animation.isFinished()) return;
        float start = 90f;
        float end = 30f;
        float value = start+(end-start)*animation.getValue();
        CreeperBox.INSTANCE.runPython("import mod.client.extraClientApi as clientApi\n" +
                "comp = clientApi.GetEngineCompFactory().CreateCamera(clientApi.GetLevelId())\n" +
                "comp.SetFov("+value+")");
    }

}
