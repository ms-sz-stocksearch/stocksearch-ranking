package com.microsoft.stocksearch.ranking.service.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.microsoft.stocksearch.ranking.beans.QueryResult;
import com.microsoft.stocksearch.ranking.service.DataService;
import com.microsoft.stocksearch.ranking.service.RankService;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class RankServiceImpl extends RankService {
	
	private final double TITLE = 7;
	private final double CONTENT = 3;


	public RankServiceImpl() {
	}


	@Override
	public List<QueryResult> sort(List<String> keywords) {
		List<QueryResult> ans = new ArrayList();
		
		
		List<QueryResult> data = null; 
		DataService ds = new DataService();
		
		try {
			ds.connect();
			data = ds.getData();
			ds.close();
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int index = 0;
		Pair[] ws = new Pair[data.size()];
		for(QueryResult p : data) {
			int tc = 0;
			int cc = 0;
			
			for(String word : keywords) {
				tc += count(p.getTitle(), word);
				cc += count(p.getSummary(), word);
			}
			
			ws[index] = new Pair();
			ws[index].qs = p;
			ws[index].weight = TITLE * tc + CONTENT * cc;
			ws[index].qs.setTitle(ws[index].qs.getTitle() + " title c: " + tc + ", content c: " + cc + ", wei: " + ws[index].weight);
			index++;
		}

		Arrays.sort(ws);
		
		List<QueryResult> fin = new ArrayList<QueryResult>();
		for(Pair q : ws) {
			fin.add(q.qs);
		}

		return fin;

	}
	
	private int count(String str, String key) {
		int ret = 0;
		int index = 0;
	    while ((index=str.indexOf(key,index))!=-1){
	        str = str.substring(index+key.length());
	        ret++;
	    }
		return ret;
	}
	
	class Pair implements Comparable<Pair>{
		double weight;
		QueryResult qs;
		@Override
		public int compareTo(Pair o) {
			if(weight > o.weight) {
				return -1;
			} else if(weight < o.weight) {
				return 1;
			}
			return 0;
		}
	}

}
