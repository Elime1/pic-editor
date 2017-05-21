package elime.piceditor.entities;

import lombok.Data;

/**
 * Created by Elime on 15-08-16.
 */

@Data
public class Pic {
    private Thing[] things;
    private int signature;
    private int numberOfImages;
    private int numberOfBytes;
}
