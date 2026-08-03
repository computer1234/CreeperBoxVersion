package helper.creeperbox.utils.mc;
import java.util.ArrayList;

import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.block.Material;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.AxisAlignedBB;
import helper.creeperbox.sdk.math.Vec3i;

public class PlayerUtil {

    public static ArrayList<Block> getCollidingBoundingBoxes(EntityLocalPlayer player, AxisAlignedBB aabb){
        ArrayList<Block> array = new ArrayList<>();
        int minX = (int) Math.floor(aabb.minX);
        int maxX = (int) Math.floor(aabb.maxX + 1.0);
        int minY = (int) Math.floor(aabb.minY);
        int maxY = (int) Math.floor(aabb.maxY + 1.0);
        int minZ = (int) Math.floor(aabb.minZ);
        int maxZ = (int) Math.floor(aabb.maxZ + 1.0);
        for(int x = minX; x<maxX; x++){
            for(int y = minY; y<maxY; y++){
                for(int z = minZ; z<maxZ; z++){
                    Block block = player.getLevel().getBlock(new Vec3i(x,y,z));
                    Material material = player.getLevel().getMaterial(new Vec3i(x,y,z));
                    if(!material.isAir() && block.getCollisionShape().intersectsWith(aabb)){
                        array.add(block);
                    }
                }
            }
        }
        return array;
    }

}
