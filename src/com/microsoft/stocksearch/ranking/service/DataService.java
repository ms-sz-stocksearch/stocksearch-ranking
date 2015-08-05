package com.microsoft.stocksearch.ranking.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.microsoft.stocksearch.ranking.beans.QueryResult;

public class DataService {
	
	private final String url = "jdbc:sqlserver://172.23.149.65:1433;databaseName=StockSearch";
	private final String user = "sa";
	private final String password = "LYZforsiess0804";

	private Connection conn;
	
	public void connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
		conn = DriverManager.getConnection(url, user, password);
	}
	
	public void close() throws SQLException {
		conn.close();
	}
	
	public List<QueryResult> getData() throws SQLException {
		String sql = "select * from HTMLPageData";
		Statement st = conn.createStatement();
		ResultSet res = st.executeQuery(sql);
		List<QueryResult> result = new ArrayList<QueryResult>(); 
		while(res.next()) {
			QueryResult qr = new QueryResult();
			qr.setTitle(res.getString("Title"));
			qr.setUrl(res.getString("Url"));
			qr.setSummary(res.getString("HTML_Content"));
			result.add(qr);
		}
		return result;
	}

}
