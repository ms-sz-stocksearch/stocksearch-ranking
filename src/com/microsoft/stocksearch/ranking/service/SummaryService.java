package com.microsoft.stocksearch.ranking.service;

import java.util.List;


public interface SummaryService {

	String getSummary(int id,List<String> keywords) ;
	
}
