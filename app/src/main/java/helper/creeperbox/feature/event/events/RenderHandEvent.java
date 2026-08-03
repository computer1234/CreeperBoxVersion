package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;

public class RenderHandEvent extends Event {

    public float[] matrix;

    public RenderHandEvent(float[] matrix){
        this.matrix = matrix;
    }

}
