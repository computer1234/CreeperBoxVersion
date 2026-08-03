package helper.creeperbox.feature.component;

import java.util.ArrayList;

import helper.creeperbox.clients.CreeperBox;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.ClientTickEvent;
import helper.creeperbox.feature.event.events.TickEvent;

public class PythonCallerComponent {
    private static ArrayList<String> cmd = new ArrayList<>();
    private static ArrayList<String> clientCMD = new ArrayList<>();

    @SubscribeEvent
    public void onTick(TickEvent event){
        for(String s : cmd){
            CreeperBox.INSTANCE.runPython(s);
        }
        cmd.clear();
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent event){
        for(String s : clientCMD){
            CreeperBox.INSTANCE.runPython(s);
        }
        clientCMD.clear();
    }

    public static void addClientQueue(String s){
        clientCMD.add(s);
    }

    public static void addQueue(String s){
        cmd.add(s);
    }

}
