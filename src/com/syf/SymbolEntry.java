package com.syf;

public class SymbolEntry {

    private int code;
    private String lexme;
    private int intValue;
    private float floatValue;

    public SymbolEntry(Token token){
        this.code = token.tag;
        if(token instanceof Word) {
            this.lexme = ((Word) token).lexme;
        }else if(token instanceof Unsigned_Float){
            this.floatValue = ((Unsigned_Float) token).value;
        }else if(token instanceof Unsigned_Int){
            this.intValue = ((Unsigned_Int) token).value;
        }
    }

    /*
     *  使用String的hashcode函数，用name + code 唯一标识。
     */
    @Override
    public int hashCode(){
        return this.lexme.hashCode() + this.code;
    }

    @Override
    public boolean equals(Object object){
        if(this == object)
            return true;
        if(object instanceof SymbolEntry){
            if(this.getLexme() == ((SymbolEntry) object).getLexme())
                if(this.getCode() == ((SymbolEntry) object).getCode())
                    return true;
        }
        return false;
    }

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
}
