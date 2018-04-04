package com.syf;
import java.util.*;
import java.lang.StringBuilder;

/**
* @file Scanner.java
* @CopyRight (C) https://github.com/Sngunfei
* @brief
* @author syfnico
* @email syfnico@foxmail.com
* @date 2018-3-31
*/

public class Scanner {
	
	private ArrayList<Character> buffer;
	private int tail = 0;
	private char ch;

	public boolean isEnd(){
		return tail < buffer.size();
	}

	public Scanner(ArrayList<Character> buffer){
		this.buffer = buffer;
	}
	
	public char getchar(){
		return buffer.get(tail++).charValue();
	}
	
	public void retract(){
		this.tail--;
		this.ch = ' ';
	}
	
	public boolean isAlpha(char ch){
		return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
	}
	
	public boolean isDigit(char ch){
		return ch >= '0' && ch <= '9';
	}
	
	public boolean isAlnum(char ch){
		return isAlpha(ch) || isDigit(ch);
	}

	public Token token_scan(){
		StringBuilder sb = new StringBuilder();
		if(tail == buffer.size())
			return null;
		ch = getchar();
		if(tail == buffer.size())
			return null;
		while(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'){
			ch = getchar();
			if(tail == buffer.size())
				return null;
		}
		if(isAlpha(ch)){
			sb.append(ch);
			ch = getchar();
			while(isAlnum(ch)){
				sb.append(ch);
				ch = getchar();
			}
			retract();
			String name = sb.toString();
			return new Word(name);
		}else if(isDigit(ch)){
			sb.append(ch);
			ch = getchar();
			while(isDigit(ch)){
				sb.append(ch);
				ch = getchar();
			}
			if(ch == '.'){
				sb.append(ch);
				ch = getchar();
				while(isDigit(ch)){
					sb.append(ch);
					ch = getchar();
				}
				retract();
				float num = Float.parseFloat(sb.toString());
				return new Unsigned_Float(num);
			}
			retract();
			int num = Integer.parseInt(sb.toString());
			return new Unsigned_Int(num);
		}else 
			switch(ch){
				case '*':sb.append(ch);
						 ch = getchar();	
						 if(ch == '*'){
							 return new Token(Code.EXP.ordinal());
						 }else if(ch == '='){
							 return new Token(Code.MULTI_ASSIGN.ordinal());
						 }else{
							 retract();
							 return new Token(Code.MULTI.ordinal());
						 }
				case '+':sb.append(ch);
						 ch = getchar();
						 if(ch == '+'){
							 return new Token(Code.INC.ordinal());
						 }else if(ch == '='){
							 return new Token(Code.ADD_ASSGIN.ordinal());
						 }else{
							 retract();
							 return new Token(Code.ADD.ordinal());
						 }
				case '-':sb.append(ch);
						 ch = getchar();
						 if(ch == '-'){
							 return new Token(Code.DEC.ordinal());
						 }else if(ch == '='){
							 return new Token(Code.MINUS_ASSIGN.ordinal());
						 }else if(ch == '>'){
							 return new Token(Code.POINTER.ordinal());
						 }else{
							 retract();
							 return new Token(Code.MINUS.ordinal());
						 }
				case '=':sb.append(ch);
						 ch = getchar();
						 if(ch == '='){
							 return new Token(Code.EQ.ordinal());
						 }else{
							 retract();
							 return new Token(Code.ASSIGN.ordinal());
						 }
				case '>':sb.append(ch);
						 ch = getchar();
						 if(ch == '>'){
							 return new Token(Code.R_SHIFT.ordinal());
						 }else if(ch == '='){
							 return new Token(Code.GE.ordinal());
						 }else{
							 retract();
							 return new Token(Code.GT.ordinal());
						 }
				case '<':sb.append(ch);
						 ch = getchar();
						 if(ch == '>'){
							 return new Token(Code.NE.ordinal());
						 }else if(ch == '<'){
							 return new Token(Code.L_SHIFT.ordinal());
						 }else if(ch == '='){ 
							 return new Token(Code.LE.ordinal());
						 }else{
							 retract();
							 return new Token(Code.LT.ordinal());
						 }
				case '&':sb.append(ch);
						 ch = getchar();
						 if(ch == '&'){
							 // && 
							return new Token(Code.ANDAND.ordinal());
						 }
						 retract();
						 return new Token(Code.AND.ordinal());
				case '|':sb.append(ch);
						 ch = getchar();
						 if(ch == '|'){
							 // ||
							 return new Token(Code.OROR.ordinal());
						 }
						 retract();
						 return new Token(Code.OR.ordinal());
				case '(':sb.append(ch);
						 return new Token(Code.L_BRACKET.ordinal());
				case ')':sb.append(ch);
						 return new Token(Code.R_BRACKET.ordinal());
				case '{':sb.append(ch);
						 return new Token(Code.L_BRACE.ordinal());
				case '}':sb.append(ch);
						 return new Token(Code.R_BRACE.ordinal());
				case '/':sb.append(ch);
						 ch = getchar();
						 if(ch == '='){
							 return new Token(Code.DIV_ASSIGN.ordinal());
						 }else if(ch == '/'){
							 ch = getchar();
							 while(ch != '\n')
								 ch = getchar();
							 return null;
						 }else if(ch == '*'){
						 	while(true){
						 		ch = getchar();
						 		while(ch != '*')
									ch = getchar();
						 		ch = getchar();
						 		if(ch == '/')
						 			return null;
							 }
						 }else {
							 retract();
							 return new Token(Code.DIV.ordinal());
						 }
				case '#':sb.append(ch);
						 ch = getchar();
						 while(ch != '\n')
						 	ch = getchar();
						 return null;
				case ',':sb.append(ch);
						 return new Token(Code.COMMA.ordinal());
				case ':':sb.append(ch);
						 ch = getchar();
						 if(ch == '='){
							 return new Token(Code.ASSIGN.ordinal());
						 }break;
				case ';':sb.append(ch);
						 return new Token(Code.SEMIC.ordinal());
				case '!':sb.append(ch);
						 return new Token(Code.NOT.ordinal());
				case '"':sb.append(ch);
						 ch = getchar();
						 while(ch != '"'){
							 sb.append(ch);
							 ch = getchar();
						 }
						 sb.append(ch);
						 String lexme = sb.toString();
						 return new Const_String(lexme);
			}
			return null;
	}
	
//	private ArrayList<Token> scanner(Stdio scan, String fileName){
//		scan.readFile(fileName);
//		this.buffer = scan.getBuffer();
//		//System.out.println(buffer.size());
//		this.tail = 0;
//		ArrayList<Token> tokens = new ArrayList<>();
//		Token token = token_scan();
//		while(tail < buffer.size()){
//			//System.out.println(token.getSymbol());
//			tokens.add(token);
//			token = token_scan();
//		}
//		return tokens;
//	}
//
//	public static void main(String[] args) {
//		Scanner test = new Scanner();
//		Stdio scan = new Stdio();
//		String fileName = "F:\\123.c";
//		ArrayList<Token> tokens = test.scanner(scan, fileName);
//	}
}