package bg.sofia.uni.fmi.mjt.steganography;

import java.util.LinkedList;
import java.util.Queue;

public class SharedBuffer {
    private Queue<SteganographyImage> queue = new LinkedList<>();
    private int capacity;
    private boolean producersFinished = false;

    public SharedBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("The capacity of the buffer should be greater than 0.");
        }
        this.capacity = capacity;
    }

    //Called from the producer threads, it ensures the buffer is not full before adding an image
    public synchronized void put(SteganographyImage img) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.add(img);
        notifyAll();
    }

    //Called from the consumer threads, ensures the buffer is not empty when getting an image
    //producerFinished flag - notifies the consumer threads when all the producer threads
    // are finished, so the program can stop
    public synchronized SteganographyImage get() throws InterruptedException {
        while (queue.isEmpty() && !producersFinished) {
            wait();
        }

        if (queue.isEmpty() && producersFinished) {
            return null;
        }

        SteganographyImage image = queue.poll();
        notifyAll();
        return image;
    }

    public synchronized void setProducersFinished() {
        this.producersFinished = true;
        notifyAll();
    }
}
