package com.example.menozzi.hw2;

import android.support.annotation.NonNull;

public class FixedCirculuarFloatBuffer {
    private float[] buf;

    private int front;
    private int rear;

    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public FixedCirculuarFloatBuffer(int capacity) {
        buf = new float[capacity];

        this.capacity = capacity;
    }

    public synchronized void add(@NonNull float element) {
        if (isFull()) {
            front = (front+1) % capacity;
        } else {
            size++;
        }
        buf[rear] = element;
        rear = (rear+1) % capacity;
    }

    @NonNull
    public synchronized float get(int i) {
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

    public synchronized Float getMax() {
        if (isEmpty()) {
            return null;
        }
        float max = get(0);
        for (int i = 1; i < getSize(); i++) {
            float elem = get(i);
            if (elem > max) {
                max = elem;
            }
        }
        return max;
    }

    public synchronized FixedCirculuarFloatBuffer copy() {
        FixedCirculuarFloatBuffer newbuf = new FixedCirculuarFloatBuffer(capacity);
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
            sb.append(get(i));
            if (i != size-1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        // Quick test
        FixedCirculuarFloatBuffer buf = new FixedCirculuarFloatBuffer(4);
        System.out.println(buf);
        for (int i = 0; i < 20; i++) {
            buf.add(i);
            System.out.println(buf);
        }
    }
}
