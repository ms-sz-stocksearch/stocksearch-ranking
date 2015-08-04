package com.microsoft.stocksearch.ranking.service;

public abstract class CorrectService {
	
	public abstract String[] correct(String query, String[] segments);

}
