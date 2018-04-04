package com.syf;

public class Const_String extends Token {
    public final String lexme;
    public Const_String(String name){
        super(Code.STRING.ordinal());
        this.lexme = name;
    }
}
