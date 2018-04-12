package com.syf;


import java.util.ArrayList;

class Production{
    // 产生式左部，右部
    public String left;
    public String[] right;

    public Production(String s){
        String[] strings = s.split(" ");
        left = strings[0];
        right = new String[strings.length - 1];
        System.arraycopy(strings, 1, this.right,0, strings.length - 1);
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

interface Contains{
    boolean contains();
}

class terminals implements Contains{
    public
}

public class Parse {

    private final int MAX_PRODCUCTION = 1000;
    private final int MAX_LENGTH = 1000;

    private Production[] productions = new Production[MAX_PRODCUCTION];
    private SymbolTable table = new SymbolTable(null);


    /*
     *   归约动作，这时候返回产生式的长度，然后栈中弹出这一串，并把产生式的左部分压栈
     */

    private SetOfItems CLOSURE(SetOfItems I){
        int size = 0;
        while(true){
            for(Item item: I.items) {
                //String
                for (Production production : productions) {
                    if (production.left.equals("B")){
                        int length = item.right.length - item.dot + 1;
                        // dot_right里存放着 [A -> α.Bβ, b] 中的 βb 部分
                        String[] dot_right = new String[length];
                        System.arraycopy(item.right, item.dot, dot_right, 0, length);
                        for (String b : getFirst(dot_right)) {
                            Item newItem = new Item(production, b);
                            I.insert(newItem);
                        }
                    }
                }
            }
            // until 不能向I中加入更多的项
            if(I.size > size){
                size = I.size;
            }else
                return I;
        }
    }

    /*
     *   getFirst(x), 求符号x的first集
     *   如果x是终结符，那么x就是它本身的终结符
     *   如果x不是终结符，那么x->x1x2x3x4x5, 返回x1的first集，如果x1->ε存在，那么返回x2的first集
     *   依次类推，如果所有的xi都可以推出空串，那么x的first集中包括ε
     */
    private ArrayList<String> getFirst(String[] X){
        int length = X.length;
        ArrayList<String> firstSet = new ArrayList<>(500);

        for(int i = 0; i < length; i++){
            if(X[i] instanceof terminal){
                firstSet.add(X[i]);
                return firstSet;
            }else if(X[i] instanceof nonTerminal){
                // 应该找到形如 X[i]->sadasd 的所有产生式
                Production[] productions = new Production[500];
                for(Production production: productions){
                    firstSet.addAll(getFirst(production.right));
                }
            }
        }

    }

    private void getFollow(){

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
