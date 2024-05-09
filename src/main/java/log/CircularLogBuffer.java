package log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Кольцевой буфер журнала, который реализует хранение элементов в кольцевом массиве.
 * Он обеспечивает потокобезопасность при многопоточном доступе к буферу.
 *
 * @param <T> тип элементов, хранимых в буфере
 */
public class CircularLogBuffer <T> {
    private final T[] buffer;
    private int size;
    private int start;
    private int end;
    private final ReentrantLock lock;
    private final Condition notFull;
    private final Condition notEmpty;

    /**
     * Создает новый кольцевой буфер с указанной емкостью.
     *
     * @param capacity емкость буфера
     */
    public CircularLogBuffer(int capacity) {
        buffer = (T[]) new Object[capacity];
        size = 0;
        start = 0;
        end = 0;
        lock = new ReentrantLock();
        notFull = lock.newCondition();
        notEmpty = lock.newCondition();
    }


    /**
     * Добавляет элемент в буфер. Если буфер полон, поток блокируется до освобождения места.
     *
     * @param entry элемент, который необходимо добавить в буфер
     */
    public void append(LogEntry entry) {
        lock.lock();
        try {
            if (size == buffer.length) {
                start = (start + 1) % buffer.length;
                size--; // Уменьшаем размер буфера при добавлении нового элемента при полном буфере
            }
            buffer[end] = (T) entry;
            end = (end + 1) % buffer.length;
            size++;
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }


    /**
     * Возвращает итератор, позволяющий перебирать элементы буфера начиная с указанного индекса.
     *
     * @param startFrom индекс, с которого начинается перебор
     * @param count количество элементов, которые нужно вернуть
     * @return итератор, позволяющий перебирать элементы буфера
     */
    public Iterable<LogEntry> range(int startFrom, int count) {
        lock.lock();
        try {
            if (startFrom < 0 || startFrom >= size) {
                return Collections.emptyList();
            }
            int index = (start + startFrom) % buffer.length;
            List<LogEntry> result = new ArrayList<>();
            for (int i = 0; i < count && i < size; i++) {
                result.add((LogEntry) buffer[index]);
                index = (index + 1) % buffer.length;
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Возвращает текущий размер буфера.
     *
     * @return текущий размер буфера
     */
    public int size() {
        lock.lock();
        try {
            return size;
        } finally {
            lock.unlock();
        }
    }



    /**
     * Возвращает список, содержащий все элементы буфера.
     *
     * @return список элементов буфера
     */
    public List<LogEntry> all() {
        lock.lock();
        try {
            List<LogEntry> result = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                result.add((LogEntry) buffer[(start + i) % buffer.length]);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }
    /**
     * Очищает буфер.
     */
    public void clear() {
        lock.lock();
        try {
            Arrays.fill(buffer, null);
            size = 0;
            start = 0;
            end = 0;
            notFull.signalAll();
        } finally {
            lock.unlock();
        }
    }
}