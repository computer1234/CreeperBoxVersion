package helper.creeperbox.sdk.network.packet.modify;

import org.cloudburstmc.protocol.bedrock.packet.TextPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class NeteaseTextPacket extends TextPacket {

    @Override
    public NeteaseTextPacket clone() {
        return (NeteaseTextPacket) super.clone();
    }

    private boolean hasExtra;

    private List<String> params = new ArrayList<>();
    private String author;

    private String filteredMessage = "";


    public String getFilteredMessage() {
        return filteredMessage;
    }

    public void setFilteredMessage(String filteredMessage) {
        this.filteredMessage = filteredMessage;
    }


    public List<String> getParams() {
        return params;
    }

    public String getAuthor() {
        return author;
    }


    public boolean isHasExtra() {
        return hasExtra;
    }

    public void setHasExtra(boolean hasExtra) {
        this.hasExtra = hasExtra;
    }


    public void setAuthor(String author) {
        this.author = author;
    }


    public void setParams(List<String> params) {
        this.params = params;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        NeteaseTextPacket that = (NeteaseTextPacket) o;
        return hasExtra == that.hasExtra && Objects.equals(params, that.params) && Objects.equals(author, that.author);
    }


    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), hasExtra, params, author);
    }




}
