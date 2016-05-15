package elime.piceditor.entities;

/**
 * Created by Elime on 15-08-16.
 */
public class Pic {
    private Thing[] things;
    private int signature;
    private int numberOfImages;
    private int numberOfBytes;

    public Thing[] getThings() {
        return things;
    }

    public void setThings(Thing[] things) {
        this.things = things;
    }

    public int getSignature() {
        return signature;
    }

    public void setSignature(int signature) {
        this.signature = signature;
    }

    public int getNumberOfImages() {
        return numberOfImages;
    }

    public void setNumberOfImages(int numberOfImages) {
        this.numberOfImages = numberOfImages;
    }

    public int getNumberOfBytes() {
        return numberOfBytes;
    }

    public void setNumberOfBytes(int numberOfBytes) {
        this.numberOfBytes = numberOfBytes;
    }


}
