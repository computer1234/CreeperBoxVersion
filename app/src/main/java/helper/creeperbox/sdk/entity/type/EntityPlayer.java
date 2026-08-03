package helper.creeperbox.sdk.entity.type;

import org.cloudburstmc.protocol.bedrock.data.skin.SerializedSkin;

public class EntityPlayer extends EntityActor{
    public EntityPlayer(long pointer) {
        super(pointer);
    }


    public SerializedSkin getSkin(){
        return ab();
    }
    public native SerializedSkin ab();
}
