package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;

public class LoginEvent extends Event {
    public String loginData;


    public LoginEvent(String loginData){
        this.loginData = loginData;
    }
}
