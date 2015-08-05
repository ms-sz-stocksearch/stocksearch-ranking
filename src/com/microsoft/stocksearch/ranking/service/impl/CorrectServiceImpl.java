package com.microsoft.stocksearch.ranking.service.impl;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.List;

import com.microsoft.stocksearch.ranking.service.CorrectService;
import com.microsoft.stocksearch.ranking.service.Dictionary;

public class CorrectServiceImpl extends CorrectService {
	
	@Override
	public List<String> correct(String query, List<String> segment) {
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
		// 最大距离要求为2
		int th = 2;
		int distance = th;
		String correct = "";
		for (int i = 0; i <dic.size(); i++) {
			String stock = dic.get(i);
			int dif = minDistance(query, stock);
			if (dif < distance) {
				if (dif == 0) {
					segment.add(stock);
					return segment;
				}
				correct = stock;
				distance = dif;
			}
		}
		if(distance<th)segment.add(correct);
		return segment;
	}

	public int minDistance(String word1, String word2) {
		// Step 1
		int n = word1.length(), m = word2.length();
		if (n == 0)
			return m;
		if (m == 0)
			return n;
		int A[][] = new int[n + 1][m + 1];

		// Step 2
		for (int i = 0; i <= n; ++i)
			A[i][0] = i;
		for (int j = 0; j <= m; ++j)
			A[0][j] = j;

		for (int i = 1; i <= n; ++i) { // Step 3
			char word1_i = word1.charAt(i - 1);
			for (int j = 1; j <= m; ++j) { // Step 4
				char word2_j = word2.charAt(j - 1);
				int cost = (word1_i == word2_j) ? 0 : 1; // Step 5
				A[i][j] = Math.min(Math.min(A[i - 1][j] + 1, A[i][j - 1] + 1), A[i - 1][j - 1] + cost);// Step
																										// 6
			}
		}
		return A[n][m]; // Step 7
	}


}
