package fr.pierrelemee;

import com.jayway.jsonpath.JsonPath;
import io.mikael.urlbuilder.UrlBuilder;

import java.net.URL;
import java.util.List;

public class LastfmCoverProvider implements CoverProvider {

    protected String key;

    public LastfmCoverProvider(String key) {
        this.key = key;
    }

    public URL getCoverURL(String artist, String album) throws Exception {
        URL lastfm = UrlBuilder.fromString("http://ws.audioscrobbler.com/2.0/")
                .addParameter("method", "album.getinfo")
                .addParameter("api_key", this.key)
                .addParameter("artist", artist)
                .addParameter("album", album)
                .addParameter("format", "json").toUrl();

        System.out.println(lastfm);
        List<String> urls = JsonPath.parse(lastfm.openConnection().getInputStream()).read("$.album.image.[?(@.size==extralarge)].#text");
        return new URL(urls.get(0));
    }
}
