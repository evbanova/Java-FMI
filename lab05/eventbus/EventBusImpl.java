package bg.sofia.uni.fmi.mjt.eventbus;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;
import bg.sofia.uni.fmi.mjt.eventbus.exception.MissingSubscriptionException;
import bg.sofia.uni.fmi.mjt.eventbus.subscribers.Subscriber;

public class EventBusImpl implements EventBus {

    private Map<Class <? extends Event<?>>, List<Subscriber<?>>> subscribersForEvent;
    private List<Event<?>> eventLogs;


    public EventBusImpl() {
        subscribersForEvent = new HashMap<>();
        eventLogs = new ArrayList<>();
    }
   
    /**
     * Subscribes the given subscriber to the given event type.
     * If the same subscriber is already subscribed to the given event type, the method
     * should do nothing (no duplicate subscriptions).
     *
     * @param eventType  the type of event to subscribe to
     * @param subscriber the subscriber to subscribe
     * @throws IllegalArgumentException if the event type is null
     * @throws IllegalArgumentException if the subscriber is null
     */
    @Override
    public <T extends Event<?>> void subscribe(Class<T> eventType, Subscriber<? super T> subscriber){
        if(subscriber == null)
            throw new IllegalArgumentException("the subscriber is null");

        if(eventType == null)
            throw new IllegalArgumentException("the eventType is null");    
        
        if(!subscribersForEvent.containsKey(eventType))
            subscribersForEvent.put(eventType, new ArrayList<Subscriber<?>>());

        if (!subscribersForEvent.get(eventType).contains(subscriber))
            subscribersForEvent.get(eventType).add(subscriber);
    }

    /**
     * Unsubscribes the given subscriber from the given event type.
     *
     * @param eventType  the type of event to unsubscribe from
     * @param subscriber the subscriber to unsubscribe
     * @throws IllegalArgumentException     if the event type is null
     * @throws IllegalArgumentException     if the subscriber is null
     * @throws MissingSubscriptionException if the subscriber is not subscribed to the event type
     */
    @Override
    public <T extends Event<?>> void unsubscribe(Class<T> eventType, Subscriber<? super T> subscriber)
        throws MissingSubscriptionException {

        if(subscriber == null)
            throw new IllegalArgumentException("the subscriber is null");

        if(eventType == null)
            throw new IllegalArgumentException("the eventType is null"); 

        if(!subscribersForEvent.containsKey(eventType) || !subscribersForEvent.get(eventType).contains(subscriber))
            throw new MissingSubscriptionException("the given subscriber is not subscribed to that event");

        subscribersForEvent.get(eventType).remove(subscriber);
        }

    /**
     * Publishes the given event to all subscribers of the event type.
     *
     * @param event the event to publish
     * @throws IllegalArgumentException if the event is null
     */
    @Override
    public <T extends Event<?>> void publish(T event){
        if(event == null)
            throw new IllegalArgumentException("the event is null");
        
        //Iterator<T> iterator =  subscribersForEvent.get(event).iterator();  
        
        for (Subscriber s : subscribersForEvent.get(event)) {
            s.onEvent(event);
        }

        eventLogs.add(event);
    }

    /**
     * Clears all subscribers and event logs.
     */
    @Override
    public void clear(){
        subscribersForEvent.clear();
        eventLogs.clear();
    }

    /**
     * Returns all events of the given event type that occurred between the given timestamps. If
     * {@code from} and {@code to} are equal the returned collection is empty.
     * <p> {@code from} - inclusive, {@code to} - exclusive. </p>
     *
     * @param eventType the type of event to get
     * @param from      the start timestamp (inclusive)
     * @param to        the end timestamp (exclusive)
     * @return an unmodifiable collection of events of the given event type that occurred between
     * the given timestamps
     * @throws IllegalArgumentException if the event type is null
     * @throws IllegalArgumentException if the start timestamp is null
     * @throws IllegalArgumentException if the end timestamp is null
     */
    @Override
     public Collection<? extends Event<?>> getEventLogs(Class<? extends Event<?>> eventType, Instant from, Instant to) {
        if(eventType == null)
            throw new IllegalArgumentException("the eventType is null"); 
         
        if(from == null || to == null)
            throw new IllegalArgumentException("the instants are null");     

        List<Event<?>> result = new ArrayList<>();

        if (from.equals(to))
            return List.copyOf(result);

        for (Event<?> event : eventLogs) {
            if(event.getTimestamp().isAfter(from) && event.getTimestamp().isBefore(to) && event.equals(eventType)){
                result.add(event);
                }
        }

        return List.copyOf(result);

    }
    
    /**
     * Returns all subscribers for the given event type in an unmodifiable collection. If there are
     * no subscribers for the event type, the method returns an empty unmodifiable collection.
     *
     * @param eventType the type of event to get subscribers for
     * @return an unmodifiable collection of subscribers for the given event type
     * @throws IllegalArgumentException if the event type is null
     */
    @Override
     public <T extends Event<?>> Collection<Subscriber<?>> getSubscribersForEvent(Class<T> eventType) {
        if(eventType == null)
            throw new IllegalArgumentException("the eventType is null"); 

        return List.copyOf(subscribersForEvent.get(eventType));    
    }

}