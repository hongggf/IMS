package com.inventory.core;

import java.util.ArrayList;
import java.util.List;

public class AppEventBus {

    private static final List<Runnable> listeners = new ArrayList<>();

    public static void subscribe(Runnable r) {
        listeners.add(r);
    }

    public static void publish() {
        for (Runnable r : listeners) {
            r.run();
        }
    }
}