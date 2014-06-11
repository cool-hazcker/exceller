package com.digsolab;

public class Format {

    private Type type = null;
    private String mask = null;
    private int width = -1;

    public Format(Type type, String mask, int width) {
        this.type = type;
        this.mask = mask;
        this.width = width;
    }

    public Type getType() {
        return this.type;
    }

    public String getMask() {
        return this.mask;
    }

    public int getWidth() {
        return width;
    }
}
