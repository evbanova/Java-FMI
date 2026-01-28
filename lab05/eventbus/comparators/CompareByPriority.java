package bg.sofia.uni.fmi.mjt.eventbus.comparators;

import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

import java.util.Comparator;

public class CompareByPriority<T extends Event<?>> implements Comparator<T>{

    public int compare(T e1, T e2){

        if (e1.getPriority() == e2.getPriority()) {
            return e2.getTimestamp().compareTo(e1.getTimestamp());
        }    
        else {
            return e1.getPriority() - e2.getPriority();
        }
    }    
}

