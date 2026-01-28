package bg.sofia.uni.fmi.mjt.eventbus.iterators;

import bg.sofia.uni.fmi.mjt.eventbus.comparators.CompareByPriority;
import bg.sofia.uni.fmi.mjt.eventbus.events.Event;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;


public class EventsByPriorityIterator<T extends Event<?>> implements Iterator<T>{
    private int currIndex;
    private List<T> data;
    
    public EventsByPriorityIterator(List<T> data){
        this.data = data;

        data.sort(new CompareByPriority<>());

        currIndex = data.size() - 1;
    }

    @Override
    public boolean hasNext(){
        return currIndex >= 0;
    }

    @Override
    public T next(){
         if (!hasNext()) {
            throw new NoSuchElementException("No more elements");
        }

        T element = data.get(currIndex);
        currIndex--;

        return element;
    }
    
}
