package com.example.menozzi.hw2;

import android.support.annotation.NonNull;

public class FixedCircularBuffer<T> {
    private T[] buf;

    private int front;
    private int rear;

    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public FixedCircularBuffer(int capacity) {
        buf = (T[]) new Object[capacity];

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

    private boolean isEmpty() {
        return size == 0;
    }

    private boolean isFull() {
        return size == capacity;
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
