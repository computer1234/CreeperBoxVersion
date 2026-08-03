package helper.creeperbox.feature.module.modules.combat;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import helper.creeperbox.feature.component.GameDataComponent;
import helper.creeperbox.feature.component.RotationComponent;
import helper.creeperbox.feature.event.SubscribeEvent;
import helper.creeperbox.feature.event.events.KeyEvent;
import helper.creeperbox.feature.event.events.TickEvent;
import helper.creeperbox.feature.module.Category;
import helper.creeperbox.feature.module.Module;
import helper.creeperbox.feature.module.ModuleInfo;
import helper.creeperbox.feature.module.modules.build.Scaffold;
import helper.creeperbox.feature.settings.BooleanValue;
import helper.creeperbox.feature.settings.ListValue;
import helper.creeperbox.feature.settings.MarginValue;
import helper.creeperbox.feature.settings.NumberValue;
import helper.creeperbox.sdk.entity.Attributes;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityItem;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.network.ItemDef;
import helper.creeperbox.sdk.network.packet.PyRpcPacket;
import helper.creeperbox.utils.mc.ItemUtil;
import helper.creeperbox.utils.mc.RotationUtil;
import helper.creeperbox.utils.mc.StopWatch;
import helper.creeperbox.utils.mc.TargetUtil;

@ModuleInfo(name = "杀戮光环", category = Category.Combat,key = KeyEvent.KEYCODE_R)
public class KillAura extends Module {

    private final NumberValue cps = new NumberValue("频率", this,10,1, 100, 1);
    private final NumberValue multiple = new NumberValue("倍数", this,1,1, 100, 1);
    private final NumberValue range = new NumberValue("范围", this,3,1, 20, 0.1);

    private final ListValue mode = new ListValue("模式",this,"轮换")
            .addSubList("单体")
            .addSubList("群体")
            .addSubList("轮换");

    private final MarginValue margin1 = new MarginValue(this);

    private final ListValue priority = new ListValue("攻击优先级",this,"血量最低")
            .addSubList("血量最低")
            .addSubList("装备等级")
            .addSubList("距离最近");


    private final MarginValue margin2 = new MarginValue(this);

    private final BooleanValue throughWall = new BooleanValue("隔墙不打",this,false);

    private final BooleanValue scaffold = new BooleanValue("铺路不打",this,false);
    private final BooleanValue moveFix = new BooleanValue("移动修复",this,false);


    private List<EntityActor> targets;

    private static double distance(EntityActor a1, EntityActor a2){
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }


    private float getTotalArmor(EntityActor actor){
        float level = 0;
        for(int i = 0 ; i < 4 ; i ++){
            ItemData data = actor.getArmor(i).getItemData();
            if(data.getDefinition() instanceof ItemDef){
                level+= ItemUtil.getLevel(data,(ItemDef) data.getDefinition());
            }
        }
        return level;
    }

    private StopWatch switchChangeTicks = new StopWatch();

    private EntityActor currentTarget;
    private StopWatch attackTicks = new StopWatch();

    private void findEntity(EntityLocalPlayer player) {

        targets = new ArrayList<>();

        boolean through = throughWall.getCurrentValue().booleanValue();

        for (EntityActor en : TargetUtil.findTarget(player,range.getCurrentValue().floatValue())){
            if(!through || player.canSeeActor(en)) targets.add(en);
        }

        if(targets.isEmpty()){
            return;
        }
//
//        switch(priority.getCurrentValue()){
//            case "装备等级":
//                targets.sort(Comparator.comparingDouble(this::getTotalArmor));
//                break;
//            case "距离最近":
//                targets.sort(Comparator.comparingDouble(en->distance(player,en)));
//                break;
//            default:
//                targets.sort(Comparator.comparingDouble(en->en.getAttributeCurrentValue(Attributes.HEALTH)));
//                break;
//        }

        switch(mode.getCurrentValue()){
            case "单体":
                currentTarget = targets.get(0);
                targets = new ArrayList<>();
                targets.add(currentTarget);
                break;
            case "轮换":
                if(!switchChangeTicks.finished(100)){
                    targets = new ArrayList<>();
                    targets.add(currentTarget);
                    break;
                }
                if(targets.contains(currentTarget)) {
                    int index = targets.indexOf(currentTarget)+1;
                    if (index > 0 && index < targets.size()){
                        currentTarget = targets.get(index);
                    }else{
                        currentTarget = targets.get(0);
                    }
                }else{
                    currentTarget = targets.get(0);
                }
                targets = new ArrayList<>();
                targets.add(currentTarget);
                switchChangeTicks.reset();
                break;
            case "群体":
            default:
                break;
        }

    }

    private void attackEntity(EntityLocalPlayer player) {
        if(targets.isEmpty()){
            return;
        }

        int cps = multiple.getCurrentValue().intValue()*this.cps.getCurrentValue().intValue();
        double preTick = 1000d / cps;

        if(attackTicks.finished((long) preTick)){
            int count = cps>20?(int) (attackTicks.getElapsedTime() / preTick):1;
            attackAll(player,count);
            attackTicks.reset();
        }
    }

    public static byte[] hexToByteArray(String hex) {
        int length = hex.length();
        byte[] byteArray = new byte[length / 2];

        for(int i = 0; i < length; i += 2) {
            String subStr = hex.substring(i, i + 2);
            byteArray[i / 2] = (byte)Integer.parseInt(subStr, 16);
        }

        return byteArray;
    }

    private void attackAll(EntityLocalPlayer player,int count){

        if (GameDataComponent.movementServerAuthoritative) {
            PyRpcPacket packet = new PyRpcPacket();
            packet.setData(hexToByteArray("93c40b4d6f644576656e7443325394c41145434e756b6b6974436c69656e744d6f64c41445434e756b6b6974436c69656e7453797374656dc419496e7465726163744265666f7265436c69656e744576656e7481c40474797065c403544150c0"));
            packet.setMsgId(98247598);
            player.sendPacketNoEvent(packet);
        }

        for(int i = 0 ; i < count ; i ++){
            for(EntityActor en : targets){
                player.attack(en);
                player.swing();
            }
        }
    }

    public StopWatch attackWatch = new StopWatch();

    @SubscribeEvent
    public void onTick(TickEvent event){

        if(scaffold.getCurrentValue() && Scaffold.getINSTANCE().isEnable()){
            return;
        }


        EntityLocalPlayer player = event.getPlayer();

        if(!attackWatch.finished(400)) return;

        findEntity(player);

        if(targets.isEmpty()){
            return;
        }

        doRotation(player);
        attackEntity(player);
    }

    private void doRotation(EntityLocalPlayer player) {
        float rotationSpeed = 180f;
        if(targets.isEmpty()){
            return;
        }
        Vec2f rot = RotationUtil.calcRotation(player,targets.get(0).getPos());
        RotationComponent.setRotation(player,rot,rotationSpeed,moveFix.getCurrentValue());
    }

    @Override
    public void onEnable() {
        attackTicks.reset();
        switchChangeTicks.reset();
        attackWatch.reset();
    }

    @Override
    public String getTag() {
        switch(mode.getCurrentValue()) {
            case "单体":
                return "Single";
            case "群体":
                return "Multi";
            default:
                return "Switch";
        }
    }
}


