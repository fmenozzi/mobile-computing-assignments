package com.example.menozzi.hw2;

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

    public void add(T element) {
        if (isFull()) {
            front = (front+1) % capacity;
        } else {
            size++;
        }
        buf[rear] = element;
        rear = (rear+1) % capacity;
    }

    public T get(int i) {
        return buf[(front + i) % capacity];
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public boolean isFull() {
        return size == capacity;
    }

    public String toString() {
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
