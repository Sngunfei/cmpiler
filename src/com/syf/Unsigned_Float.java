package com.syf;

public class Unsigned_Float extends Token {
    public final float value;
    Unsigned_Float(float num){
        super(Code.UNSIGNED_FLOAT.ordinal());
        this.value = num;
    }
}
