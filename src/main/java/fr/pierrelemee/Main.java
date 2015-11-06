package fr.pierrelemee;

public class Main {

    public static void main(String... args) {
        Coverize coverize = new Coverize(new LastfmCoverProvider("3ec43a7a83174245c37fbb75cc028c6c"));
        Long start = System.currentTimeMillis();
        coverize.coverize("/Users/pierrelemee/Music/coverize/");
        System.out.println(String.format("Process terminated in %d ms through %d thread(s)", System.currentTimeMillis() - start, Thread.activeCount()));
    }
}
