package com.syf;

public class SymbolEntry {

    private String name;
    private int code;
    private String lexme;
    private int intValue;
    private float floatValue;

    public float getFloatValue() {
        return floatValue;
    }

    public int getIntValue() {
        return intValue;
    }

    public int getCode(){
        return code;
    }

    public String getLexme() {
        return lexme;
    }

    public String getName() {
        return name;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setFloatValue(float floatValue) {
        this.floatValue = floatValue;
    }

    public void setLexme(String lexme) {
        this.lexme = lexme;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIntValue(int intValue) {
        this.intValue = intValue;
    }
}
