package com.microsoft.stocksearch.ranking.service.impl;

import com.microsoft.stocksearch.ranking.service.SegmentService;

/**
 * implementation of SegmentService
 * @author v-junjzh
 *
 */
public class SegmentServiceImpl extends SegmentService {
	
	public SegmentServiceImpl() {
		
	}

	@Override
	public String[] segment(String data) {
		String[] result = new String[data.length()];
		for (int i = 0; i < data.length(); i++) {
			result[i] = "" + data.charAt(i);
		}
		return result;
	}

}
