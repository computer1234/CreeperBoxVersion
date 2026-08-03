package helper.creeperbox.utils.mc;
import helper.creeperbox.sdk.block.EnumFacing;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.math.Vec2f;
import helper.creeperbox.sdk.math.Vec3f;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.utils.math.MathUtil;

public class RotationUtil {
    public static Vec3i getFacing(EntityLocalPlayer player){
        float yaw = MathUtil.wrapAngleTo180_float(player.getRotation().y)+180f;
        if(yaw>325 || yaw<35){
            return EnumFacing.NORTH.getValue();
        }
        if (yaw<55) {
            return new Vec3i(1,0,-1);
        }
        if(yaw < 125){
            return EnumFacing.EAST.getValue();
        }

        if(yaw < 145){
            return new Vec3i(1,0,1);
        }

        if(yaw<215){
            return EnumFacing.SOUTH.getValue();
        }

        if(yaw<235){
            return new Vec3i(-1,0,1);
        }

        if(yaw<305){
            return EnumFacing.WEST.getValue();
        }
        return new Vec3i(-1,0,-1);
    }



    public static Vec2f calcRotation(EntityLocalPlayer player, Vec3f pos){
        Vec3f playerPos = player.getPos();
        final double xSize = pos.x - playerPos.x;
        final double ySize = pos.y - playerPos.y;
        final double zSize = pos.z - playerPos.z;
        final double theta = Math.sqrt(xSize * xSize + zSize * zSize);
        final float yaw = (float) (Math.atan2(zSize, xSize) * 180 / Math.PI) - 90;
        final float pitch = (float) (-(Math.atan2(ySize, theta) * 180 / Math.PI));
        return new Vec2f(pitch,yaw);
    }


    public static Vec2f calcRotationDelta(EntityLocalPlayer player, Vec3f pos){
        Vec2f dest = calcRotation(player,pos);
        Vec2f current = player.getRotation();
        return new Vec2f(current.x-dest.x,dest.y-current.y);
    }

    public static float smoothRotation(float from, float to, float speed) {
        float f = MathUtil.wrapAngleTo180_float(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

}
