package com.microsoft.stocksearch.ranking.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.sun.istack.internal.Builder;

public abstract class Dictionary {

	
	public abstract void build(List<String> list);
	public abstract String maxMatch(String str);
	

}
