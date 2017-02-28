package com.example.menozzi.hw2.util;

import android.support.annotation.NonNull;

/**
 * Data structure for representing fixed-sized circular FIFO
 * buffers of floats. Useful for storing streaming sensor data.
 * Makes a (weak) attempt at being thread-safe by synchronizing
 * all actions that could potentially be happening in both the
 * background timer task and the canvas draw calls.
 */
public class FixedCircularFloatBuffer {
    private float[] buf;

    private int front;
    private int rear;

    private int size;
    private int capacity;

    @SuppressWarnings("unchecked")
    public FixedCircularFloatBuffer(int capacity) {
        buf = new float[capacity];

        this.capacity = capacity;
    }

    public synchronized void add(float element) {
        if (isFull()) {
            front = (front+1) % capacity;
        } else {
            size++;
        }
        buf[rear] = element;
        rear = (rear+1) % capacity;
    }

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

    public synchronized Float getMean() {
        if (isEmpty()) {
            return null;
        }
        int size = getSize();
        float sum = 0.0f;
        for (int i = 0; i < size; i++) {
            sum += get(i);
        }
        return sum/size;
    }

    public synchronized Float getStdDev() {
        if (isEmpty()) {
            return null;
        }
        float mean = getMean();
        float size = getSize();
        float ssd = 0.0f;
        for (int i = 0; i < size; i++) {
            ssd += Math.pow(get(i)-mean, 2);
        }
        return (float)Math.sqrt(ssd/size);
    }

    public synchronized FixedCircularFloatBuffer copy() {
        FixedCircularFloatBuffer newbuf = new FixedCircularFloatBuffer(capacity);
        System.arraycopy(buf, 0, newbuf.buf, 0, capacity);
        newbuf.front = front;
        newbuf.rear = rear;
        newbuf.size = size;
        return newbuf;
    }
}
