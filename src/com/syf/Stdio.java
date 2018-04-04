package com.syf;
import java.io.*;
import java.util.*;

/**
* @file Stdio.java
* @CopyRight (C) https://github.com/Sngunfei
* @author syfnico
* @email syfnico@foxmail.com
* @date 2018-3-31
*/

public class Stdio {
	
	private ArrayList<Character> buffer = new ArrayList<Character>();
	
	public ArrayList<Character> getBuffer(){
		return this.buffer;
	}
	
	public void readFile(String fileName){
		FileReader fileInput = null;
		File file;
		try{
			file = new File(fileName);
			if(!file.exists())
				file.createNewFile();
			fileInput = new FileReader(file);
			char[] buffer = new char[4096];
			int charNum;
			while((charNum = fileInput.read(buffer))!=-1){
				for(int i=0; i<charNum; i++){
					//System.out.print(buffer[i]);
					this.buffer.add(new Character(buffer[i]));
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{
				if(fileInput!=null){
					fileInput.close();
				}
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	

	public static void main(String[] args) {
		Stdio test = new Stdio();
		String filename = "F:\\123.c";
		test.readFile(filename);

	}

}
