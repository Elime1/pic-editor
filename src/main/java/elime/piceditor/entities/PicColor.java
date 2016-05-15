package elime.piceditor.entities;

/**
 * Created by Elime on 15-08-17.
 */
public class PicColor {

    public byte r;
    public byte g;
    public byte b;

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
}
