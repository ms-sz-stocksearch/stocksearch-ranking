package com.microsoft.stocksearch.ranking.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.microsoft.stocksearch.ranking.servlets.SearchServlet;

public class StockMapUtils {

	private static Map<String, String> stockmap;
	private static Set<String> stockids;
	
	static {
		stockmap = new HashMap();
		stockids = new HashSet();
		String FullPath = ConfigUtil.getStockMap();
		try {
			String encoding = CodeUtils.getFileEncode(FullPath);
			System.out.println("-->stock map encode: " + encoding);
			File file = new File(FullPath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {// 按行读取
					if (!"".equals(lineTxt)) {
						String[] reds = lineTxt.split("\\s+");// 对行的内容进行分析处理后再放入map里。
						String stock = reds[0];
						String stockid = reds[1];
						stockids.add(stockid);
						stockmap.put(stock, stockid);// 放入map
					}
				}
				read.close();// 关闭InputStreamReader
				bufferedReader.close();// 关闭BufferedReader
			} else {
				System.out.println("cannot find file");
			}
		} catch (Exception e) {
			System.out.println("read error");
			e.printStackTrace(SearchServlet.ps);
		}
	}
	
	public static Map<String, String> getStockMap() {
		return stockmap;
	}
	
	public static Set<String> getStockIds() {
		return stockids;
	}
	
}
