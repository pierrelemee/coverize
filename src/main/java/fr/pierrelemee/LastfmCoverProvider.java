package fr.pierrelemee;

import com.jayway.jsonpath.JsonPath;
import io.mikael.urlbuilder.UrlBuilder;
import rx.Observable;
import rx.Subscriber;

import java.net.URL;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class LastfmCoverProvider implements CoverProvider {

    protected String key;
    protected ExecutorService executor;

    public LastfmCoverProvider(String key) {
        this.key = key;
        this.executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                5,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors(), true),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    public Observable<URL> getCoverURL(final String artist, final String album) {
        return Observable.create(new Observable.OnSubscribe<URL>() {
            public void call(final Subscriber<? super URL> subscriber) {
                executor.submit(new Runnable() {
                    public void run() {
                        try {
                            URL lastfm = UrlBuilder.fromString("http://ws.audioscrobbler.com/2.0/")
                                    .addParameter("method", "album.getinfo")
                                    .addParameter("api_key", key)
                                    .addParameter("artist", artist)
                                    .addParameter("album", album)
                                    .addParameter("format", "json").toUrl();

                            System.out.println(lastfm);
                            List<String> urls = JsonPath.parse(lastfm.openConnection().getInputStream()).read("$.album.image.[?(@.size==extralarge)].#text");

                            subscriber.onNext(new URL(urls.get(0)));
                            subscriber.onCompleted();
                        } catch (Exception e) {
                            subscriber.onError(e);
                        }
                    }
                });
            }
        });
    }

    public void terminate() {
        this.executor.shutdown();
    }
}
