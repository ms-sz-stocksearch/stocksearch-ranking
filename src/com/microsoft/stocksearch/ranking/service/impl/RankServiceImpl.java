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
		
		QueryResult qr = new QueryResult();
		qr.setTitle("aaa");
		qr.setUrl("bbb");
		qr.setSummary("ccc");
		ans.add(qr);
		
		return ans;

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
