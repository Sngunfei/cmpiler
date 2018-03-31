package com.syf;
import java.util.*;
import java.lang.StringBuilder;

public class Scanner {
	
	private ArrayList<Character> buffer = null;
	private int head = 0;
	private int tail = 0;
	private char ch;
	
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
			return new Token(-1, "####");
		ch = getchar();
		if(tail == buffer.size())
			return new Token(-1, "####");
		while(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'){
			ch = getchar();
			if(tail == buffer.size())
				return new Token(-1, "####");
			head++;
		}
		if(isAlpha(ch)){
			sb.append(ch);
			ch = getchar();
			while(isAlnum(ch)){
				sb.append(ch);
				ch = getchar();
			}
			retract();
			String str = sb.toString();
			return new Token(Const.install_id(str), str);
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
				String str = sb.toString();
				return new Token(Const.install_unsigned_float(str),str);
			}
			retract();
			String str = sb.toString();
			//System.out.println(str);
			return new Token(Const.install_unsigned_int(str), str);
		}else 
			switch(ch){
				case '*':sb.append(ch);
						 ch = getchar();	
						 if(ch == '*'){
							 return new Token(Code.EXP.ordinal(), "EXP");
						 }else if(ch == '='){
							 return new Token(Code.MULTI_ASSIGN.ordinal(), "MULTI-ASSIGN");
						 }else{
							 retract();
							 return new Token(Code.MULTI.ordinal(), "MULTI");
						 }
				case '+':sb.append(ch);
						 ch = getchar();
						 if(ch == '+'){
							 return new Token(Code.INC.ordinal(), "INC");
						 }else if(ch == '='){
							 return new Token(Code.ADD_ASSGIN.ordinal(), "ADD-ASSIGN");
						 }else{
							 retract();
							 return new Token(Code.ADD.ordinal(), "ADD");
						 }
				case '-':sb.append(ch);
						 ch = getchar();
						 if(ch == '-'){
							 return new Token(Code.DEC.ordinal(), "DEC");
						 }else if(ch == '='){
							 return new Token(Code.MINUS_ASSIGN.ordinal(), "MINUS-ASSIGN");
						 }else if(ch == '>'){
							 return new Token(Code.POINTER.ordinal(), "POINTER");
						 }else{
							 retract();
							 return new Token(Code.MINUS.ordinal(), "MINUS"); 
						 }
				case '=':sb.append(ch);
						 ch = getchar();
						 if(ch == '='){
							 return new Token(Code.EQ.ordinal(), "EQ");
						 }else{
							 retract();
							 return new Token(Code.ASSIGN.ordinal(), "ASSIGN");
						 }
				case '>':sb.append(ch);
						 ch = getchar();
						 if(ch == '>'){
							 return new Token(Code.R_SHIFT.ordinal(), "R-SHITF");
						 }else if(ch == '='){
							 return new Token(Code.GE.ordinal(), "GE");
						 }else{
							 retract();
							 return new Token(Code.GT.ordinal(), "GT");
						 }
				case '<':sb.append(ch);
						 ch = getchar();
						 if(ch == '>'){
							 return new Token(Code.NE.ordinal(),"NE");
						 }else if(ch == '<'){
							 return new Token(Code.L_SHIFT.ordinal(),"L-SHIFT");
						 }else if(ch == '='){ 
							 return new Token(Code.LE.ordinal(),"LE");
						 }else{
							 retract();
							 return new Token(Code.LT.ordinal(),"LT");
						 }
				case '&':sb.append(ch);
						 ch = getchar();
						 if(ch == '&'){
							 // && 
							return new Token(Code.ANDAND.ordinal(),"&&");
						 }
						 retract();
						 return new Token(Code.AND.ordinal(), "&");
				case '|':sb.append(ch);
						 ch = getchar();
						 if(ch == '|'){
							 // ||
							 return new Token(Code.OROR.ordinal(), "||");
						 }
						 retract();
						 return new Token(Code.OR.ordinal(), "|");
				case '(':sb.append(ch);
						 return new Token(Code.L_BRACKET.ordinal(), "L-bracket");
				case ')':sb.append(ch);
						 return new Token(Code.R_BRACKET.ordinal(), "R-bracket");
				case '{':sb.append(ch);
						 return new Token(Code.L_BRACE.ordinal(), "L-brace");
				case '}':sb.append(ch);
						 return new Token(Code.R_BRACE.ordinal(), "R-brace");
				case '/':sb.append(ch);
						 ch = getchar();
						 if(ch == '='){
							 return new Token(Code.DIV_ASSIGN.ordinal(), "DIV-ASSIGN");
						 }else if(ch == '/'){
							 // ��ע��
							 ch = getchar();
							 while(ch != '\n')
								 ch = getchar();
							 return new Token(0, "COMMENT");
						 }else if(ch == '*'){
					  Loop:	 while(true){
						     ch = getchar();
							 while(ch != '*')
								 ch = getchar();
							 ch = getchar();
							 if(ch == '/')
								 return new Token(0, "COMMENT");
							 else
								 continue Loop;
							 }
						 }else{
							 retract();
							 return new Token(Code.DIV.ordinal(), "DIV");
						 }
				case ',':sb.append(ch);
						 return new Token(Code.COMMA.ordinal(), "COMMA");
				case ':':sb.append(ch);
						 ch = getchar();
						 if(ch == '='){
							 return new Token(Code.ASSIGN.ordinal(), "ASSIGN");
						 }break;
				case ';':sb.append(ch);
						 return new Token(Code.SEMIC.ordinal(), "SEMIC");
				case '!':sb.append(ch);
						 return new Token(Code.NOT.ordinal(), "NOT");
				case '#':sb.append(ch);
						 ch = getchar();
						 while(ch != '\n')
							 ch = getchar();
						 return new Token(0, "MACRO");
				case '"':sb.append(ch);
						 ch = getchar();
						 while(ch != '"'){
							 sb.append(ch);
							 ch = getchar();
						 }
						 sb.append(ch);
						 String str = sb.toString();
						 return new Token(Const.install_string(str),str);
			}
		return new Token(-1, "ERROR");
	}
	
	public ArrayList<Token> scanner(Stdio scan, String fileName){
		scan.readFile(fileName);
		this.buffer = scan.getBuffer();
		//System.out.println(buffer.size());
		this.head = this.tail = 0;
		ArrayList<Token> tokens = new ArrayList<Token>();
		Token token = token_scan();
		while(token.getCode() != -1){
			//System.out.println(token.getSymbol());
			tokens.add(token);
			token = token_scan();
		}
		return tokens;
	}

	public static void main(String[] args) {
		Scanner test = new Scanner();
		Stdio scan = new Stdio();
		String fileName = "F:\\123.c";
		ArrayList<Token> tokens = test.scanner(scan, fileName);
		for(int i=0; i<tokens.size(); i++){
			String attr = Const.category[tokens.get(i).getCode()];
			String str  = "(" + attr + ", " + tokens.get(i).getName() + ")";
			System.out.println(str);
		}
	}
}
