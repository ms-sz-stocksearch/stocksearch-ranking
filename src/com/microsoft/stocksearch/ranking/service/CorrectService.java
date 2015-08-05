package com.microsoft.stocksearch.ranking.service;

import java.util.List;

public abstract class CorrectService {
	
	public abstract List<String> correct(String query, List<String> segments);

}
