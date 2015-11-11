package fr.pierrelemee;

public class Main {

    private static final String LASTFM_API_KEY = "3ec43a7a83174245c37fbb75cc028c6c";

    public static void main(String... args) {
        Coverize coverize = new Coverize(new LastfmCoverProvider(LASTFM_API_KEY));
        if (args.length > 0) {
            coverize.coverize(args[0]);
        } else {
            System.err.println("No music directory provided");
            System.exit(1);
        }
    }
}
