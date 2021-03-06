package com.example.menozzi.hw2.util;

/**
 * Helper class for representing bounding boxes by
 * their left-right-top-bottom edge coordinates.
 */
public class LTRB {
    public int l, t, r, b;

    public LTRB() {
        set(l,r,t,b);
    }

    public int width() {
        return r-l;
    }
    public int height() {
        return b-t;
    }

    public void set(int l, int t, int r, int b) {
        this.l = l;
        this.t = t;
        this.r = r;
        this.b = b;
    }
}
