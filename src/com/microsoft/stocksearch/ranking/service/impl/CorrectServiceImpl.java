package com.microsoft.stocksearch.ranking.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.microsoft.stocksearch.ranking.service.CorrectService;

import java.util.HashMap;

public class CorrectServiceImpl extends CorrectService {
	public static Map<String, String> getDic(List<String> dic, Map<String, String> filemaps) {
		String FilePath = "C:\\Users\\v-junjzh\\ranking\\";
		String FileName = "stockid.txt";
		String FullPath = FilePath + FileName;
		try {
			String encoding = "UTF-8";
			File file = new File(FullPath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {// 按行读取
					if (!"".equals(lineTxt)) {
						String[] reds = lineTxt.split("\\s+");// 对行的内容进行分析处理后再放入map里。
						//System.out.println("===>" + reds[0] + " : " + reds[1]);
						String stock = reds[0];
						// System.out.println(stock);
						//int index = stock.length();
						//String stockid = lineTxt.substring(index).trim();
						String stockid = reds[1];
						dic.add(stock);
						filemaps.put(stock, stockid);// 放入map
					}
				}
				read.close();// 关闭InputStreamReader
				bufferedReader.close();// 关闭BufferedReader
			} else {
				System.out.println("==>cannot find file:" + FullPath);
			}
		} catch (Exception e) {
			System.out.println("read error");
			e.printStackTrace();
		}
		return filemaps;
	}

	@Override
	public List<String> correct(String query, List<String> segment) {
		Boolean isExist = false;
		List<String> dic = new ArrayList<String>();
		Map<String, String> filemaps = new HashMap<String, String>();
		getDic(dic, filemaps);
		// 最大距离要求为2
		int th = 2;
		int distance = th;
		String correct = "";
		for (int i = 0; i < dic.size(); i++) {
			String stock = dic.get(i);
			int dif = minDistance(query, stock);
			if (dif < distance) {
				if (dif == 0) {
					segment.add(stock);
					return segment;
				}
				// System.out.println(i);
				correct = stock;
				distance = dif;
			}
		}
		if (distance < th) {
			segment.add(correct);
			System.out.println("+++++++" + filemaps.get(correct));
			segment.add(filemaps.get(correct));
			isExist = true;
		} else {
			String id = null;
			for (String s : segment) {
				if (filemaps.get(s) != null) {
					System.out.println("))))))))))))))))" + filemaps.get(s));
					id = filemaps.get(s);
					break;
				}
			}
			segment.add(id);
		}
		// System.out.println(isExist);
		// if(!isExist)segment.add(null);
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
				A[i][j] = Math.min(Math.min(A[i - 1][j] + 1, A[i][j - 1] + 1), A[i - 1][j - 1] + cost);// Step6
			}
		}
		return A[n][m]; // Step 7
	}
}
