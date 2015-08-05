package com.microsoft.stocksearch.ranking.service;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Dictionary {

	private final String DB_URL = "jdbc:mysql://172.23.150.95:3306/stocksearch?useUnicode=true&amp;characterEncoding=UTF-8";
	private final String DB_USER = "root";
	private final String DB_PASSWORD = "password";

	private Connection conn = null;

	public Dictionary() {

	}

	public void init() throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver").newInstance();
		conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
	}

	public List<String> getDictionary()
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, SQLException, UnsupportedEncodingException {

		List<String> result = new ArrayList<String>();

		String sql = "select * from dic order by length(word) desc";

		Statement stmt = conn.createStatement();
		ResultSet res = stmt.executeQuery(sql);

		while (res.next()) {
			String word = res.getString("word");
			//word = new String(word.getBytes("ISO-8859-1"), "utf-8");
			result.add((word));
		}

		stmt.close();

		return result;
	}

	public boolean add(String word) throws SQLException, UnsupportedEncodingException {
		if (word == null || word.length() < 0 || word.contains("'")) {
			throw new IllegalArgumentException("bad word");
		}
		//word = new String(word.getBytes("utf-8"), "ISO-8859-1");
		Statement stmt = conn.createStatement();
		String sql = "insert into dic(word) values('" + word + "')";
		boolean ret = stmt.execute(sql);
		stmt.close();
		return ret;
	}

	public boolean delete(String word) throws SQLException {
		if (word == null || word.length() < 0 || word.contains("'")) {
			throw new IllegalArgumentException("bad word");
		}
		Statement stmt = conn.createStatement();
		String sql = "delete from dic where word='" + word + "'";
		boolean ret = stmt.execute(sql);
		return ret;
	}

	public void close() throws SQLException {
		conn.close();
		conn = null;
	}

}
