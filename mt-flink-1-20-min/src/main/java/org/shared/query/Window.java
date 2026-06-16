package org.shared.query;

import java.io.Serializable;

public class Window implements Serializable {
    private int start;
    private int length;
    private int slide;

    public Window(int start, int length, int slide) {
        this.start = start;
        this.length = length;
        this.slide = slide;
    }


    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public int getSlide() {
        return slide;
    }

    @Override
    public String toString() {
        return "window(" + start + " - " + length + ", slide: " + slide + ")";
    }
}
