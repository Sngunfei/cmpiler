package com.syf;

import java.io.*;
import java.util.*;

class Production{
    // 产生式左部，右部
    private String left;
    private String[] right;

    Production(String s){
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

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder(left);
        sb.append(" -> ");
        for (String aRight : right) sb.append(aRight).append(" ");
        return sb.toString();
    }

    @Override
    public boolean equals(Object object){
        Production prod = (Production)object;
        if(this == prod)
            return true;
        if(this.left.equals(prod.getLeft()) && this.right.length == prod.right.length){
            for(int i=0; i<this.right.length; i++){
                if(!this.right[i].equals(prod.getRight()[i]))
                    return false;
            }
            return true;
        }
        return false;
    }
}

class Item{
    /*
        LR(1)的一个项， 形如 [A->a.Bc, β]
     */
    private Production production;
    private int dot;  // 产生式中点的位置
    private ArrayList<String> rightHand;

    Item(Production production, ArrayList<String> rightHand, int dot){
        this.production = production;
        this.rightHand = rightHand;
        this.dot = dot;
    }

    public Production getProduction() {
        return production;
    }

    public ArrayList<String> getRightHand() {
        return rightHand;
    }

    public int getDot() {
        return dot;
    }

    public String getTmpSymbol(){
        if(this.dot == this.production.getRight().length)
            return null;
        return this.production.getRight()[this.getDot()];
    }

    public ArrayList<String> getRight(){
        ArrayList<String> right = new ArrayList<>(1000);
        for(int i = this.getDot()+1; i<this.production.getRight().length; i++){
            right.add(production.getRight()[i]);
        }
        right.addAll(this.rightHand);
        right.trimToSize();
        return right;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("[ ").append(production.getLeft()).append(" -> ");
        for(int i=0; i<production.getRight().length; i++){
            if(i == this.dot){
                sb.append(".");
            }
            sb.append(production.getRight()[i]).append(" ");
        }
        sb.append(", ");
        for(String aRight: this.rightHand) sb.append(aRight).append(" ");
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object object){
        Item item = (Item)object;
        if(!this.production.equals(item.getProduction()))
            return false;
        if(this.dot != item.getDot() || this.rightHand.size() != item.getRightHand().size())
            return false;
        for(int i=0; i<this.rightHand.size(); i++){
            if(!this.rightHand.get(i).equals(item.getRightHand().get(i)))
                return false;
        }
        return true;
    }
}

class SetOfItems{
    // LR(1)的项集
    public static int number; // 静态变量监控当前的项集个数
    private ArrayList<Item> items = new ArrayList<>(500);
    private int id;   // 项集的编号，也就是状态号 I1, I2, ... ,In

    public void add(Item newItem){
        items.add(newItem);
    }

    public int getSize() {
        return this.items.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public ArrayList<Item> getItems() {
        return items;
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("I").append(id).append(" : ").append("\r\n");
        for(Item item: this.items) sb.append(item.toString()).append("\r\n");
        return sb.toString();
    }

    public boolean contains(Item item){
        for (Item item1 : this.items) {
            if (item.equals(item1))
                return true;
        }
        return false;
    }

    @Override
    public boolean equals(Object object){
        SetOfItems state = (SetOfItems)object;
        if(this.items.size() != state.getItems().size())
            return false;
        for(int i=0; i<this.items.size(); i++){
            if(!this.items.get(i).equals(state.getItems().get(i)))
                return false;
        }
        return true;
    }
}

public class Parse {

    private static final int MAX_PRODCUCTION = 250;
    private static final int MAX_LENGTH = 100;
    private static final int MAX_STATE_NUMBER = 3000;

    private ArrayList<Production> productions = new ArrayList<>(MAX_PRODCUCTION);
    private ArrayList<String> terminals = new ArrayList<>(MAX_LENGTH);
    private ArrayList<String> non_terminals = new ArrayList<>(MAX_LENGTH);

    private ArrayList<SetOfItems> states = new ArrayList<>(MAX_STATE_NUMBER);

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
        this.terminals.add("$");
        in.close();
        this.productions.trimToSize();
    }

    // classify productions by non-terminals
    private void classify_production(){
        boolean[] visited = new boolean[productions.size()];
        for(int i=0; i<non_terminals.size(); i++){
            ArrayList<Production> arrayList = new ArrayList<>(50);
            for(int j=0; j<this.productions.size(); j++){
                if(visited[j]) continue;
                Production production = productions.get(j);
                if(production.getLeft().equals(this.non_terminals.get(i))){
                    arrayList.add(production);
                    visited[j] = true;
                }
            }
            classified_production.put(this.non_terminals.get(i), arrayList);
        }
    }

    // 搜索某个非终结符的所有产生式
    private ArrayList<Production> getProductionBySymbol(String non_terminal){
        if(!classified_production.containsKey(non_terminal)){
            //System.out.println("该非终结符不存在！");
            return null;
        }
        return classified_production.get(non_terminal);
    }

    // 求某个项集的闭集
    private SetOfItems CLOSURE(SetOfItems I){
        int size = 0;
        int prev = 0;
        while(true) {
            //ArrayList<Item> items = I.getItems();
            for (int i = 0; i < I.getItems().size(); i++) {
                //System.out.println("项集大小： " + I.getItems().size());
                Item item = I.getItems().get(i);
                String tmpSymbol = item.getTmpSymbol();
                //System.out.println(tmpSymbol);
                if(tmpSymbol == null)
                    continue;
                ArrayList<Production> tmpProductions = this.getProductionBySymbol(tmpSymbol);
                if (tmpProductions == null)
                    continue;
                for (Production production : tmpProductions) {
                   // System.out.println(production.toString());
                    ArrayList<String> rightHand = item.getRight();
                    ArrayList<String> tmpFirstSet = this.getFirst(rightHand);
                    //System.out.println(tmpFirstSet.toString());
                    Item newItem = new Item(production, tmpFirstSet, 0);
                    if (!I.contains(newItem)) {
                        I.add(newItem);
                        size++;
                    }
                }
            }
            // until 不能向I中加入更多的项
            if (size > prev) {
                prev = size;
                //System.out.println("size: " + size);
            }
            else
                return I;
        }
    }

    // GOTO函数
    private SetOfItems GOTO(SetOfItems I, String X){
        SetOfItems J = new SetOfItems();
        for(Item item: I.getItems()){
            Production prod = item.getProduction();
            int dot = item.getDot();
            if(X.equals(item.getTmpSymbol())){
                Item newItem = new Item(prod, item.getRightHand(), dot+1);
                J.add(newItem);
            }
        }
        return CLOSURE(J);
    }

    private boolean containState(SetOfItems state){
        for(SetOfItems tmp : this.states){
            if(tmp.equals(state))
                return true;
        }
        return false;
    }

    /*
     * items函数，初始化项集
     * 输入：文法G的增广文法G'
     * 输出：项集族
     */
    private void items(){
        SetOfItems start = new SetOfItems();
        Production production = new Production("s translation_unit");
        ArrayList<String> right = new ArrayList<>();
        right.add("$");
        Item item = new Item(production, right, 0);
        start.add(item);
        start.setId(SetOfItems.number++);
        this.states.add(CLOSURE(start));
        int size = 0;
        int prev = 0;
        int flag = 0;
        while(flag++ < 5) {
            System.out.println(flag);
            for (int i = 0; i < this.states.size(); i++) {
                SetOfItems state = this.states.get(i);
                for (String terminal : this.terminals) {
                    SetOfItems nextState = GOTO(state, terminal);
                    if (nextState != null && !this.states.contains(nextState)) {
                        nextState.setId(SetOfItems.number++);
                        this.states.add(nextState);
                        size++;
                    }
                }
                for (String non_terminal : this.non_terminals) {
                    SetOfItems nextState = GOTO(state, non_terminal);
                    if (nextState != null && !this.states.contains(nextState)) {
                        nextState.setId(SetOfItems.number++);
                        this.states.add(nextState);
                        size++;
                    }
                }
            }
            if (size > prev)
                prev = size;
            else
                break;
        }
    }

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
        ArrayList<String> SymbolFirstSet;
        // 终结符的first集
        for (String terminal : this.terminals) {
            SymbolFirstSet = new ArrayList<>(1);
            SymbolFirstSet.add(terminal);
            this.firstSet.put(terminal, SymbolFirstSet);
        }
        // 非终结符的first集
        int count = 0;
        int prevcount = 0;
        while(true) {
            for (String non_terminal : this.non_terminals) {
                if (this.firstSet.containsKey(non_terminal))
                    SymbolFirstSet = this.firstSet.get(non_terminal);
                else
                    SymbolFirstSet = new ArrayList<>(MAX_LENGTH);
                ArrayList<Production> SymbolProductions = getProductionBySymbol(non_terminal);
                if(SymbolProductions == null)
                    continue;
                for (Production SymbolProduction : SymbolProductions) {
                    ArrayList<String> right = new ArrayList<>();
                    Collections.addAll(right, SymbolProduction.getRight());
                    //System.out.println(right.toString());
                    ArrayList<String> tmpFirstSet = this.getFirst(right);
                    for(String str: tmpFirstSet){
                        if(!SymbolFirstSet.contains(str)){
                            SymbolFirstSet.add(str);
                            count++;
                        }
                    }
                }
                SymbolFirstSet.trimToSize();
                this.firstSet.put(non_terminal, SymbolFirstSet);
            }
            if(count == prevcount)
                break;
            prevcount = count;
        }
    }

    // 查找某个符号的first集
    private ArrayList<String> getFirst(String symbol){
        if(!firstSet.containsKey(symbol)){
            //System.out.println("该符号不存在！");
            return null;
        }
        return firstSet.get(symbol);
    }

    // 查找符号串的first集
    private ArrayList<String> getFirst(ArrayList<String> right){
        ArrayList<String> tmpFirstSet = new ArrayList<>(MAX_LENGTH);
        int cnt = 0;
        for(String rightHand: right){
            ArrayList<String> tmp = getFirst(rightHand);
            if(tmp == null)
                continue;
            for(String first: tmp){
                if(!tmpFirstSet.contains(first) && !"".equals(first))
                    tmpFirstSet.add(first);
            }
            if(!this.ifCanEmpty(rightHand))
                break;
            cnt++;
        }
        if(cnt == right.size())
            tmpFirstSet.add("");
        tmpFirstSet.trimToSize();
        return tmpFirstSet;
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

    private void outputFirst() throws IOException{
        File file = new File("F:\\first.txt");
        PrintWriter pw = new PrintWriter(file);
        ArrayList<String> first;
        for (String terminal : this.terminals) {
            first = this.getFirst(terminal);
            pw.println(terminal + " : " + first.toString());
        }
        for (String non_terminal : this.non_terminals) {
            first = this.getFirst(non_terminal);
            pw.println(non_terminal + " : " + first.toString());
        }
        pw.flush();
        pw.close();
    }

    // 输出文法的相关信息
    private void output() throws IOException {
        this.outputProductions();
        this.outputFirst();
        this.outputStates();
    }

    // output all productions
    private void outputProductions() throws IOException{
        File file = new File("F:\\Productions.txt");
        PrintWriter pw = new PrintWriter(file);
        for(int i=0; i<this.productions.size(); i++){
            Production p = this.productions.get(i);
            pw.println(i + "  " + p.toString());
        }
        pw.flush();
        pw.close();
    }

    // 输出所有状态
    private void outputStates() throws IOException{
        File file = new File("F:\\states.txt");
        PrintWriter pw = new PrintWriter(file);
        for(SetOfItems setOfItems: this.states){
            pw.println(setOfItems.toString());
            pw.flush();
        }
        pw.close();
    }

	public static void main(String[] args) {
        Parse parse = new Parse();
        try {
            parse.readIn();
            parse.classify_production();
            parse.makeFirst();
            parse.output();
            SetOfItems start = new SetOfItems();
            Production production = new Production("s translation_unit");
            ArrayList<String> right = new ArrayList<>();
            right.add("$");
            Item item = new Item(production, right, 0);
            start.add(item);
            start.setId(SetOfItems.number++);
            parse.states.add(parse.CLOSURE(start));
            parse.outputStates();
           // parse.items();
            //System.out.println(parse.states.size());
            //parse.outputStates();
        }catch (IOException e){
            e.printStackTrace();
        }

	}

}
