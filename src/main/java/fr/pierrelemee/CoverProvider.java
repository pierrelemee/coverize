package fr.pierrelemee;

import java.net.URL;

public interface CoverProvider {

    URL getCoverURL(String artist, String album) throws Exception;
}
