package com.microsoft.stocksearch.ranking.service.impl;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.text.Segment;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.microsoft.stocksearch.ranking.service.Dictionary;
import com.microsoft.stocksearch.ranking.service.SegmentService;

/**
 * implementation of SegmentService
 * @author v-junjzh
 *
 */
public class SegmentServiceImpl extends SegmentService {
	
	private Dictionary dic = null;
	
	public SegmentServiceImpl() {
	}
	
	@Override
	public void initDictionary(List<String> dictionary) {
		dic = new TrieDictionary();
		dic.build(dictionary);
	}

	@Override
	public List<String> segment(String data) {
		if(dic == null) {
			throw new RuntimeException("please initialize dictionary");
		}
		List<String> result = new ArrayList<>();

		int index = 0;
		int length = data.length();
		while(index < length) {
			String word = dic.maxMatch(data.substring(index));
			if(word == null) {
				word = data.substring(index, index+1);
			}
			result.add(word);
			index += word.length();
		}
		
		return result;
	}
	
}
