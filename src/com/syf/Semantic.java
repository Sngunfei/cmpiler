package  com.syf;
import java.util.ArrayList;


class Semantic{
public ArrayList<Record> symbolTable; //全局符号表
public ArrayList<ConstRecord> constTable; //常量表
public ArrayList<Record> curSymbolTable = symbolTable; //当前使用的符号表
public Record curRecord; //当前使用的表项
public ArrayList<String> interCode = new ArrayList<>(); //中间代码
public int type;
public int offset;
public int arr;
public ExpAttribute arr1;
public int quad = 1; //行号
        ArrayList<Integer> arg = new ArrayList<>();
        Stack<ExpAttribute> exp = new Stack<>();
        Stack<ExpAttribute> isnull_exp = new Stack<>();
        Stack<ExpAttribute> var = new Stack<>();
        ExpAttribute exp1 = new ExpAttribute();
        ExpAttribute exp2 = new ExpAttribute();
        ExpAttribute exp3 = new ExpAttribute();
        ExpAttribute exp4 = new ExpAttribute();
        ExpAttribute exp5 = new ExpAttribute();
        ExpAttribute exp6 = new ExpAttribute();
        ExpAttribute value = new ExpAttribute();
        Stack<ExpAttribute> con = new Stack<>();
        Stack<Integer> truequad = new Stack<>(); //真出口行号
        Stack<Integer> falsequad = new Stack<>(); //假出口行号
        Stack<Integer> blockquad = new Stack<>(); //块起始行号
        Stack<Integer> breakquad = new Stack<>(); //break语句行号
        Stack<Integer> continuequad = new Stack<>(); //continue语句行号
public int tmpquad; //for语句分析时“i++”位置的行号
public int tempNum = 0; //申请临时变量的编号
public int argIndex = 0; //调用函数时参数index

class ExpAttribute{
    public int addr;
    public String temp = null;
    public int typeOfTable;
    public ExpAttribute index = null;
    public int logicValue = -1;
    public String getName(){
        if(temp!=null) return temp;
        if(typeOfTable==0){
            Record r = curSymbolTable.get(addr);
            if(index == null) return r.name;
            else return r.name+"["+index.getName()+"]";
        }
        if(typeOfTable==1){
            ConstRecord cr = constTable.get(addr);
            return cr.value;
        }
        return null;
    }
    public ExpAttribute clone(){
        ExpAttribute tmp = new ExpAttribute();
        tmp.addr = this.addr;
        tmp.temp = this.temp;
        tmp.typeOfTable = this.typeOfTable;
        if(this.index==null) tmp.index=null;
        else tmp.index = this.index.clone();
        tmp.logicValue = -1;
        return tmp;
    }
}

    public String getNewTemp(){
        String tmp = "t"+tempNum;
        tempNum++;
        return tmp;
    }
    //func -> type ID m ( args ) m1 block
    public void func(){
        curSymbolTable = symbolTable;
        lex.curSymbolTable = symbolTable;
    }
    //m1 -> $
    public void m(Token ID){
        arg = new ArrayList<>();
        offset = 0;
        Record record = curSymbolTable.get(ID.value);
        record.recordType = 2;
        record.type = type;
        record.nextSymbolTable = new ArrayList<>();
        record.beginquad = quad;
        curSymbolTable = record.nextSymbolTable;
        lex.curSymbolTable = curSymbolTable;
        curRecord = record;

        funcFlag.put(quad,record.name+": ");
        System.out.println(record.name + ":");
    }
    //m1 -> $
    public void m1(Token ID){
        curRecord.args = arg;
    }

    //type -> VOID
    public void type_VOID(){
        type=Tag.VOID;
    }
    //type -> INT
    public void type_INT(){
        type=Tag.INT;
    }
    //type -> CHAR
    public void type_CHAR(){
        type=Tag.CHAR;
    }
    //type -> FLOAT
    public void type_FLOAT(){
        type=Tag.FLOAT;
    }
    //n->$
    public void n(Token ID){
        Record record = curSymbolTable.get(ID.value);
        record.recordType = 0;
        record.type = type;
        record.isArgs = 1;
        if(type==Tag.INT) while(offset%4!=0) offset++;
        if(type==Tag.FLOAT) while(offset%8!=0) offset++;
        record.offset = offset;
        if(type==Tag.INT) offset += 4;
        if(type==Tag.FLOAT) offset += 8;
        if(type==Tag.CHAR) offset += 1;
        arg.add(type);
    }
    //args->$
    public void args_$(){
        arg = null;
    }

    //m13->$
    public void m13(Token ID){
        Record record = curSymbolTable.get(ID.value);
        //symbolTable.set(ID.value,null);
        record.recordType = 1;
        record.type = type;
        curRecord = record;
    }
    //m2 -> $
    public void m2() {
        offset = 0 ;
    }
    //m3 -> $
    public void m3(Token ID){
        Record record = curSymbolTable.get(ID.value);
        //symbolTable.set(ID.value,null);
        record.recordType = 0;
        record.type = type;
        if(type==Tag.INT) while(offset%4!=0) offset++;
        if(type==Tag.FLOAT) while(offset%8!=0) offset++;
        record.offset = offset;
        if(type==Tag.INT) offset += 4;
        if(type==Tag.FLOAT) offset += 8;
        if(type==Tag.CHAR) offset += 1;
    }
    //m4 -> $
    public void m4(){
        curRecord.arraySize=arr;
        if(type==Tag.INT) while(offset%4!=0) offset++;
        if(type==Tag.FLOAT) while(offset%8!=0) offset++;
        curRecord.offset = offset;
        if(type==Tag.INT) offset += 4*arr;
        if(type==Tag.FLOAT) offset += 8*arr;
        if(type==Tag.CHAR) offset += 1*arr;
    }
    //m5 -> $
    public void m5(Token num){
        ConstRecord cr = constTable.get(num.value);
        arr = Integer.valueOf(cr.value);
    }
    //arr -> [ NUM m5 ]

    //arr1 -> [ exp ]
    public void arr1_exp(){
        arr1 = exp.pop();
    }

    //jump_sen -> CONTINUE ;
    public void CONTINUE(){
        continuequad.pop();
        continuequad.add(quad);
        interCode.add(quad + ": goto ");
        System.out.println(quad + ": goto ");
        quad++;
    }
    //jump_sen -> BREAK ;
    public void BREAK(){
        breakquad.pop();
        breakquad.add(quad);
        interCode.add(quad + ": goto ");
        System.out.println(quad + ": goto ");
        quad++;
    }
    //jump_sen -> RETURN isnull_exp ;
    public void RETURN(){
        ExpAttribute tmp = isnull_exp.pop();
        if(tmp!=null) {
            interCode.add(quad + ": F = " + tmp.getName());
            quad++;
        }
        interCode.add(quad + ": ret");
        quad++;
    }

    //isnull_exp -> exp
    public void isnull_exp_exp(){
        isnull_exp.add(exp.pop());
    }
    //isnull_exp -> $
    public void isnull_exp_$(){
        isnull_exp.add(null);
    }
    //iteration_sen -> WHILE m9 ( exp ) m7 block_sen
    public void WHILE(){
        int tmp3 = blockquad.pop();
        interCode.add(quad + ": goto " + tmp3);
        quad++;
        //int tmp = falsequad.pop();
        int tmp1 = truequad.pop()-2;
        interCode.set(tmp1,(tmp1+1)+": goto "+(quad));

        Integer b = breakquad.pop();
        if(b!=null){
            interCode.set(b-1,(b)+": goto "+(quad));
        }
        int i = breakquad.size() - blockquad.size();
        for(int j=0;j<i;j++) breakquad.pop();

        Integer c = continuequad.pop();
        if(c!=null){
            interCode.set(c-1,(c)+": goto "+(tmp3));
        }
        i = continuequad.size() - blockquad.size();
        for(int j=0;j<i;j++) continuequad.pop();

    }

    //iteration_sen -> FOR ( isnull_exp ; m9 isnull_exp ; m12 isnull_exp ) m11 block_sen
    public void FOR(){
        interCode.add(quad + ": goto " + (tmpquad+1));
        quad++;
        //int tmp = falsequad.pop();
        int tmp1 = truequad.pop()-2;
        interCode.set(tmp1,(tmp1+1)+": goto "+(quad));
        int tmp3 = blockquad.pop();

        Integer b = breakquad.pop();
        if(b!=null){
            interCode.set(b-1,(b)+": goto "+(quad));
        }
        int i = breakquad.size() - blockquad.size();
        for(int j=0;j<i;j++) breakquad.pop();

        Integer c = continuequad.pop();
        if(c!=null){
            interCode.set(c-1,(c)+": goto "+(tmp3));
        }
        i = continuequad.size() - blockquad.size();
        for(int j=0;j<i;j++) continuequad.pop();
    }

    //m12->$
    public void m12(){
        tmpquad = quad;
        interCode.add(quad + ": goto ");
        quad++;
    }

    //iteration_sen -> DO m9 block_sen WHILE ( exp ) ;
    public void DO(){
        quad += 1;
        String tmp =  exp.pop().getName();
        interCode.add((quad - 1) +": if "+ tmp + " = 1 goto " + blockquad.peek());
        System.out.println((quad - 1)+": if "+ tmp + " = 1 goto " + blockquad.peek());
        int tmp3 = blockquad.pop();

        Integer b = breakquad.pop();
        if(b!=null){
            interCode.set(b-1,(b)+": goto "+(quad));
        }
        int i = breakquad.size() - blockquad.size();
        for(int j=0;j<i;j++) breakquad.pop();

        Integer c = continuequad.pop();
        if(c!=null){
            interCode.set(c-1,(c)+": goto "+(tmp3));
        }
        i = continuequad.size() - blockquad.size();
        for(int j=0;j<i;j++) continuequad.pop();
    }

    //m11->$
    public void m11(){
        int tmp3 = blockquad.peek();
        interCode.add(quad + ": goto " + tmp3);
        quad++;
        interCode.set(tmpquad-1,(tmpquad)+": goto "+(quad));
        isnull_exp.pop();
        ExpAttribute exptmp = isnull_exp.pop();
        if(exptmp==null){
            exptmp = new ExpAttribute();
            exptmp.temp = "1";
        }
        quad += 2;
        truequad.add(quad);
        String tmp =  exptmp.getName();
        interCode.add((quad - 2) +": if "+ tmp + " = 1 goto " + truequad.peek());
        interCode.add((quad - 1) + ": goto ");
    }

    //m10->$
    public void m10(){
        interCode.add(quad + ": goto ");
        System.out.println(quad+": goto ");
        quad++;
    }

    //branch_sen -> IF m9 ( exp ) m7 block_sen m10 branch_sen'
    public void IF(){
        int tmp = falsequad.pop();
        int tmp1 = truequad.pop()-2;
        interCode.set(tmp1,(tmp1+1)+": goto "+tmp);
        System.out.println((tmp1+1) + ": goto "+tmp);

        interCode.set(tmp-2,(tmp-1)+": goto "+quad);
        System.out.println((tmp-1) + ": goto "+quad);
        blockquad.pop();
        if(breakquad.peek()==null) breakquad.pop();
        if(continuequad.peek()==null) continuequad.pop();
    }

    //m7->$
    public void m7(){
        quad += 2;
        truequad.add(quad);
        String tmp =  exp.pop().getName();
        interCode.add((quad - 2) +": if "+ tmp + " = 1 goto " + truequad.peek());
        interCode.add((quad - 1) + ": goto ");
    }

    //m9->$
    public void m9(){
        continuequad.add(null);
        breakquad.add(null);
        blockquad.add(quad);
    }

    //m8->$
    public void m8(){
        falsequad.add(quad);
    }
    //branch_sen' -> $
    public void branch_sen1_$(){
        falsequad.add(quad);
    }

    //exp -> var = exp1
    public void exp_var_assign_exp1(){
        interCode.add(quad + ": "+ var.peek().getName() + " = " + exp1.getName());
        exp.add(var.peek().clone());
        var.pop();
        quad++;
    }

    //exp -> exp1
    public void exp_exp1(){
        exp.add(exp1.clone());
    }

    //m6->$
    public void m6(Token ID){
        Record r = curSymbolTable.get(ID.value);
        ExpAttribute tmp = new ExpAttribute();
        tmp.addr = ID.value;
        var.add(tmp);
        if(r.type==0){
            System.out.println(r.name +"无定义！");
        }
    }

    // var -> ID m6 arr1
    public void var_ID_arr1(){
        ExpAttribute tmp = var.pop();
        tmp.typeOfTable=0;
        tmp.index = arr1;
        var.add(tmp);
    }

    //var -> ID
    public void var_ID(Token ID){
        Record r = curSymbolTable.get(ID.value);
        ExpAttribute tmp = new ExpAttribute();
        tmp.addr = ID.value;
        tmp.typeOfTable=0;
        var.add(tmp);
        if(r.type==0){
            System.out.println(r.name +"无定义！");
        }
    }

    //exp1 -> exp1 || exp2
    public void exp1_exp1_or_exp2(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp1.getName() + " | " + exp2.getName());
        exp1.temp = tmp;
        quad++;
    }

    //exp1 -> exp2
    public void exp1_exp2(){
        exp1 = exp2;
    }

    //exp2 -> exp2 && exp3
    public void exp2_exp2_and_exp3(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp2.getName() + " & " + exp3.getName());
        exp2.temp = tmp;
        quad++;
    }

    //exp2 -> exp3
    public void exp2_exp3(){
        exp2 = exp3;
    }

    //exp3 -> exp3 > exp4
    public void exp3_exp3_g_exp4(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ "if "+exp3.getName() + " > " + exp4.getName() +" goto " + (quad+3));
        interCode.add((quad+1) + ": "+ tmp +" = 0");
        interCode.add((quad+2) + ": "+ "goto " + (quad+4));
        interCode.add((quad+3) + ": "+ tmp +" = 1");
        exp3.temp = tmp;
        quad += 4;
    }

    //exp3 -> exp3 >= exp4
    public void exp3_exp3_ge_exp4(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ "if "+exp3.getName() + " >= " + exp4.getName() +" goto " + (quad+3));
        interCode.add((quad+1) + ": "+ tmp +" = 0");
        interCode.add((quad+2) + ": "+ "goto " + (quad+4));
        interCode.add((quad+3) + ": "+ tmp +" = 1");
        exp3.temp = tmp;
        quad += 4;
    }

    //exp3 -> exp3 < exp4
    public void exp3_exp3_l_exp4(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ "if "+exp3.getName() + " < " + exp4.getName() +" goto " + (quad+3));
        interCode.add((quad+1) + ": "+ tmp +" = 0");
        interCode.add((quad+2) + ": "+ "goto " + (quad+4));
        interCode.add((quad+3) + ": "+ tmp +" = 1");
        exp3.temp = tmp;
        quad += 4;
    }

    //exp3 -> exp3 <= exp4
    public void exp3_exp3_le_exp4(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ "if "+exp3.getName() + " <= " + exp4.getName() +" goto " + (quad+3));
        interCode.add((quad+1) + ": "+ tmp +" = 0");
        interCode.add((quad+2) + ": "+ "goto " + (quad+4));
        interCode.add((quad+3) + ": "+ tmp +" = 1");
        exp3.temp = tmp;
        quad += 4;
    }

    //exp3 -> exp3 == exp4
    public void exp3_exp3_e_exp4(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ "if "+exp3.getName() + " = " + exp4.getName() +" goto " + (quad+3));
        interCode.add((quad+1) + ": "+ tmp +" = 0");
        interCode.add((quad+2) + ": "+ "goto " + (quad+4));
        interCode.add((quad+3) + ": "+ tmp +" = 1");
        exp3.temp = tmp;
        quad += 4;
    }

    //exp3 -> exp3 != exp4
    public void exp3_exp3_ue_exp4(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ "if "+exp3.getName() + " != " + exp4.getName() +" goto " + (quad+3));
        interCode.add((quad+1) + ": "+ tmp +" = 0");
        interCode.add((quad+2) + ": "+ "goto " + (quad+4));
        interCode.add((quad+3) + ": "+ tmp +" = 1");
        exp3.temp = tmp;
        quad += 4;
    }

    //exp3 -> exp4
    public void exp3_exp4(){
        exp3 = exp4;
    }

    //exp4 -> exp4 + exp5
    public void exp4_exp4_add_exp5(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp4.getName() + " + " + exp5.getName());
        System.out.println(quad + ": "+ tmp +" = "+exp4.getName() + " + " + exp5.getName());
        exp4.temp = tmp;
        quad++;
    }

    //exp4 -> exp4 - exp5
    public void exp4_exp4_minus_exp5(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp4.getName() + " - " + exp5.getName());
        System.out.println(quad + ": "+ tmp +" = "+exp4.getName() + " + " + exp5.getName());
        exp4.temp = tmp;
        quad++;
    }

    //exp4 -> exp5
    public void exp4_exp5(){
        exp4 = exp5;
    }

    //exp5 -> exp5 * exp6
    public void exp5_exp5_mul_exp6(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp5.getName() + " * " + exp6.getName());
        System.out.println(quad + ": "+ tmp +" = "+exp5.getName() + " * " + exp6.getName());
        exp5.temp = tmp;
        quad++;
    }

    //exp5 -> exp5 / exp6
    public void exp5_exp5_div_exp6(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp5.getName() + " / " + exp6.getName());
        System.out.println(quad + ": "+ tmp +" = "+exp5.getName() + " / " + exp6.getName());
        exp5.temp = tmp;
        quad++;
    }

    //exp5 -> exp5 % exp6
    public void exp5_exp5_mod_exp6(){
        String tmp = getNewTemp();
        interCode.add(quad + ": "+ tmp +" = "+exp5.getName() + " % " + exp6.getName());
        System.out.println(quad + ": "+ tmp +" = "+exp5.getName() + " % " + exp6.getName());
        exp5.temp = tmp;
        quad++;
    }

    //exp5 -> exp6
    public void exp5_exp6(){
        exp5 = exp6;
    }

    //exp6 -> ! value
    public void exp6_not_value(){
        interCode.add(quad + ": "+ exp6.getName() + " = ~" + value.getName());
        System.out.println(quad + ": "+ exp6.getName() + " = ~" + value.getName());
        quad++;
    }

    //exp6 -> value
    public void exp6_value(){
        exp6 = value;
    }

    //value -> ( exp )
    public void value_exp(){
        value = exp.pop();
    }

    //value -> var
    public void value_var(){
        value = var.pop();
    }

    //value -> const
    public void value_const(){
        value = con.pop();
    }

    //value -> ID m14 ( actual_args )
    public void value_ID_m14_actual_args(){
        for(int i=0;i<argIndex;i++)
        {
            interCode.add(quad + ": " +"param " + curRecord.nextSymbolTable.get(i).name);
            quad++;
        }
        argIndex=0;
        interCode.add(quad + ": " +"call " + curRecord.name+ ", "+curRecord.beginquad);
        quad++;
        String tmp = getNewTemp();
        interCode.add(quad + ": " +tmp + " = F");
        quad++;

    }

    //m14->$
    public void m14(Token ID){
        String tmp = curSymbolTable.get(ID.value).name;
        for(int i=1;i<symbolTable.size();i++){
            if(tmp.equals(symbolTable.get(i).name)){
                curRecord = symbolTable.get(i);
                break;
            }
        }
    }
    //m15->$
    public void m15(){
        String tmp = exp.pop().getName();
        interCode.add(quad + ": " +curRecord.nextSymbolTable.get(argIndex).name + " = "+tmp);
        quad++;
        argIndex++;
    }

    //const -> NUM
    public void const_NUM(Token num){
        ExpAttribute tmp = new ExpAttribute();
        tmp.typeOfTable=1;
        tmp.addr = num.value;
        con.add(tmp);
    }

    //const -> REAL
    public void const_REAL(Token real){
        ExpAttribute tmp = new ExpAttribute();
        tmp.typeOfTable=1;
        tmp.addr = real.value;
        con.add(tmp);
    }

    //const -> CHAR_CONST
    public void const_CHAR(Token ch){
        ExpAttribute tmp = new ExpAttribute();
        tmp.typeOfTable=1;
        tmp.addr = ch.value;
        con.add(tmp);
    }

    //const -> STR
    public void const_STR(Token str){
        ExpAttribute tmp = new ExpAttribute();
        tmp.typeOfTable=1;
        tmp.addr = str.value;
        con.add(tmp);
    }

    public void semantic(String prod, Token tmp){
        switch(prod){
            case "func -> type ID m ( args ) m1 block":
                func();
                break;
            case "m -> $":
                m(tmp);
                break;
            case "m1 -> $":
                m1(tmp);
                break;
            case "type -> VOID":
                type_VOID();
                break;
            case "type -> INT":
                type_INT();
                break;
            case "type -> FLOAT":
                type_FLOAT();
                break;
            case "type -> CHAR":
                type_CHAR();
                break;
            case "args -> $":
                args_$();
                break;
            case "n -> $":
                n(tmp);
                break;
            case "m13 -> $":
                m13(tmp);
                break;
            case "m2 -> $":
                m2();
                break;
            case "m3 -> $":
                m3(tmp);
                break;
            case "m4 -> $":
                m4();
                break;
            case "m5 -> $":
                m5(tmp);
                break;
            case "arr1 -> [ exp ]":
                arr1_exp();
                break;
            case "jump_sen -> CONTINUE ;":
                CONTINUE();
                break;
            case "jump_sen -> BREAK ;":
                BREAK();
                break;
            case "jump_sen -> RETURN isnull_exp ;":
                RETURN();
                break;
            case "isnull_exp -> exp":
                isnull_exp_exp();
                break;
            case "isnull_exp -> $":
                isnull_exp_$();
                break;
            case "iteration_sen -> WHILE m9 ( exp ) m7 block_sen":
                WHILE();
                break;
            case "iteration_sen -> FOR ( isnull_exp ; m9 isnull_exp ; m12 isnull_exp ) m11 block_sen":
                FOR();
                break;
            case "iteration_sen -> DO m9 block_sen WHILE ( exp ) ;":
                DO();
                break;
            case "branch_sen -> IF m9 ( exp ) m7 block_sen m10 branch_sen'":
                IF();
                break;
            case "m12 -> $":
                m12();
                break;
            case "m11 -> $":
                m11();
                break;
            case "m10 -> $":
                m10();
                break;
            case "m9 -> $":
                m9();
                break;
            case "m8 -> $":
                m8();
                break;
            case "m7 -> $":
                m7();
                break;
            case "exp -> var = exp1":
                exp_var_assign_exp1();
                break;
            case "exp -> exp1":
                exp_exp1();
                break;
            case "var -> ID m6 arr1":
                var_ID_arr1();
                break;
            case "var -> ID":
                var_ID(tmp);
                break;
            case "m6 -> $":
                m6(tmp);
                break;
            case "exp1 -> exp1 || exp2":
                exp1_exp1_or_exp2();
                break;
            case "exp1 -> exp2":
                exp1_exp2();
                break;
            case "exp2 -> exp2 && exp3":
                exp2_exp2_and_exp3();
                break;
            case "exp2 -> exp3":
                exp2_exp3();
                break;
            case "exp3 -> exp3 > exp4":
                exp3_exp3_g_exp4();
                break;
            case "exp3 -> exp3 >= exp4":
                exp3_exp3_ge_exp4();
                break;
            case "exp3 -> exp3 < exp4":
                exp3_exp3_l_exp4();
                break;
            case "exp3 -> exp3 <= exp4":
                exp3_exp3_le_exp4();
                break;
            case "exp3 -> exp3 == exp4":
                exp3_exp3_e_exp4();
                break;
            case "exp3 -> exp3 != exp4":
                exp3_exp3_ue_exp4();
                break;
            case "exp3 -> exp4":
                exp3_exp4();
                break;
            case "exp4 -> exp4 + exp5":
                exp4_exp4_add_exp5();
                break;
            case "exp4 -> exp4 - exp5":
                exp4_exp4_minus_exp5();
                break;
            case "exp4 -> exp5":
                exp4_exp5();
                break;
            case "exp5 -> exp5 * exp6":
                exp5_exp5_mul_exp6();
                break;
            case "exp5 -> exp5 / exp6":
                exp5_exp5_div_exp6();
                break;
            case "exp5 -> exp5 % exp6":
                exp5_exp5_mod_exp6();
                break;
            case "exp5 -> exp6":
                exp5_exp6();
                break;
            case "exp6 -> ! value":
                exp6_not_value();
                break;
            case "exp6 -> value":
                exp6_value();
                break;
            case "value -> ( exp )":
                value_exp();
                break;
            case "value -> var":
                value_var();
                break;
            case "value -> const":
                value_const();
                break;
            case "value -> ID m14 ( actual_args )":
                value_ID_m14_actual_args();
                break;
            case "m14 -> $":
                m14(tmp);
                break;
            case "m15 -> $":
                m15();
                break;
            case "const -> NUM":
                const_NUM(tmp);
                break;
            case "const -> REAL":
                const_REAL(tmp);
                break;
            case "const -> CHAR_CONST":
                const_CHAR(tmp);
                break;
            case "const -> STR":
                const_STR(tmp);
                break;
        }
    }
}