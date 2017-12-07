package io.github.elime1.piceditor.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class WritableDataBuffer {

    private static Logger log = LogManager.getLogger();

    private ByteBuffer byteBuffer;

    public WritableDataBuffer(int size) {
        byteBuffer = ByteBuffer.allocate(size);
        byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
    }

    public void rewind() {
        byteBuffer.rewind();
    }

    public void position(int newPosition) {
        byteBuffer.position(newPosition);
    }

    public void skip(int nBytes) {
        byteBuffer.position(byteBuffer.position() + nBytes);
    }

    public int position() {
        return byteBuffer.position();
    }

    public void putByte(byte value) {
        byteBuffer.put(value);
    }

    public void putU8(int value) {
        byteBuffer.put((byte)value);
    }

    public void putU16(int value) {
        byteBuffer.putChar((char) value);
    }

    public void putU16(int index, int value) {
        byteBuffer.putChar(index, (char) value);
    }

    public void putU32(long value) {
        byteBuffer.putInt((int) value);
    }

    public void putBytes(byte[] bytes) {
        byteBuffer.put(bytes);
    }

    public byte[] array() {
        byte[] bufferArray = byteBuffer.array();
        return Arrays.copyOf(bufferArray, byteBuffer.position());
    }


    public void writeFile(File picFile) {
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(picFile);
            outputStream.write(byteBuffer.array());
        } catch (FileNotFoundException e) {
            log.warn("Could not open file: " + picFile.getPath());
            throw new RuntimeException(e);
        } catch (IOException e) {
            log.warn("Failed while writing to file: " + picFile.getPath());
            throw new RuntimeException(e);
        } finally {
            try {
                if (outputStream != null) outputStream.close();
            } catch (IOException e) {
                log.warn("Failed to close FileOutputStream");
                throw new RuntimeException(e);
            }
        }
    }
}