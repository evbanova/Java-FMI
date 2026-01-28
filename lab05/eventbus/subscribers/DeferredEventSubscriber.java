package bg.sofia.uni.fmi.mjt.eventbus.subscribers;

import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.iterators.EventsByPriorityIterator;

public class DeferredEventSubscriber<T extends Event<?>> implements Subscriber<T>, Iterable<T> {

    private List<T> unprocessedEventsList;

    public DeferredEventSubscriber() {
        unprocessedEventsList = new ArrayList<T>();
    }

    /**
     * Store an event for processing at a later time.
     *
     * @param event the event to be processed
     * @throws IllegalArgumentException if the event is null
     */
    @Override
    public void onEvent(T event) {
        if (event == null)
            throw new IllegalArgumentException("Event agruments is null");

        unprocessedEventsList.add(event);   
    }

    /**
     * Get an iterator for the unprocessed events. The iterator should provide the events sorted
     * by priority, with higher-priority events first (lower priority number = higher priority).
     * For events with equal priority, earlier events (by timestamp) come first.
     *
     * @return an iterator for the unprocessed events
     */
    @Override
    public Iterator<T> iterator() {
        return new EventsByPriorityIterator<>(unprocessedEventsList);
    }

    /**
     * Check if there are unprocessed events.
     *
     * @return true if there are unprocessed events, false otherwise
     */
    public boolean isEmpty() {
        return !unprocessedEventsList.isEmpty();
    }

}