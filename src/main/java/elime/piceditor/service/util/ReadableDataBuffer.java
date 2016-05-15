package elime.piceditor.service.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

/**
 * Created by Elime on 15-08-09.
 */
public class ReadableDataBuffer {

    private static Logger log = LogManager.getLogger();

    private ByteBuffer byteBuffer;

    public ReadableDataBuffer(File picFile) {
        loadFile(picFile);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public int getNumberOfBytes() {
        return byteBuffer.limit();
    }

    public void rewind() {
        byteBuffer.rewind();
    }

    public void position(int newPosition) {
        byteBuffer.position(newPosition);
    }

    public int position() {
        return byteBuffer.position();
    }

    public long getU32() {
        return byteBuffer.getInt() & 0xffffffffl;
    }

    public int getU16() {
        return byteBuffer.getChar();
    }

    public int getU8() {
        return byteBuffer.get() & 0xFF;
    }

    public byte getByte() {
        return byteBuffer.get();
    }

    public byte[] getBytes(int nBytes) {
        byte[] bytes = new byte[nBytes];
        byteBuffer.get(bytes);
        return bytes;
    }

    public String getString(int bytes) {
        byte[] b = new byte[bytes];
        for (int i = 0; i < bytes; i++) {
            b[i] = byteBuffer.get();
        }
        return Arrays.toString(b);
    }

    public void skip(int nBytes) {
        byteBuffer.position(byteBuffer.position() + nBytes);
    }

    private void loadFile(File picFile) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(picFile);

            byte[] data = new byte[(int)picFile.length()];
            inputStream.read(data);

            this.byteBuffer = byteBuffer.wrap(data);

        } catch (FileNotFoundException e) {
            log.warn("Could not open the file: " + picFile.getPath());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.warn("Faild while reading file: " + picFile.getPath());
            throw new RuntimeException(e);
        } finally {
            try {
                if (inputStream != null) inputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close FileInputStream");
                throw new RuntimeException(e);
            }
        }
    }
}
