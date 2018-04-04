package com.syf;

/**
* @file Code.java
* @CopyRight (C) https://github.com/Sngunfei
* @brief
* @author syfnico
* @email syfnico@foxmail.com
* @date 2018-3-31
*/

public enum Code{
	ID,   // 标识符类
    STRING, // 字符串常量
    UNSIGNED_INT, // 无符号整数
    UNSIGNED_FLOAT, // 无符号浮点数

    // C语言32个关键字
	MAIN, VOLATILE, STRUCT, TYPEDEF, UNION, AUTO, EXTERN, UNSIGNED, SIGNED, STATIC, SIZEOF,
	CONST, ENUM, CHAR, LONG, FLOAT, DOUBLE, SHORT, INT, DEFAULT, CASE, SWITCH, CONTINUE, BREAK,
	GOTO, ELSE, IF, VOID, RETURN, DO, WHILE, FOR,

    // 运算符
	ADD, MINUS, INC, DEC, MULTI, DIV, MOD, MOD_ASSIGN, ADD_ASSGIN, MINUS_ASSIGN, DIV_ASSIGN, MULTI_ASSIGN,
    EQ, NE, GT, LT, GE, LE, ASSIGN, POINTER, L_SHIFT, R_SHIFT, EXP, AND, OR, NOT, ANDAND, OROR,

    // 分界符
    L_BRACKET, R_BRACKET, L_BRACE, R_BRACE,
	SEMIC,COLON,DOT,COMMA
}
