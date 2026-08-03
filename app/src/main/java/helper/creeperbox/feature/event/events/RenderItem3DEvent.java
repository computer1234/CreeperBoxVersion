package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.entity.type.EntityItem;

public class RenderItem3DEvent extends Event {
    public float[] matrix;

    private EntityItem item;

    private float partialTicks;

    public EntityItem getItem() {
        return item;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public RenderItem3DEvent(float[] matrix, EntityItem item, float partialTicks) {
        this.matrix = matrix;
        this.item = item;
        this.partialTicks = partialTicks;
    }
}
