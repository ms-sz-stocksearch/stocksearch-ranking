package com.microsoft.stocksearch.ranking.service;

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
	 */
	abstract public String[] segment(String data);
	
}
