package com.microsoft.stocksearch.ranking.utils;

import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;

import com.microsoft.stocksearch.ranking.servlets.SearchServlet;

public class CodeUtils {

	public static String getEncode(byte[] input) {
		if (input.length < 3) {
			return null;
		}
		String code = null;
		if (input[0] == -1 && input[1] == -2) {
			code = "UTF-16";
		} else if (input[0] == -2 && input[1] == -1) {
			code = "Unicode";
		} else if (input[0] == -17 && input[1] == -69 && input[2] == -65) {
			code = "UTF-8";
		} else { // for test
			code = "gb2312";
		}
		return code;
	}

	public static String getEncode(String str) {
		String charEncode = "GB2312";
		try {
			if (str.equals(new String(str.getBytes(charEncode), charEncode))) {
				return charEncode;
			}
		} catch (UnsupportedEncodingException e) {

		}

		charEncode = "GBK";
		try {
			if (str.equals(new String(str.getBytes(charEncode), charEncode))) {
				return charEncode;
			}
		} catch (UnsupportedEncodingException e) {

		}

		charEncode = "ISO-8859-1";
		try {
			if (str.equals(new String(str.getBytes(charEncode), charEncode))) {
				return charEncode;
			}
		} catch (UnsupportedEncodingException e) {

		}

		charEncode = "UTF-8";
		try {
			if (str.equals(new String(str.getBytes(charEncode), charEncode))) {
				return charEncode;
			}
		} catch (UnsupportedEncodingException e) {

		}

		return null;
	}

	public static String getFileEncode(String path) {
		String encode = null;
		try {
			FileInputStream in = new FileInputStream(path);
			byte[] buff = new byte[3];
			in.read(buff);
			encode = getEncode(buff);
			in.close();
		} catch (Exception e) {
			e.printStackTrace(SearchServlet.ps);
		}
		return encode;
	}

}
