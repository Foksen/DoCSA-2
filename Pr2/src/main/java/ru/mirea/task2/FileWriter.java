package ru.mirea.task2;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
public class FileWriter {

    private static final String FILENAME = "tmp/source_100mb.txt";
    private static final long SIZE = 100L * 1024 * 1024;

    public static void main(String[] args) {
        String line = "The quick brown fox jumps over the lazy dog\n";
        int lineBytes = line.getBytes(StandardCharsets.UTF_8).length;
        long bytesWritten = 0;

        try (BufferedWriter writer = Files.newBufferedWriter(
                Paths.get(FILENAME), StandardCharsets.UTF_8)) {

            StringBuilder block = new StringBuilder();
            int linesInBlock = 1000;
            for (int i = 0; i < linesInBlock; i++) {
                block.append(line);
            }
            int blockBytes = block.toString().getBytes(StandardCharsets.UTF_8).length;

            while (bytesWritten + blockBytes < SIZE) {
                writer.write(block.toString());
                bytesWritten += blockBytes;
            }
            while (bytesWritten < SIZE) {
                writer.write(line);
                bytesWritten += lineBytes;
            }

            System.out.println("File was created. Size " + bytesWritten / (1024 * 1024) + " MB");
        } catch (IOException e) {
            log.error("Error during file creating: {}", e.getMessage());
        }
    }
}
