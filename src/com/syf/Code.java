package com.syf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
* @file Code.java
* @CopyRight (C) https://github.com/Sngunfei
* @brief
* @author syfnico
* @email syfnico@foxmail.com
* @date 2018-3-31
*/

public class Code{

    public static ArrayList<String> IDENTIFIER = new ArrayList<>();
    public static ArrayList<String> CONSTANT = new ArrayList<>();
    public static ArrayList<String> STRING_LITERAL = new ArrayList<>();
    public static ArrayList<String> KEYWORD = new ArrayList<>(35);
    public static HashMap<String, String> OP = new HashMap<>();

    // 32个关键字
    static{
        KEYWORD.add("volatile");
        KEYWORD.add("struct");
        KEYWORD.add("typedef");
        KEYWORD.add("union");
        KEYWORD.add("auto");
        KEYWORD.add("extern");
        KEYWORD.add("unsigned");
        KEYWORD.add("signed");
        KEYWORD.add("static");
        KEYWORD.add("sizeof");
        KEYWORD.add("const");
        KEYWORD.add("enum");
        KEYWORD.add("char");
        KEYWORD.add("long");
        KEYWORD.add("float");
        KEYWORD.add("double");
        KEYWORD.add("short");
        KEYWORD.add("int");
        KEYWORD.add("default");
        KEYWORD.add("case");
        KEYWORD.add("switch");
        KEYWORD.add("continue");
        KEYWORD.add("break");
        KEYWORD.add("goto");
        KEYWORD.add("else");
        KEYWORD.add("if");
        KEYWORD.add("void");
        KEYWORD.add("return");
        KEYWORD.add("do");
        KEYWORD.add("while");
        KEYWORD.add("for");
        KEYWORD.add("rigister");

        OP.put(">>=", "RIGHT_ASSIGN");
        OP.put("<<=", "LEFT_ASSIGN");
        OP.put("+=", "ADD_ASSIGN");
        OP.put("-=", "SUB_ASSIGN");
        OP.put("*=", "MUL_ASSIGN");
        OP.put("/=", "DIV_ASSIGN");
        OP.put("%=", "MOD_ASSIGN");
        OP.put("&=", "AND_ASSIGN");
        OP.put("^=", "XOR_ASSIGN");
        OP.put("|=", "OR_ASSIGN");
        OP.put(">>", "RIGHT_OP");
        OP.put("<<", "LEFT_OP");
        OP.put("++", "INC_OP");
        OP.put("--", "DEC_OP");
        OP.put("->", "PTR_OP");
        OP.put("&&", "AND_OP");
        OP.put("||", "OR_OP");
        OP.put("<=", "LE_OP");
        OP.put(">=", "GE_OP");
        OP.put("==", "EQ_OP");
        OP.put("!=", "NE_OP");

    }

}
