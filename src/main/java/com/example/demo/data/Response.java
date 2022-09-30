package com.example.demo.data;

public enum Response {
    CONNECT(1), DISCONNECT(2), NONE(3)
    ;

    private final int value;
    Response(int value) {
        this.value = value;
    }
    public int getValue() { return value; }
}
