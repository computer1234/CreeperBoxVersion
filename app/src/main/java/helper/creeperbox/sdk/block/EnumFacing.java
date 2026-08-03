package helper.creeperbox.sdk.block;

import helper.creeperbox.sdk.math.Vec3i;

public enum EnumFacing {
    DOWN(1, -1, "down", new Vec3i(0, -1, 0)),
    UP( 0, -1, "up",  new Vec3i(0, 1, 0)),
    NORTH(3, 2, "north", new Vec3i(0, 0, -1)),
    SOUTH(2, 0, "south", new Vec3i(0, 0, 1)),
    WEST(5, 1, "west", new Vec3i(-1, 0, 0)),
    EAST(4, 3, "east", new Vec3i(1, 0, 0));

    private int oppositeIndex;
    public final int index;
    private String name;
    private Vec3i value;

    public static final EnumFacing[] VALUES = new EnumFacing[6];

    public static final EnumFacing[] HORIZONTALS = new EnumFacing[4];

    private static int horizontalsIndex;

    static {
        horizontalsIndex = 0;
        for (EnumFacing enumfacing : values())
        {
            VALUES[enumfacing.index] = enumfacing;

            if(enumfacing.value.y == 0){
                HORIZONTALS[horizontalsIndex] = enumfacing;
                horizontalsIndex++;
            }
        }

    }



    EnumFacing(int index, int oppositeIndex, String name, Vec3i value){
        this.oppositeIndex = oppositeIndex;
        this.index = index;
        this.name = name;
        this.value = value;
    }

    public EnumFacing getOpposite() {
        return VALUES[oppositeIndex];
    }

    public Vec3i getValue() {
        return value;
    }
}
