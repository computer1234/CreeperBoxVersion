package helper.creeperbox.feature.event.events;

import helper.creeperbox.feature.event.Event;
import helper.creeperbox.sdk.block.Block;
import helper.creeperbox.sdk.entity.type.EntityLocalPlayer;

public class BlockDestroyEvent extends Event {
    private Block block;
    private EntityLocalPlayer player;
    private float progress;

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public EntityLocalPlayer getPlayer() {
        return player;
    }

    public void setPlayer(EntityLocalPlayer player) {
        this.player = player;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public BlockDestroyEvent(Block block, EntityLocalPlayer player, float progress) {
        this.block = block;
        this.player = player;
        this.progress = progress;
    }
}
