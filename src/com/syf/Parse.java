package com.syf;

import java.io.*;
import java.util.*;

class Production{
    // 产生式左部，右部
    private String left;
    private String[] right;
    private int id;
    private int hash;

    Production(String s){
        String[] strings = s.split(" ");
        left = strings[0];
        right = new String[strings.length - 1];
        System.arraycopy(strings, 1, this.right,0, strings.length - 1);
        hash = 0;
        for(int i=0; i>right.length; i++)
            hash = 31 * hash + right[i].hashCode();
        hash = 31 * hash + left.hashCode();
    }

    public String getLeft() {
        return this.left;
    }

    public String[] getRight() {
        return this.right;
    }

    public int getId(){
        return this.id;
    }

    public void setId(int id){
        this.id = id;
    }

    @Override
    public int hashCode(){
        return this.hash;
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
    //LR(1)的一个项， 形如 [A->a.Bc, β]

    private Production production;
    private int dot;  // 产生式中点的位置
    private ArrayList<String> rightHand;
    private int hash;

    Item(Production production, ArrayList<String> rightHand, int dot){
        this.production = production;
        this.rightHand = rightHand;
        this.dot = dot;
        hash = 0;
        for(int i=0; i<rightHand.size(); i++){
            hash = 31*hash + rightHand.get(i).hashCode();
        }
        hash = 31*hash + production.hashCode();
        hash = 31*hash + dot;
    }

    @Override
    public int hashCode(){
        return this.hash;
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

    public void setRightHand(ArrayList<String> rightHand){
        this.rightHand = rightHand;
        hash = 0;
        for(int i=0; i<rightHand.size(); i++){
            hash = 31*hash + rightHand.get(i).hashCode();
        }
        hash = 31*hash + production.hashCode();
        hash = 31*hash + dot;
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
    public boolean equals(Object object) {
        Item item = (Item) object;
        return this == object || this.production.equals(item.getProduction()) && this.dot == item.getDot() && this.rightHand.equals(item.getRightHand());
    }
}

class SetOfItems{
    // LR(1)的项集
    public static int number; // 静态变量监控当前的项集个数
    private ArrayList<Item> items = new ArrayList<>(500);
    private int id;   // 项集的编号，也就是状态号 I1, I2, ... ,In
    private int hash = 0;

    public void add(Item newItem){
        items.add(newItem);
        hash = hash*31 + newItem.hashCode();
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

    @Override
    public int hashCode(){
        return this.hash;
    }

    @Override
    public boolean equals(Object object){
        return this.hash == ((SetOfItems)object).hashCode() || this.items.equals(((SetOfItems)object).getItems());
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

    private Stack<Integer> state_stack = new Stack<>();
    private Stack<String> symbol_stack = new Stack<>();

    private String[][] table = new String[2000][200];

    public String start_unit;
    public Production start_production;

    /*
     * read all productions from "C.txt.txt"
     * store production and identify terminals and non_terminals
     */
    private void readIn() throws IOException {
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\sample2");
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                        new FileInputStream(file), "UTF-8"));
        String line;
        while((line = in.readLine()) != null){
            this.productions.add(new Production(line));
            String[] production = line.split(" ");
            for(int i=0; i<production.length; i++){
                if(Character.isLetter(production[i].charAt(0)) && Character.isUpperCase(production[i].charAt(0))){
                    if(!this.non_terminals.contains(production[i]))
                        this.non_terminals.add(production[i]);
                }else{
                    if(!this.terminals.contains(production[i]))
                        this.terminals.add(production[i]);
                }
            }
        }
        this.productions.add(this.start_production);
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
                // 对项集中的每一项
                Item item = I.getItems().get(i);
                String tmpSymbol = item.getTmpSymbol();
                //System.out.println(tmpSymbol);
                if(tmpSymbol == null)
                    continue;
                // 如果项的形式类似于 [ A -> α.Bβ, b]
                // 那么对于以B为首的所有产生式
                ArrayList<Production> tmpProductions = this.getProductionBySymbol(tmpSymbol);
                if (tmpProductions == null)
                    continue;
                // 符号B的后面，即βb
                ArrayList<String> rightHand = item.getRight();
                ArrayList<String> tmpFirstSet;
                if(item.getDot() == item.getProduction().getRight().length - 1)
                    tmpFirstSet = rightHand;
                else
                    tmpFirstSet = this.getFirst(rightHand);
             L1:for (Production production : tmpProductions) {
                   // System.out.println(production.toString());
                   // System.out.println(tmpFirstSet.toString());
                 for (int i1 = 0; i1 < I.getItems().size(); i1++) {
                     // 合并形如 [ B -> .C, b] 和 [ B -> .C, b d] 的项
                     Item tmp = I.getItems().get(i1);
                     if (tmp.getProduction().equals(production) && tmp.getDot() == 0) {
                         // 将具有相同产生式和点的位置的项合并起来，也就是将向前看符号合并到一起
                         ArrayList<String> newFirstSet = tmp.getRightHand();
                         for (int j = 0; j < tmpFirstSet.size(); j++) {
                             if (!newFirstSet.contains(tmpFirstSet.get(j)))
                                 newFirstSet.add(tmpFirstSet.get(j));
                         }
                         tmp.setRightHand(newFirstSet);
                         // 因为将这样的项合并到一起，所以for循环中如果检测到一个，就跳出该循环
                         break L1;
                     }
                 }
                 // 如果没有，说明是一个新的项
                 Item newItem = new Item(production, tmpFirstSet, 0);
                 I.add(newItem);
                 size++;
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
            if(dot == prod.getRight().length)
                continue;
            if(X.equals(item.getTmpSymbol())){
                Item newItem = new Item(prod, item.getRightHand(), dot+1);
                J.add(newItem);
            }
        }
        return CLOSURE(J);
    }

    /*
     * items函数，初始化项集
     * 输入：文法G的增广文法G'
     * 输出：项集族
     */
    private void items(){
        SetOfItems start = new SetOfItems();
        ArrayList<String> right = new ArrayList<>();
        right.add("$");
        Item item = new Item(this.start_production, right, 0);
        start.add(item);
        SetOfItems.number = 0;
        start.setId(SetOfItems.number++);
        this.states.add(CLOSURE(start));
        int size = 0;
        int prev = 0;
        int flag = 0;
        while(true) {
            for (int i = 0; i < this.states.size(); i++) {
                SetOfItems state = this.states.get(i);
                for (String terminal : this.terminals) {
                    SetOfItems nextState = GOTO(state, terminal);
                    if (nextState.getItems().size()!=0 && !this.states.contains(nextState)) {
                        nextState.setId(SetOfItems.number++);
                        this.states.add(nextState);
                        size++;
                    }
                }
                for (String non_terminal : this.non_terminals) {
                    SetOfItems nextState = GOTO(state, non_terminal);
                    if (nextState.getItems().size()!=0 && !this.states.contains(nextState)) {
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

    private void makeLabel(){
        this.states.trimToSize();
        for(int i=0; i<this.states.size(); i++){
            this.states.get(i).setId(i);
        }
        SetOfItems.number = this.states.size();

        this.productions.trimToSize();
        for(int i=0; i<this.productions.size(); i++){
            this.productions.get(i).setId(i);
        }
    }

    // 规范LR语法分析表的构造
    private void parserTable(){
        for(int i=0; i<this.states.size(); i++){
            SetOfItems curState = this.states.get(i);
            for(int j=0; j<curState.getItems().size(); j++){
                Item curItem = curState.getItems().get(j);
                String curSymbol = curItem.getTmpSymbol();
                // 第一种情况
                if((curItem.getDot() < curItem.getProduction().getRight().length) && this.terminals.contains(curSymbol)) {
                    SetOfItems nextState = GOTO(curState, curSymbol);
                    int index1;
                    if((index1 = this.states.indexOf(nextState)) != -1) {
                        nextState = this.states.get(index1);
                        int index = terminals.indexOf(curSymbol);
                        table[i][index] = "s"+nextState.getId();
                    }
                }
                // 第二种情况
                if((curItem.getDot() == curItem.getProduction().getRight().length)
                        && !curItem.getProduction().getLeft().equals(this.start_unit)){
                    ArrayList<String> rightHand = curItem.getRightHand();
                    for(String str: rightHand) {
                        int index = this.terminals.indexOf(str);
                        table[i][index] = "r" + curItem.getProduction().getId();
                    }
                }
                // 第三种情况
                if(curItem.getProduction().getLeft().equals(this.start_unit) && curItem.getDot() == 1){
                    int index = this.terminals.indexOf("$");
                    table[i][index] = "acc";
                }
            }
            // 第四步
            for(int j=0; j<this.non_terminals.size(); j++){
                SetOfItems nextState = GOTO(curState, this.non_terminals.get(j));
                int index1;
                if((index1 = this.states.indexOf(nextState)) != -1) {
                    nextState = this.states.get(index1);
                    int index = this.terminals.size() + j;
                    table[i][index] = String.valueOf(nextState.getId());
                }
            }
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
                else {
                    SymbolFirstSet = new ArrayList<>(MAX_LENGTH);
                    this.firstSet.put(non_terminal, SymbolFirstSet);
                }
                ArrayList<Production> SymbolProductions = getProductionBySymbol(non_terminal);
                if(SymbolProductions == null)
                    continue;
                for (Production SymbolProduction : SymbolProductions) {
                    ArrayList<String> right = new ArrayList<>();
                    Collections.addAll(right, SymbolProduction.getRight());
                    //System.out.println(right.toString());
                    ArrayList<String> tmpFirstSet = this.getFirst(right);
                    if(tmpFirstSet == null) break;
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
        if(!firstSet.containsKey(symbol))
            return null;
        return firstSet.get(symbol);
    }

    // 查找符号串的first集
    private ArrayList<String> getFirst(ArrayList<String> right){
        ArrayList<String> tmpFirstSet = new ArrayList<>(MAX_LENGTH);
        int cnt = 0;
        for(String rightHand: right){
            if(!this.firstSet.containsKey(rightHand))
                return null;
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

    // action-shift
    private void action_shift(int nextState, String curSymbol){
        this.state_stack.push(nextState);
        this.symbol_stack.push(curSymbol);
        System.out.println("移入");
    }

    // action-reduce
    private void action_reduce(int curState, int id){
        Production production = this.productions.get(id);
        int len = production.getRight().length;
        String left = production.getLeft();
        System.out.println("使用产生式 "+ production.toString()+ " 规约");
        for(int i=0; i<len; i++) {
            symbol_stack.pop();
            state_stack.pop();
        }
        symbol_stack.push(left);
        curState = state_stack.peek();
        int index = this.terminals.size() + this.non_terminals.indexOf(left);
       // System.out.println(curState + " " + index);
        int nextState = Integer.parseInt(this.table[curState][index]);
        state_stack.push(nextState);
    }

    // LR(1)文法
    public void parser(){
        Scanner scan = new Scanner();
        this.state_stack.push(0);
        this.symbol_stack.push(this.start_unit);
        Token a = scan.token_scan();
        System.out.println(a.name);
        while(true) {
            String curSymbol = a.name;
            int index = this.terminals.indexOf(curSymbol);
            int curState = this.state_stack.peek();
            //System.out.println(curState + "   " + index + "  " + curSymbol);
            String str;
            if (index != -1 && (str = this.table[curState][index]) != null) {
                if (str.charAt(0) == 's') {
                    StringBuilder sb = new StringBuilder(str);
                    sb.deleteCharAt(0);
                    int nextState = Integer.parseInt(sb.toString());
                    this.state_stack.push(nextState);
                    this.symbol_stack.push(curSymbol);
                    a = scan.token_scan();
                    System.out.println(a.name);
                    System.out.println("移入"+nextState);
                }else if (str.charAt(0) == 'r') {
                    StringBuilder sb = new StringBuilder(str);
                    sb.deleteCharAt(0);
                    int id = Integer.parseInt(sb.toString());
                    this.action_reduce(curState, id);
                }else if ("acc".equals(str)){
                    System.out.println("语法分析完成");
                    break;
                }
            }else{
                System.out.println("语法分析出错！");
                break;
            }
        }
    }

    // 输出文法的相关信息
    private void output() throws IOException {
        outputSymbol();
        System.out.println("Write symbols successful");
        this.outputProductions();
        System.out.println("Write productions successful!");
        this.outputFirst();
        System.out.println("Write firstSets successful!");
        this.outputStates();
        System.out.println("Write states successful!");
        this.outTable();
        System.out.println("write table successful!");
    }

    // 输出符号
    private void outputSymbol() throws IOException{
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\symbols.txt");
        PrintWriter pw = new PrintWriter(file);
        pw.println(this.terminals.size()+" terminals");
        for(int i=0; i<this.terminals.size(); i++)
            pw.println(i + " " + this.terminals.get(i));
        pw.println(this.non_terminals.size() + " non_terminals");
        for(int i=0; i<this.non_terminals.size(); i++)
            pw.println(i + " " + this.non_terminals.get(i));
        pw.flush();
        pw.close();
    }

    // 输出符号的first集
    private void outputFirst() throws IOException{
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\first.txt");
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

    // output all productions
    private void outputProductions() throws IOException{
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\Productions.txt");
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
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\states.txt");
        PrintWriter pw = new PrintWriter(file);
        for(SetOfItems setOfItems: this.states){
            pw.println(setOfItems.toString());
            pw.flush();
        }
        pw.close();
    }

    // 输出action和goto表
    private void outTable() throws IOException{
        File file = new File("C:\\Users\\86234\\workspace\\FirstProj\\src\\com\\syf\\table.txt");
        PrintWriter pw = new PrintWriter(file);
        pw.print('\t');
        for (String terminal : this.terminals) {
            pw.print(terminal + '\t');
        }
        for (String non_terminal: this.non_terminals){
            pw.print(non_terminal + '\t');
        }
        pw.print("\r\n");
        for(int i=0; i<this.states.size(); i++){
            pw.print("I" + i + '\t');
            for(int j=0; j<this.terminals.size(); j++){
                if(table[i][j] != null)
                    pw.print(table[i][j]+'\t');
                else
                    pw.print('\t');
            }
            for(int j=0; j<this.non_terminals.size(); j++){
                int index = this.terminals.size() + j;
                if(table[i][index] != null)
                    pw.print(table[i][index]+'\t');
                else
                    pw.print('\t');
            }
            pw.print("\r\n");
        }
        pw.flush();
        pw.close();
    }

	public static void main(String[] args) {
        Parse parse = new Parse();
        try {
            parse.start_unit="S1";
            parse.start_production = new Production("S1 E");

            parse.readIn();
            parse.outputSymbol();
            parse.classify_production();

            parse.makeFirst();
            parse.makeLabel();
            System.out.println("1");

            parse.items();
            System.out.println("2");
            parse.makeLabel();
            parse.parserTable();
            System.out.println("3");
            parse.output();
            parse.parser();
            System.out.println("4");
        }catch (IOException e){
            e.printStackTrace();
        }

	}

}
