package com.example.menozzi.hw2;

import android.support.annotation.NonNull;

public class FixedCircularBuffer<T extends Comparable<T>> {
    private T[] buf;

    private int front;
    private int rear;

    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public FixedCircularBuffer(int capacity) {
        buf = (T[]) new Comparable[capacity];

        this.capacity = capacity;
    }

    public synchronized void add(@NonNull T element) {
        if (isFull()) {
            front = (front+1) % capacity;
        } else {
            size++;
        }
        buf[rear] = element;
        rear = (rear+1) % capacity;
    }

    @NonNull
    public synchronized T get(int i) {
        return buf[(front + i) % capacity];
    }

    public synchronized int getSize() {
        return size;
    }

    public synchronized boolean isEmpty() {
        return size == 0;
    }

    public synchronized boolean isFull() {
        return size == capacity;
    }

    public synchronized T getMax() {
        if (isEmpty()) {
            return null;
        }
        T max = get(0);
        for (int i = 1; i < getSize(); i++) {
            T elem = get(i);
            if (elem.compareTo(max) > 0) {
                max = elem;
            }
        }
        return max;
    }

    public synchronized FixedCircularBuffer<T> copy() {
        FixedCircularBuffer<T> newbuf = new FixedCircularBuffer<>(capacity);
        System.arraycopy(buf, 0, newbuf.buf, 0, capacity);
        newbuf.front = front;
        newbuf.rear = rear;
        newbuf.size = size;
        return newbuf;
    }

    public synchronized String toString() {
        if (isEmpty()) {
            return "(len: 0, cap: " + capacity + ")";
        }
        StringBuilder sb = new StringBuilder(size);
        sb.append("(len: " + size + ", cap: " + capacity + ") ");
        for (int i = 0; i < size; i++) {
            sb.append(get(i).toString());
            if (i != size-1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Quick test
        FixedCircularBuffer<Integer> buf = new FixedCircularBuffer<>(4);
        System.out.println(buf);
        for (int i = 0; i < 20; i++) {
            buf.add(i);
            System.out.println(buf);
        }
    }
}
