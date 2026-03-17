package queue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

/**
 * Thread-safe lane queue wrapper around ConcurrentLinkedQueue.
 * Provides a small API used by the simulation engine and monitor.
 */
public class LaneQueue<T> {
    private final ConcurrentLinkedQueue<T> queue = new ConcurrentLinkedQueue<>();

    public boolean offer(T element) {
        return queue.offer(element);
    }

    public T poll() {
        return queue.poll();
    }

    public T peek() {
        return queue.peek();
    }

    public int size() {
        return queue.size();
    }

    /**
     * Snapshot of current items in the queue. Useful for logging/statistics.
     */
    public List<T> snapshot() {
        return new ArrayList<>(queue);
    }

    /**
     * Stream view over a snapshot to safely use stream operations without being affected by concurrent modifications.
     */
    public Stream<T> stream() {
        return snapshot().stream();
    }
}
