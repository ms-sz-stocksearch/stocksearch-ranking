package com.microsoft.stocksearch.ranking.service;

import java.util.List;

/**
 * interface of segmentation
 * @author v-junjzh
 *
 */
public abstract class SegmentService {

	/**
	 * break query string down into undertandable segments
	 * @param data query string
	 * @return understandable segments
	 * @throws Exception 
	 */
	abstract public List<String> segment(String data);

	public void initDictionary(List<String> dictionary) {}
	
}
