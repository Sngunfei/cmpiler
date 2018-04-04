package com.syf;

import java.util.ArrayList;

public class Const {

	// 符号表
	static ArrayList<ArrayList> Table = new ArrayList<>();
	// 字符串常量
	static ArrayList<String> Strings = new ArrayList<>();
	// 无符号整数
	static ArrayList<Integer> unsigned_int = new ArrayList<Integer>();
	// 无符号浮点数
	static ArrayList<Float> unsigned_float = new ArrayList<Float>();

	static{
		Table.add(Strings);
		Table.add(unsigned_int);
		Table.add(unsigned_float);
	}

	/*
	 *	 添加字符串常量，返回索引值
	 */
	public static int install_string(String token){
		int size = Strings.size();
		for(int i=0; i<size; i++){
			if(Strings.get(i).equals(token)){
				return i;
			}
		}
		Strings.add(token);
		return size;
	}

	/*
	 *	添加无符号整数，返回索引
	 */


	/*
	 *	添加无符号浮点数，返回索引
	 */


	static ArrayList<String> id = new ArrayList<>();

}
