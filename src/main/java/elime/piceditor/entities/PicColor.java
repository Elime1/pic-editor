package elime.piceditor.entities;

import lombok.Getter;

/**
 * Created by Elime on 15-08-17.
 */

@Getter
public class PicColor {

    private byte r;
    private byte g;
    private byte b;

    public PicColor(byte r, byte g, byte b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public String toRGBString() {
        return new StringBuilder("rgb(")
                .append(r & 0xFF).append(",")
                .append(g & 0xFF).append(",")
                .append(b & 0xFF).append(")")
                .toString();
    }

    public String toHexString() {
        return String.format("#%06X", (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
    }

    @Override
    public String toString() {
        return toRGBString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PicColor c = (PicColor) o;
        return  r == c.r && g == c.g && b == c.b;
    }

}
