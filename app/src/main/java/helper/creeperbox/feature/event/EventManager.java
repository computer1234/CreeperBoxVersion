package helper.creeperbox.feature.event;



import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import helper.creeperbox.bp;
import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.component.BlinkComponent;
import helper.creeperbox.feature.component.CommandBlockComponent;
import helper.creeperbox.feature.component.FovComponent;
import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.component.HytComponent;
import helper.creeperbox.feature.component.InjectComponent;
import helper.creeperbox.feature.component.InventoryComponent;
import helper.creeperbox.feature.component.PythonCallerComponent;
import helper.creeperbox.feature.component.RenderHelperComponent;
import helper.creeperbox.feature.component.RotationComponent;
import helper.creeperbox.feature.module.Module;

public class EventManager {

    public final HashMap<Class<?>, CopyOnWriteArrayList<EventHandler>> events = new HashMap<>();

    public List<EventHandler> cacheHandler = new ArrayList<>();


    public EventManager(){
        registerDefault();
    }


    private void registerDefault(){
        if(CreeperBox.INSTANCE.debug){
            return;
        }

        register(new GameDataComponent());
        register(new InventoryComponent());
        register(new RotationComponent());
        register(new PythonCallerComponent());
        register(new FovComponent());
        register(new HytComponent());
        register(new RenderHelperComponent());
        register(new BlinkComponent());
        register(new InjectComponent());
        register(new CommandBlockComponent());
        register(new bp());

    }

    public synchronized void unregister(Object obj){
        Class<?> clazz = obj.getClass();
        events.remove(clazz);
        sortHandler();
    }

    public void register(Object obj){
        Class<?> clazz = obj.getClass();
        for(Method method : clazz.getDeclaredMethods()){
            if(method.getParameterTypes().length == 1 && method.isAnnotationPresent(SubscribeEvent.class)){
                if(Event.class.isAssignableFrom(method.getParameterTypes()[0])){
                    SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);
                    EventHandler handler = new EventHandler(method,(Class<? extends Event>)method.getParameterTypes()[0],obj,annotation.value());
                    if(events.containsKey(clazz)){
                        if(!events.get(clazz).contains(handler)){
                            events.get(clazz).add(handler);
                        }
                    }else{
                        events.put(clazz,new CopyOnWriteArrayList<>(Collections.singletonList(handler)));
                    }
                }
            }
        }
        sortHandler();
    }


    private void sortHandler(){
        cacheHandler.clear();
        events.values().forEach(list->{
            cacheHandler.addAll(list);
        });

        cacheHandler.sort(Comparator.comparingInt(EventHandler::getPriority));
    }

    public synchronized void callEvent(Event event){
        cacheHandler.forEach(handler->{
            if(handler.getEvent().equals(event.getClass())){
                Method method = handler.getMethod();
                Object owner = handler.getOwner();
                method.setAccessible(true);

                if(owner instanceof Module && !((Module) owner).isEnable()){
                     return;
                }
                try {
                    method.invoke(owner,event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
