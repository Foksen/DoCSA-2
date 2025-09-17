package ru.mirea.task4;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class WatchDirApp {

    private static final String DIRNAME = "tmp/watch";

    public static void main(String[] args) throws IOException {
        Path dir = Paths.get(DIRNAME);
        new WatchDir(dir).processEvents();
    }
}
