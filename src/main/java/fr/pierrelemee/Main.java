package fr.pierrelemee;

import rx.Observable;
import rx.Subscriber;
import java.util.Scanner;

public class Main {

    public static void main(String... args) {
        final Scanner scanner = new Scanner(System.in);
        Observable<String> myObservable = Observable.create(new Observable.OnSubscribe<String>(){
            public void call(Subscriber<? super String> subscriber) {
                String line;
                while (!(line = scanner.nextLine()).equalsIgnoreCase("quit")) {
                    if (line.isEmpty()) {
                        subscriber.onError(new Exception("Sorry, what's your name again ?"));
                    } else {
                        subscriber.onNext(line);
                    }
                }
                subscriber.onCompleted();
            }
        });
        GreetingRunnable runnable = new GreetingRunnable();
        myObservable.subscribe(runnable);
        new Thread(runnable).run();
    }
}
