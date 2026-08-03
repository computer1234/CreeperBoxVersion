package helper.creeperbox.feature.module.modules.render;

import helper.creeperbox.sdk.block.Material;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;
import helper.creeperbox.sdk.level.Level;
import helper.creeperbox.sdk.math.Vec3i;
import helper.creeperbox.utils.math.RandomUtil;

public class PhysicsParticles {
    public float x;
    public float y;
    public float z;
    public float px;
    public float py;
    public float pz;
    public float motionX;
    public float motionY;
    public float motionZ;
    public float rotationAngle;
    public float rotationSpeed;
    public float health;

    public long time;
    public int color;
    public final float starsScale = 10f;

    public PhysicsParticles(float x, float y, float z, int color, float rotationAngle, float rotationSpeed, float health) {
        this.x = x;
        this.y = y;
        this.z = z;
        px = x;
        py = y;
        pz = z;
        float speed = 2;
        motionX = (float) RandomUtil.getRandom(-(float) speed / 50f, (float) speed / 50f);
        motionY = (float) RandomUtil.getRandom(-(float) speed / 50f, (float) speed / 50f);
        motionZ = (float) RandomUtil.getRandom(-(float) speed / 50f, (float) speed / 50f);
        time = System.currentTimeMillis();
        this.color = color;
        this.rotationAngle = rotationAngle;
        this.rotationSpeed = rotationSpeed;
        this.health = health;
    }


    public boolean update(EntityLocalPlayer player, boolean fly, int fadeTime) {
        double sp = Math.sqrt(motionX * motionX + motionZ * motionZ);
        px = x;
        py = y;
        pz = z;

        x += motionX;
        y += motionY;
        z += motionZ;

        Level level = player.getLevel();
        if (posBlock(level,x, y - starsScale / 10f, z)) {
            motionY = -motionY / 1.1f;
            motionX = motionX / 1.1f;
            motionZ = motionZ / 1.1f;
        } else {
            if (posBlock(level,x - sp, y, z - sp)
                    || posBlock(level,x + sp, y, z + sp)
                    || posBlock(level,x + sp, y, z - sp)
                    || posBlock(level,x - sp, y, z + sp)
                    || posBlock(level,x + sp, y, z)
                    || posBlock(level,x - sp, y, z)
                    || posBlock(level,x, y, z + sp)
                    || posBlock(level,x, y, z - sp)
            ) {
                motionX = -motionX;
                motionZ = -motionZ;
            }
        }

        if (!fly)
            motionY -= 0.035f;

        motionX /= 1.005f;
        motionZ /= 1.005f;
        motionY /= 1.005f;

        return System.currentTimeMillis() - time > fadeTime;
    }

    private boolean posBlock(Level level, double x, double y, double z) {
        Material material = level.getMaterial(new Vec3i((int) Math.floor(x), (int) Math.floor(y), (int) Math.floor(z)));
        return !material.isAir() && !material.isLiquid();
    }



}
