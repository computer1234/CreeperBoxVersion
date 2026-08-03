package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec3f;

public class PlayerRotEvent extends Event {

    private EntityLocalPlayer player;
    public Vec3f rot;

    public PlayerRotEvent(EntityLocalPlayer player, Vec3f rot) {
        this.player = player;
        this.rot = rot;
    }

    public EntityLocalPlayer getPlayer() {
        return player;
    }

}
