package com.microsoft.stocksearch.ranking.service;

import com.microsoft.stocksearch.ranking.beans.QueryResult;

public abstract class RankService {
	
	public abstract QueryResult[] sort(String[] keywords);
	
}
