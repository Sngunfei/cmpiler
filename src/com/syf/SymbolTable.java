package com.syf;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {

	private HashMap<String, SymbolEntry> table;
	protected SymbolTable pre;

	public SymbolTable(SymbolTable pre){
		table = new HashMap<>();
		this.pre = pre;
	}

	public void put(String s, Token token){
		table.put(s, new SymbolEntry(token));
	}

	// 递归的搜索
	public SymbolEntry get(String s){
		for(SymbolTable st = this; st != null; st = st.pre){
			SymbolEntry se = st.table.get(s);
			if(se != null)
				return se;
		}
		return null;
	}

}
