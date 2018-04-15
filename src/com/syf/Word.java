package com.syf;

public class Word extends Token {
    public final String lexme;
    public Word(String name){
        //super(Code.ID.ordinal());
        this.lexme = name;
    }
}
