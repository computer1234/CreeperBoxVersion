package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.packet.ResourcePackStackPacket;

import java.util.List;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class NeteaseResourcePackStackPacket extends ResourcePackStackPacket {

    @Override
    public NeteaseResourcePackStackPacket clone() {
        return (NeteaseResourcePackStackPacket) super.clone();
    }

    private boolean hasExtra;
    private final List<Entry> neteaseVipBehaviorPacks = new ObjectArrayList();
    private final List<Entry> neteaseVipResourcePacks = new ObjectArrayList();
    private boolean hasEditorPacks;

    public boolean isHasEditorPacks() {
        return hasEditorPacks;
    }

    public void setHasEditorPacks(boolean hasEditorPacks) {
        this.hasEditorPacks = hasEditorPacks;
    }

    public List<Entry> getNeteaseVipBehaviorPacks() {
        return neteaseVipBehaviorPacks;
    }

    public List<Entry> getNeteaseVipResourcePacks() {
        return neteaseVipResourcePacks;
    }

    public String toString() {
        return "NeteaseResourcePackStackPacket(forcedToAccept=" + this.isForcedToAccept() + ", behaviorPacks=" + this.getBehaviorPacks() + ", resourcePacks=" + this.getResourcePacks() + ", gameVersion=" + this.getGameVersion() + ", experiments=" + this.getExperiments() + ", experimentsPreviouslyToggled=" + this.isExperimentsPreviouslyToggled() + ", neteaseVipBehaviorPacks="+neteaseVipBehaviorPacks+", neteaseVipResourcePacks="+neteaseVipResourcePacks+")";
    }

    public boolean isHasExtra() {
        return hasExtra;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }
}
