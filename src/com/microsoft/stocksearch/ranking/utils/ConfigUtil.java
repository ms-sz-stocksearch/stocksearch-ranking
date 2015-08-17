package com.microsoft.stocksearch.ranking.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.microsoft.stocksearch.ranking.servlets.SearchServlet;

public class ConfigUtil {
	
	private static final String CONF_PATH = "/path.conf";
	
	public static String getDictionaryPath() {
		return get("Dictionary");
	}
	
	public static String getStockMap() {
		return get("StockMap");
	}
	
	public static String get(String key) {
		Properties properties = new Properties();
		InputStream in = ConfigUtil.class.getResourceAsStream(CONF_PATH);
		try {
			properties.load(in);
		} catch (IOException e) {
			e.printStackTrace(SearchServlet.ps);
			return null;
		}
		return (String) properties.get(key);
	}
	
}
