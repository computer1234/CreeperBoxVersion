package helper.creeperbox.utils.mc;

import org.cloudburstmc.protocol.bedrock.data.inventory.ItemData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import helper.creeperbox.feature.module.modules.combat.AntiBot;
import helper.creeperbox.feature.module.modules.combat.Target;
import helper.creeperbox.feature.module.modules.combat.Team;
import helper.creeperbox.sdk.entity.type.EntityActor;
import helper.creeperbox.sdk.entity.type.EntityItem;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.entity.type.EntityPlayer;
import helper.creeperbox.sdk.entity.type.EntityServerPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;

public class TargetUtil {
    public static List<EntityActor> findTarget(EntityLocalPlayer player, float distance){
        List<EntityActor> targets = new ArrayList<>();

        boolean armorColor = Team.getINSTANCE().isEnable();
        boolean bmwSw = false;

        boolean bwmAntiBot = true;

        int color = -1;
        if(armorColor){
            ItemData armor = player.getArmor(0).getItemData();
            if(armor.getTag() != null){
                color = armor.getTag().getInt("customColor",-1);
            }
        }

        boolean onlyPlayer = Target.getINSTANCE().target.getCurrentValue().equals("玩家");

        for (EntityActor en : player.getLevel().getRuntimeActorList()) {

            if(en instanceof EntityLocalPlayer) continue;
            if(en instanceof EntityItem) continue;
            if(!en.isAlive()) continue;
            if(onlyPlayer && !(en instanceof EntityPlayer)){
                continue;
            }

            if(AntiBot.getINSTANCE().isEnable()){
                if(en.getArmor(1).getItemData()==ItemData.AIR){
                    continue;
                }
            }

            if (distance(player, en) < distance){

                if(bwmAntiBot){
                    Vec2f rot = en.getRotation();
                    if(rot.x == 0 && rot.y == 0) continue;
                }

                if(bmwSw){
                    if(en instanceof EntityPlayer && en.getNameTag().startsWith("§")){
                        continue;
                    }
                }

                if(armorColor){
                    if(en instanceof EntityPlayer){
                        ItemData enArmor = en.getArmor(0).getItemData();
                        if(color!= -1 && enArmor.getTag() != null && enArmor.getTag().getInt("customColor",-1)==color){
                            continue;
                        }
                    }
                }
                targets.add(en);
            }
        }
        return targets;
    }

    private static double distance(EntityActor a1, EntityActor a2){
        Vec3f pos1 = a1.getPos();
        Vec3f pos2 = a2.getPos();
        float dx = pos1.x - pos2.x;
        float dy = pos1.y - pos2.y;
        float dz = pos1.z - pos2.z;
        return Math.sqrt(dx*dx+dy*dy+dz*dz);
    }


    public static EntityActor findNearest(EntityLocalPlayer player,float distance){
        List<EntityActor> targets = findTarget(player,distance);
        if(targets.isEmpty()) return null;
        targets.sort(Comparator.comparingDouble(en->distance(player,en)));
        return targets.get(0);
    }


    public static List<EntityActor> findTargetXZ(EntityLocalPlayer player, float distance){
        List<EntityActor> targets = new ArrayList<>();


        boolean armorColor = true;
        boolean bmwSw = false;
        boolean bwmAntiBot = false;


        int color = -1;
        if(armorColor){
            ItemData armor = player.getArmor(0).getItemData();
            if(armor.getTag() != null){
                color = armor.getTag().getInt("customColor",-1);
            }
        }


        for (EntityActor en : player.getLevel().getPlayers()) {
            if(!(en instanceof EntityServerPlayer) ) {
                continue;
            }

            if (distanceTo(player.getPos(), en.getPos()) < distance){


                if(bwmAntiBot){
                    Vec2f rot = en.getRotation();
                    if(rot.x == 0 && rot.y == 0) continue;
                }

                if(bmwSw){
                    if(en instanceof EntityPlayer && en.getNameTag().startsWith("§")){
                        continue;
                    }
                }

                if(armorColor){
                    if(en instanceof EntityPlayer){
                        ItemData enArmor = en.getArmor(0).getItemData();
                        if(color!= -1 && enArmor.getTag() != null && enArmor.getTag().getInt("customColor",-1)==color){
                            continue;
                        }
                    }
                }
                targets.add(en);
            }
        }
        return targets;
    }
    public static float distanceTo(Vec3f thiz,Vec3f other) {
        return (float) Math.sqrt(Math.pow(thiz.x - other.x, 2) + Math.pow(thiz.z - other.z, 2));
    }

}
