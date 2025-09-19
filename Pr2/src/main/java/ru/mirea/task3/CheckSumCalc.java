package ru.mirea.task3;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CheckSumCalc {

    private static final String DEFAULT_FILENAME = "tmp/source_100mb.txt";

    public static void main(String[] args) {
        String filename = args.length > 0 ? args[0] : DEFAULT_FILENAME;
        try {
            int checksum = calc16BitChecksum(filename);
            System.out.printf("16-bit control sum of file %s: 0x%04X\n", filename, checksum);
        } catch (IOException e) {
            System.err.println("Error during file reading: " + e.getMessage());
        }
    }

    public static int calc16BitChecksum(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename);
             FileChannel channel = fis.getChannel()) {
            ByteBuffer buffer = ByteBuffer.allocate(8192);
            int checksum = 0;
            while (channel.read(buffer) > 0) {
                buffer.flip();
                while (buffer.remaining() > 0) {
                    checksum += (buffer.get() & 0xFF);
                    checksum = (checksum & 0xFFFF) + (checksum >>> 16);
                }
                buffer.clear();
            }
            return checksum & 0xFFFF;
        }
    }
}
