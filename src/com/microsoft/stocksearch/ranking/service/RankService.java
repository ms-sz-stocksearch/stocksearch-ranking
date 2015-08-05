package com.microsoft.stocksearch.ranking.service;

import java.util.List;

import com.microsoft.stocksearch.ranking.beans.QueryResult;

public abstract class RankService {
	
	public abstract List<QueryResult> sort(List<String> keywords);
	
}
