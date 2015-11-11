package fr.pierrelemee;

import rx.Observable;

import java.net.URL;

public interface CoverProvider {

    Observable<URL> getCoverURL(String artist, String album);

    void terminate();
}
