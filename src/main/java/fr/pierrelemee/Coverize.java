package fr.pierrelemee;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

public class Coverize {

    private static final String COVER_FILENAME = ".cover.png";

    protected CoverProvider provider;
    protected Scanner scanner;

    public Coverize(CoverProvider coverizer) {
        this.provider = coverizer;
        this.scanner = new Scanner(System.in);
    }

    public void coverize(String path) {
        Long start = System.currentTimeMillis();
        File directory = new File(path);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                if (directory.canRead()) {
                    for(File subdirectory: listSubdirectories(directory)) {
                        this.processArtistDirectory(subdirectory);
                    }
                }
            }
        }
        System.out.println(String.format("Process terminated in %d ms through %d thread(s)", System.currentTimeMillis() - start, Thread.activeCount()));
    }

    protected void processArtistDirectory(File directory) {
        System.out.println(String.format("Processing albums for artist %s", directory.getName()));
        for (File subdirectory : listSubdirectories(directory)) {
            this.processAlbumDirectory(subdirectory);
        }
    }

    protected void processAlbumDirectory(File directory) {
        try {
            URL coverUrl = this.provider.getCoverURL(directory.getParentFile().getName(), directory.getName());
            this.saveCover(coverUrl, directory);
            System.out.println(String.format("Downloaded cover from %s to %s", coverUrl, directory.getAbsolutePath() + "/.cover.png"));
        } catch (Exception e) {
            System.err.println(String.format("No available cover for directory %s", directory.getAbsolutePath()));
        }


    }

    protected void saveCover(URL coverUrl, File directory) throws Exception {
        InputStream in = new BufferedInputStream(coverUrl.openStream());
        OutputStream out = new BufferedOutputStream(new FileOutputStream(directory.getAbsolutePath() + File.pathSeparator + COVER_FILENAME));
        for ( int i; (i = in.read()) != -1; ) {
            out.write(i);
        }
        in.close();
        out.close();
    }

    private static File[] listSubdirectories(File directory) {
        return directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
    }
}