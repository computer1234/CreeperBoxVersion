package helper.creeperbox.feature.event;

import java.lang.reflect.Method;

public class EventHandler {
    private final Method method;
    private final Class<? extends Event> event;
    private final Object owner;

    private final int priority;

    public EventHandler(Method method, Class<? extends Event> event, Object owner,int priority) {
        this.method = method;
        this.event = event;
        this.owner = owner;
        this.priority = priority;
    }

    public Method getMethod() {
        return method;
    }

    public Class<? extends Event> getEvent() {
        return event;
    }

    public Object getOwner() {
        return owner;
    }

    public int getPriority() {
        return priority;
    }
}
