package pl.eurokawa.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class BalanceBroadcaster {
    private final static ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final static List<Consumer<Double>> listeners = new ArrayList<>();

    public static synchronized void register (Consumer<Double> listener){
        listeners.add(listener);
    }

    public static synchronized void broadcast(Double newBalance){
        for(Consumer<Double> listener : listeners){
//            executorService.execute(() -> listener.accept(newBalance));
            listener.accept(newBalance);
        }
    }
}
