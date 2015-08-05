package com.microsoft.stocksearch.ranking.service.impl;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.microsoft.stocksearch.ranking.service.Dictionary;
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
	public List<String> segment(String data) {
		List<String> result = new ArrayList<String>();
		Dictionary dictionary = new Dictionary();
		List<String> dic = null;
		try {
			dictionary.init();
			dic = dictionary.getDictionary();
			dictionary.close();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(dic == null) {
			result.add(data);
			return result;
		}
		
		Set<String> dics = new TreeSet<String>();
		for (String word : dic) {
			dics.add(word);
		}
		
		int i = 0;
		while(i < data.length()) {
			int matchIndex = data.length();
			while(matchIndex > i+1) {
				if(dics.contains(data.substring(i, matchIndex))) {
					break;
				}
				matchIndex--;
			}
			result.add(data.substring(i, matchIndex));
			i = matchIndex;
		}
		
		return result;
	}
	
}
