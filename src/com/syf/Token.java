package com.syf;

public class Token {
	private String name;
	private int code;
	private int index;
	Token(int code, String name){
		this.name = name;
		this.code = code;
	}
	
	public String getName(){
		return this.name;
	}
	
	public int getCode(){
		return this.code;
	}
	
	public int getIndex(){
		return this.index;
	}
	
	public void setIndex(int index){
		this.index = index;
	}
	
	@Override
	public String toString(){
		return "("+ code + "," + name + ")";
	}
}
