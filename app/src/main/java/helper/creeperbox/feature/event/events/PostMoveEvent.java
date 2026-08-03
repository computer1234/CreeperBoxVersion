package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

public class PostMoveEvent extends Event {

    private EntityLocalPlayer player;

    public PostMoveEvent(EntityLocalPlayer player){
        this.player = player;
    }

    public EntityLocalPlayer getPlayer() {
        return player;
    }

}
