package com.syf;

import java.util.ArrayList;

public class Const {

	final static String[] category = new String[]{"ID","String",
			"Unsigned_int","Unsigned_float"};
	
	static ArrayList<String> constString = new ArrayList<>();
	public static int install_string(String token){
		int size = constString.size();
		for(int i=0; i<size; i++){
			if(constString.get(i).equals(token)){
				return 5;
			}
		}
		constString.add(token);
		return 5;
	}

	static ArrayList<Integer> unsigned_int = new ArrayList<Integer>();
	public static int install_unsigned_int(String token){
		int num = Integer.parseUnsignedInt(token);
		//System.out.println(num);
		//
		int size = unsigned_int.size();
		for(int i=0; i<size; i++){
			if(unsigned_int.get(i).intValue() == num){
				return 2;
			}
		}
		unsigned_int.add(num);
		return 2;
	}
	
	static ArrayList<Float> unsigned_float = new ArrayList<Float>();
	public static int install_unsigned_float(String token){
		float num = Float.parseFloat(token);
		
		int size = unsigned_float.size();
		for(int i=0; i<size; i++){
			if(Math.abs((unsigned_float.get(i).floatValue() - num)) < 0.00001)
				return 3;
		}
		
		unsigned_float.add(num);
		return 3;
	}
	
	// װ�ر�ʶ����
	static ArrayList<String> identifier = new ArrayList<>();
	public static int install_id(String token){
		// ����ǹؼ��֣���������0
		Code[] code = Code.values();
		for(Code c: code){
			if(c.toString().equals(token.toUpperCase()))
				return c.ordinal();
		}
		// �����ʶ���Ѿ��ڷ��ű��У�����
		int size = identifier.size();
		for(int i=0; i<size; i++){
			if(identifier.get(i).equals(token)){
				return 4;
			}
		}
		
		identifier.add(token);
		return 4;
		
	}
}
