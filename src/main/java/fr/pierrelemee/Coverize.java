package fr.pierrelemee;

import rx.Observable;
import rx.Subscriber;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Coverize {

    private static final String COVER_FILENAME = ".cover.png";

    protected CoverProvider provider;
    protected List<File> pendings;
    protected Long start;
    protected Scanner scanner;

    public Coverize(CoverProvider coverizer) {
        this.provider = coverizer;
        this.pendings = new ArrayList<File>();
        this.scanner = new Scanner(System.in);
    }

    public void coverize(String path) {
        this.start = System.currentTimeMillis();
        File directory = new File(path);
        if (directory.exists()) {
            if (directory.isDirectory()) {
                if (directory.canRead()) {
                    for (File subdirectory : listSubdirectories(directory)) {
                        this.processArtistDirectory(subdirectory);
                    }
                }
            }
        }
    }

    protected void processArtistDirectory(File directory) {
        System.out.println(String.format("Processing albums for artist %s", directory.getName()));
        for (File subdirectory : listSubdirectories(directory)) {
            this.processAlbumDirectory(subdirectory);
        }
    }

    protected void processAlbumDirectory(final File directory) {
        this.pendings.add(directory);
        Observable<URL> observable = this.provider.getCoverURL(directory.getParentFile().getName(), directory.getName());
        observable.subscribe(new Subscriber<URL>() {
            public void onCompleted() {
            }

            public void onError(Throwable e) {
                pendings.remove(directory);
                System.err.println(String.format("No available cover for directory %s", directory.getAbsolutePath()));
                if (pendings.isEmpty()) {
                    System.out.println(String.format("Process terminated in %d ms through %d thread(s)", System.currentTimeMillis() - start, Thread.activeCount()));
                    provider.terminate();
                }
            }

            public void onNext(URL url) {
                saveCover(url, directory);
            }
        });
    }

    protected void saveCover(final URL coverUrl, final File directory) {
        Observable.create(new Observable.OnSubscribe<File>() {
            public void call(final Subscriber<? super File> subscriber) {
                try {
                    File output = new File(directory.getAbsolutePath(), COVER_FILENAME);
                    InputStream in = new BufferedInputStream(coverUrl.openStream());
                    OutputStream out = new BufferedOutputStream(new FileOutputStream(output));
                    for (int i; (i = in.read()) != -1; ) {
                        out.write(i);
                    }
                    in.close();
                    out.close();
                    subscriber.onNext(output);
                    subscriber.onCompleted();
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        }).subscribe(new Subscriber<File>() {
            public void onCompleted() {
                // Nothing to do
            }

            public void onError(Throwable e) {
                System.err.println(String.format("Error while downloading cover from %s", coverUrl));
            }

            public void onNext(File file) {
                pendings.remove(directory);
                System.out.println(String.format("Downloaded cover from %s to %s", coverUrl, directory.getAbsolutePath() + "/.cover.png"));
                if (pendings.isEmpty()) {
                    System.out.println(String.format("Process terminated in %d ms through %d thread(s)", System.currentTimeMillis() - start, Thread.activeCount()));
                    provider.terminate();
                }
            }
        });
    }

    private static File[] listSubdirectories(File directory) {
        return directory.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.isDirectory();
            }
        });
    }
}