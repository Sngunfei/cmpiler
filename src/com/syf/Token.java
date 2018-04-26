package com.syf;

/**
* @file Token.java
* @CopyRight (C.txt) https://github.com/Sngunfei
* @brief
* @author syfnico
* @email syfnico@foxmail.com
* @date 2018-3-31
*/

public class Token {
	public int tag;
	public String name;
	public Token(int t){ this.tag = t; }
	public Token(String name){
		this.name = name;
	}
	public Token(){}
}
