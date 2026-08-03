package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.math.Vec3f;

public class SetCameraEvent extends Event {
    public Vec3f cameraPos;

    public SetCameraEvent(Vec3f cameraPos){
        this.cameraPos = cameraPos;
    }
}
