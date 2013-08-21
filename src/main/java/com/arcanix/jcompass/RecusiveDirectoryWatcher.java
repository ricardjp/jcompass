package com.arcanix.jcompass;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ricardjp@arcanix.com (Jean-Philippe Ricard)
 */
public class RecusiveDirectoryWatcher implements Runnable {

    private final Path directory;
    private final boolean recursive;
    private final FileListener fileListener;

    private final Map<WatchKey, Path> watchKeys = new HashMap<>();

    public RecusiveDirectoryWatcher(Path directory, FileListener fileListener, boolean recursive) {
        this.directory = directory;
        this.recursive = recursive;
        this.fileListener = fileListener;
    }

    @Override
    public void run() {
        try {
            processEvents();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void register(WatchService watchService, Path path) throws IOException {
        WatchKey key = path.register(
                watchService,
                StandardWatchEventKinds.ENTRY_CREATE,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        this.watchKeys.put(key, path);
    }

    private void registerAll(final WatchService watchService, Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attributes) throws IOException {
                register(watchService, path);
                return FileVisitResult.CONTINUE;
            }

        });
    }

    public void processEvents() throws Exception {
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {

            if (recursive) {
                registerAll(watchService, this.directory);
            } else {
                register(watchService, this.directory);
            }

            while(true) {
                WatchKey key;
                try {
                    key = watchService.take();
                } catch (InterruptedException x) {
                    return;
                }

                Path dir = this.watchKeys.get(key);
                if (dir == null) {
                    continue;
                }

                for (WatchEvent<?> event: key.pollEvents()) {
                    WatchEvent.Kind kind = event.kind();

                    if (kind == StandardWatchEventKinds.OVERFLOW) {
                        continue;
                    }

                    WatchEvent<Path> castedEvent = (WatchEvent<Path>) event;
                    Path name = castedEvent.context();
                    Path child = dir.resolve(name);

                    if (recursive && (kind == StandardWatchEventKinds.ENTRY_CREATE)) {
                        if (Files.isDirectory(child, LinkOption.NOFOLLOW_LINKS)) {
                            registerAll(watchService, child);
                        }
                    }

                    if (kind == StandardWatchEventKinds.ENTRY_CREATE) {
                        this.fileListener.fileCreated(child.toAbsolutePath());
                    } else if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                        this.fileListener.fileChanged(child.toAbsolutePath());
                    } else if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                        this.fileListener.fileDeleted(child.toAbsolutePath());
                    }

                }

                // reset key and remove from set if directory no longer accessible
                boolean valid = key.reset();
                if (!valid) {
                    this.watchKeys.remove(key);

                    if (this.watchKeys.isEmpty()) {
                        break;
                    }
                }
            }
        }
    }
}
