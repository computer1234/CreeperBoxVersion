package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;

public class LoginFinishEvent extends Event {

    public long randomIV;

    public LoginFinishEvent(long randomIV){
        this.randomIV = randomIV;
    }


}
