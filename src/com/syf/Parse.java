package com.syf;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

class Production{
    // 产生式左部，右部
    private String left;
    private String[] right;

    public Production(String s){
        String[] strings = s.split(" ");
        left = strings[0];
        right = new String[strings.length - 1];
        System.arraycopy(strings, 1, this.right,0, strings.length - 1);
    }

    public String getLeft() {
        return left;
    }

    public String[] getRight() {
        return right;
    }
}

class Item{
    /*
        LR(1)的一个项， 形如 [A->a.Bc, β]
     */
    public Production production;
    public int dot;  // 产生式中点的位置
    public String[] right;

    public Item(Production production, String[] right){
        this.production = production;
        this.right = right;
        this.dot = 1;
    }
}

class SetOfItems{
    /*
        LR(1)的项集
     */
    public String left;
    public ArrayList<Item> items;
    public int size;

    public SetOfItems(String left){
        this.left = left;
        items = new ArrayList<>(500);
        size = 0;
    }

    public void insert(Item newItem){
        items.add(newItem);
        size++;
    }

}

public class Parse {

    private final int MAX_PRODCUCTION = 500;
    private final int MAX_LENGTH = 500;

    private ArrayList<Production> productions = new ArrayList<>(MAX_PRODCUCTION);
    private ArrayList<String> terminals = new ArrayList<>();
    private ArrayList<String> non_terminals = new ArrayList<>();

    private HashMap<String, ArrayList<Production>> classified_production = new HashMap<>();

    private HashMap<String, ArrayList<String>> firstSet = new HashMap<>();

    /*
     * read all productions from "Production_table.txt"
     * store production and identify terminals and non_terminals
     */
    private void readIn() throws IOException {
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\Production_table");
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file), "UTF-8"));
        String line;
        while((line = in.readLine()) != null){
            this.productions.add(new Production(line));
            String[] production = line.split(" ");
            for(int i=0; i<production.length; i++){
                if(Character.isLetter(production[i].charAt(0)) && Character.isLowerCase(production[i].charAt(0))){
                    if(!this.non_terminals.contains(production[i])) {
                        this.non_terminals.add(production[i]);
                        System.out.println(production[i]);
                    }
                }else{
                    if(!this.terminals.contains(production[i])) {
                        this.terminals.add(production[i]);
                        System.out.println(production[i]);
                    }
                }
            }
        }
    }

    /*
     * classify productions by non-terminals
     */
    private void classify_production(){
        boolean[] visited = new boolean[productions.size()];
        for(int i=0; i<non_terminals.size(); i++){
            ArrayList<Production> arrayList = new ArrayList<>(50);
            for(int j=0; j<this.productions.size(); j++){
                if(visited[j])
                    continue;
                Production production = productions.get(j);
                if(production.getLeft().equals(this.non_terminals.get(i))){
                    arrayList.add(production);
                    visited[j] = true;
                }
            }
            classified_production.put(this.non_terminals.get(i), arrayList);
        }
    }

    /*
     * 搜索某个非终结符的所有产生式
     */
    private ArrayList<Production> getProductionBySymbol(String non_terminal){
        if(!classified_production.containsKey(non_terminal)){
            System.out.println("该非终结符不存在！");
            return null;
        }
        return classified_production.get(non_terminal);
    }



     // 归约动作，这时候返回产生式的长度，然后栈中弹出这一串，并把产生式的左部分压栈
//    private SetOfItems CLOSURE(SetOfItems I){
//        int size = 0;
//        while(true){
//            for(Item item: I.items) {
//                //String
//                for (Production production : productions) {
//                    if (production.getLeft().equals("B")){
//                        int length = item.right.length - item.dot + 1;
//                        // dot_right里存放着 [A -> α.Bβ, b] 中的 βb 部分
//                        String[] dot_right = new String[length];
//                        System.arraycopy(item.right, item.dot, dot_right, 0, length);
//                        for (String b : getFirst(dot_right)) {
//                            Item newItem = new Item(production, b);
//                            I.insert(newItem);
//                        }
//                    }
//                }
//            }
//            // until 不能向I中加入更多的项
//            if(I.size > size){
//                size = I.size;
//            }else
//                return I;
//        }
//    }

    // 某个符号能否推导出ε
    private boolean ifCanEmpty(String symbol){
        if(this.terminals.contains(symbol))
            return false;
        if(this.non_terminals.contains(symbol)){
            ArrayList<Production> productions = classified_production.get(symbol);
            for(int i=0; i<productions.size(); i++){
                if(productions.get(i).getRight().length == 0)
                    return true;
            }
            return false;
        }
        System.out.println(symbol + "  该符号不存在！");
        return false;
    }

    // 求出所有符号的first集
    private void makeFirst(){
        //Object[] terminals = this.terminals.keySet().toArray();
        //Object[] non_terminals = this.non_terminals.keySet().toArray();
        ArrayList<String> firstSet;
        // 所有终结符的first集
        for(int i=0; i<this.terminals.size(); i++){
            String terminal = terminals.get(i);
            firstSet = new ArrayList<>(1);
            firstSet.add(terminal);
            this.firstSet.put(terminal, firstSet);
        }
        // 非终结符的first集
        int count = 0;
        int prevcount = 0;
        int flag = 0;
        while(flag++ < 10) {
            for (int i = 0; i < this.non_terminals.size(); i++) {
                String non_terminal = this.non_terminals.get(i);
                firstSet = new ArrayList<>(MAX_LENGTH);
                ArrayList<Production> productions = getProductionBySymbol(non_terminal);
                int size = productions.size();
                int cnt;
                for (int j = 0; j < size; j++) {
                    cnt = -1;
                    String[] right = productions.get(j).getRight();
                    // 首先查right的第一个符号，如果这个符号不能推出ε，那么就不往后延伸
                    for (int k = 0; k < right.length; k++) {
                        ArrayList<String> tmpFirstSet = this.getFirst(right[k]);
                        if(tmpFirstSet == null)
                            continue;
                        for (int m = 0; m < tmpFirstSet.size(); m++) {
                            if (!firstSet.contains(tmpFirstSet.get(m))) {
                                firstSet.add(tmpFirstSet.get(m));
                                count++;
                            }
                        }
                        //firstSet.addAll(tmpFirstSet);
                        if (!this.ifCanEmpty(right[k]))
                            break;
                        cnt = k;
                    }
                    if (cnt == right.length - 1 && !firstSet.contains("")) {
                        firstSet.add("");
                        count++;
                    }
                }
                this.firstSet.put(non_terminal, firstSet);
            }
            // until没有新的加进去，结束
            if(count == prevcount)
                break;
            prevcount = count;
        }
    }

    /*
     * 查找某个符号的first集
     */
    private ArrayList<String> getFirst(String symbol){
        if(!firstSet.containsKey(symbol)){
            //System.out.println("该符号不存在！");
            return null;
        }
        return firstSet.get(symbol);
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

    private void output() throws FileNotFoundException {
        File file = new File("F:\\output.txt");
        PrintWriter pw = new PrintWriter(file);
        pw.println("*************终结符**************");
        pw.flush();
        for(int i=0; i<this.terminals.size(); i++){
            //System.out.println(this.terminals.get(i));
            pw.println(i + " " + this.terminals.get(i) + "\t");
            pw.flush();
        }
        pw.println();
        pw.println("*************非终结符**************");
        pw.flush();
        for(int i=0; i<this.non_terminals.size(); i++){
            pw.println(i + " " + this.non_terminals.get(i) + "\t");
            pw.flush();
        }
        pw.println();
        pw.println("*************First集合**************");
        pw.println("*************终结符**************");
        pw.flush();
        for(int i=0; i<this.terminals.size(); i++){
            pw.print(i + " " + "符号：\t" + this.terminals.get(i));
            ArrayList<String> firstSet = this.firstSet.get(terminals.get(i));
            for(int j=0; j<firstSet.size(); j++){
                pw.println("\t" + firstSet.get(j) + "\t");
                pw.flush();
            }
        }
        pw.println("*************非终结符**************");
        for(int i=0; i<this.non_terminals.size(); i++){
            pw.print(i + " " + "符号：\t" + this.non_terminals.get(i));
            ArrayList<String> firstSet = this.firstSet.get(non_terminals.get(i));
            for(int j=0; j<firstSet.size(); j++){
                pw.print("\t"+firstSet.get(j) + "\t");
                pw.flush();
            }
            pw.println();
        }
        pw.flush();
        pw.close();

    }


	public static void main(String[] args) {
        Parse parse = new Parse();
        try {
            parse.readIn();
            parse.classify_production();
            //System.out.println();
            parse.makeFirst();
            parse.output();
        }catch (IOException e){
            e.printStackTrace();
        }

	}

}
