package ru.mirea.task2;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Slf4j
public class FileCopy {

    public static void main(String[] args) throws Exception {
        String source = "tmp/source_100mb.txt";
        String streamsCopy = "tmp/streams_copy.txt";
        String channelCopy = "tmp/channel_copy.txt";
        String commonsIOCopy = "tmp/commons_io_copy.txt";
        String filesCopy = "tmp/files_copy.txt";

        measure(() -> copyWithStreams(source, streamsCopy), "FileInputStream/FileOutputStream");
        measure(() -> copyWithChannel(source, channelCopy), "FileChannel");
        measure(() -> copyWithCommonsIO(source, commonsIOCopy), "Apache Commons IO");
        measure(() -> copyWithFiles(source, filesCopy), "Files.copy");
    }

    static void measure(Runnable action, String method) {
        System.gc();
        long memBefore = usedMemory();
        long start = System.nanoTime();
        action.run();
        long end = System.nanoTime();
        long memAfter = usedMemory();
        System.out.printf("%s: Time = %.2f s, memory = %.2f MB\n", method, (end-start)/1e9, (memAfter-memBefore)/1024.0/1024.0);
    }

    static long usedMemory() {
        Runtime r = Runtime.getRuntime();
        return r.totalMemory() - r.freeMemory();
    }

    static void copyWithStreams(String src, String dst) {
        try (InputStream in = new FileInputStream(src); OutputStream out = new FileOutputStream(dst)) {
            byte[] buf = new byte[8192];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
        } catch (IOException e) {
            log.error("CopyWithStreams IOException: {}", e.getMessage());
        }
    }

    static void copyWithChannel(String src, String dst) {
        try (FileChannel in = new FileInputStream(src).getChannel(); FileChannel out = new FileOutputStream(dst).getChannel()) {
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            log.error("CopyWithChannel IOException: {}", e.getMessage());
        }
    }

    static void copyWithFiles(String src, String dst) {
        try {
            Files.copy(Path.of(src), Path.of(dst), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            log.error("CopyWithFiles IOException: {}", e.getMessage());
        }
    }
    static void copyWithCommonsIO(String src, String dst) {
        try {
            FileUtils.copyFile(new java.io.File(src), new java.io.File(dst));
        } catch (java.io.IOException e) {
            log.error("CopyWithCommonsIO IOException: {}", e.getMessage());
        }
    }

}
