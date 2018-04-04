package com.syf;

public class Parse {

    // 添加字符串常量，返回索引值
    private int install_string(String token){
        int size = Const.Strings.size();
        for(int i=0; i<size; i++){
            if(Const.Strings.get(i).equals(token)){
                return i;
            }
        }
        Const.Strings.add(token);
        return size;
    }

    // 添加无符号整数
    private int install_unsigned_int(String number){
        int num = Integer.parseUnsignedInt(number);
        int size = Const.unsigned_int.size();
        for(int i=0; i<size; i++){
            if(Const.unsigned_int.get(i).intValue() == num){
                return i;
            }
        }
        Const.unsigned_int.add(num);
        return size;
    }

    // 添加无符号浮点数
    private int install_unsigned_float(String token){
        float num = Float.parseFloat(token);
        int size = Const.unsigned_float.size();
        for(int i=0; i<size; i++){
            if(Math.abs((Const.unsigned_float.get(i).floatValue() - num)) < 0.00001)
                return i;
        }
        Const.unsigned_float.add(num);
        return 3;
    }

    // 添加标识符
    private int install_id(String token){
        Code[] code = Code.values();
        for(Code c: code){
            if(c.toString().equals(token.toUpperCase()))
                return c.ordinal();
        }

        int size = Const.id.size();
        for(int i=0; i<size; i++){
            if(Const.id.get(i).equals(token)){
                return i;
            }
        }

        Const.id.add(token);
        return size;

    }
    // LR(1)文法
    public void parser(String filename){
        Stdio stdio =  new Stdio();
        stdio.readFile(filename);
        Scanner scan = new Scanner(stdio.getBuffer());

        while(!scan.isEnd()){
            Token token = scan.token_scan();
            if(token == null)
                continue;
            // 如果token是id
            if(token.tag == Code.ID.ordinal()){
                Word word = (Word)token;
            }
        }
    }


	public static void main(String[] args) {
        Parse parse = new Parse();
        String filename = "F:\\123.c";
        parse.parser(filename);

	}

}
