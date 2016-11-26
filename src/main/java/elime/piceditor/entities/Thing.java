package elime.piceditor.entities;

/**
 * Created by Elime on 15-08-16.
 */
public class Thing {
    private Pic pic;
    private Sprite[] sprites;
    private int width;
    private int height;
    private PicColor bgColor;

    public Thing(Pic pic) {
        this.pic = pic;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public void setSprites(Sprite[] sprites) {
        if (this.sprites != null) {
            //We need to make sure the pic byte size is correct when we replace sprites
            pic.setNumberOfBytes(pic.getNumberOfBytes() - getSpriteDataByteSize());
            this.sprites = sprites;
            pic.setNumberOfBytes(pic.getNumberOfBytes() + getSpriteDataByteSize());
        } else {
            this.sprites = sprites;
        }
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public PicColor getBgColor() {
        return bgColor;
    }

    public void setBgColor(PicColor bgColor) {
        this.bgColor = bgColor;
    }

    public int getSpriteDataByteSize() {
        int nBytes = 0;
        for (Sprite sprite : sprites) {
            nBytes += sprite.getPixelData().length + Integer.BYTES + Short.BYTES;
        }
        return nBytes;
    }

    public int getPixelWidth() {
        return width * Sprite.DEFAULT_W_H;
    }

    public int getPixelHeight() {
        return height * Sprite.DEFAULT_W_H;
    }

    public Pic getPic() {
        return pic;
    }

}
