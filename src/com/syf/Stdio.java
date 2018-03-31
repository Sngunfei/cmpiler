package com.syf;
import java.io.*;
import java.util.*;

public class Stdio {
	
	private ArrayList<Character> buffer = new ArrayList<Character>();
	
	public ArrayList<Character> getBuffer(){
		return this.buffer;
	}
	
	public void readFile(String fileName){
		FileReader fileInput = null;
		File file = null;
		try{
			file = new File(fileName);
			if(!file.exists())
				file.createNewFile();
			fileInput = new FileReader(file);
			char[] buffer = new char[4096];
			int charnum = 0;
			while((charnum = fileInput.read(buffer))!=-1){
				//System.out.println(charnum);
				for(int i=0; i<charnum; i++){
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
