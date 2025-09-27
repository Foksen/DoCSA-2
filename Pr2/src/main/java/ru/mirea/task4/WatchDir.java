package ru.mirea.task4;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class WatchDir {

    private final Map<Path, List<String>> fileContentCache = new ConcurrentHashMap<>();
    private final Map<Path, FileInfo> fileInfoCache = new ConcurrentHashMap<>();
    private final Path dir;

    public WatchDir(Path dir) {
        this.dir = dir;
    }

    public void processEvents() throws IOException {
        WatchService watcher = FileSystems.getDefault().newWatchService();
        dir.register(watcher,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_MODIFY,
                StandardWatchEventKinds.ENTRY_DELETE
        );

        while (true) {
            WatchKey key;
            try {
                key = watcher.take();
            } catch (InterruptedException ex) {
                return;
            }

            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();
                Path name = (Path) event.context();
                Path child = dir.resolve(name);

                if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                    log.info("File was created: {}", name);
                    try {
                        if (Files.isRegularFile(child)) {
                            List<String> lines = Files.readAllLines(child, StandardCharsets.UTF_8);
                            fileContentCache.put(child, lines);

                            byte[] content = Files.readAllBytes(child);
                            fileInfoCache.put(child, new FileInfo(content.length, calc16bitChecksum(content)));
                        }
                    } catch (Exception e) {
                        log.info("Failed to read file after creation: {}", name);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    try {
                        if (Files.isRegularFile(child)) {
                            List<String> oldLines = fileContentCache.getOrDefault(child, Collections.emptyList());
                            List<String> newLines = Files.readAllLines(child, StandardCharsets.UTF_8);

                            printDiff(name, oldLines, newLines);

                            fileContentCache.put(child, newLines);

                            byte[] content = Files.readAllBytes(child);
                            fileInfoCache.put(child, new FileInfo(content.length, calc16bitChecksum(content)));
                        }
                    } catch (Exception e) {
                        log.info("Failed to compute file changes: {}", name);
                    }
                } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    fileContentCache.remove(child);

                    FileInfo info = fileInfoCache.remove(child);
                    if (info != null) {
                        log.info("File was deleted: {}", name);
                        log.info("  Last known size: {} bytes", info.size);
                        log.info("  Last known checksum: 0x{}", String.format("%04X", info.checksum));
                    } else {
                        log.info("File was deleted: {}, no checksum/size info available.", name);
                    }
                }
            }
            boolean valid = key.reset();
            if (!valid) break;
        }
    }

    private void printDiff(Path filename, List<String> oldLines, List<String> newLines) {
        Set<String> oldSet = new HashSet<>(oldLines);
        Set<String> newSet = new HashSet<>(newLines);

        List<String> added = new ArrayList<>();
        List<String> removed = new ArrayList<>();

        for (String s : newSet)
            if (!oldSet.contains(s)) added.add(s);
        for (String s : oldSet)
            if (!newSet.contains(s)) removed.add(s);

        if (!added.isEmpty())
            log.info("In file {} lines were added: {}", filename, added);
        if (!removed.isEmpty())
            log.info("In file {} lines were deleted: {}", filename, removed);
    }

    private int calc16bitChecksum(byte[] bytes) {
        int checksum = 0;
        for (byte b : bytes) {
            checksum += (b & 0xFF);
            checksum = (checksum & 0xFFFF) + (checksum >>> 16);
        }
        return checksum & 0xFFFF;
    }

    private static class FileInfo {
        final long size;
        final int checksum;

        public FileInfo(long size, int checksum) {
            this.size = size;
            this.checksum = checksum;
        }
    }
}
