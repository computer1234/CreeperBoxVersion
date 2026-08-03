package helper.creeperbox.sdk;

import java.util.Objects;

public class PointerHolder {
    protected long pointer;

    public long getCppPointer() {
        return pointer;
    }

    public PointerHolder(long pointer){
        this.pointer = pointer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PointerHolder that = (PointerHolder) o;
        return pointer == that.pointer;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointer);
    }
}
