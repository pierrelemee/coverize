package fr.pierrelemee;

import rx.Subscriber;

public class GreetingRunnable extends Subscriber<String> implements Runnable {

    protected boolean isComplete = false;

    @Override
    public void onStart() {
        System.out.println("What's your name ?");
    }

    public void onCompleted() {
        System.out.println("No more hello to say, exiting...");
        this.isComplete = true;
    }

    public void onError(Throwable throwable) {
        System.out.println(throwable.getMessage());
    }

    public void onNext(String s) {
        System.out.println(String.format("Hello %s\n", s));
        System.out.println("What's your name ?");
    }

    public void run() {
        try {
            while (!this.isComplete) {
                Thread.currentThread().sleep(1000l);
                // Greet people
            }
        } catch (InterruptedException ie) {
            System.err.println("Fatal error");
        }
    }
}
