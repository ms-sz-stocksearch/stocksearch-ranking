package com.microsoft.stocksearch.ranking.utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.microsoft.stocksearch.ranking.servlets.SearchServlet;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.ByteOrderMarkDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;

public class CodeUtils {

	public static String getEncode(File document) {

		// Create the proxy:
		CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance(); // A
																				// singleton.

		// constructor:
		// Add the implementations of
		// info.monitorenter.cpdetector.io.ICodepageDetector:
		// This one is quick if we deal with unicode codepages:
		detector.add(new ByteOrderMarkDetector());
		// The first instance delegated to tries to detect the meta charset
		// attribut in html pages.
		detector.add(new ParsingDetector(true)); // be verbose about parsing.
		// This one does the tricks of exclusion and frequency detection, if
		// first implementation is
		// unsuccessful:
		detector.add(JChardetFacade.getInstance()); // Another singleton.
		detector.add(ASCIIDetector.getInstance()); // Fallback, see javadoc.

		boolean ret = false;
		// Work with the configured proxy:
		java.nio.charset.Charset charset = null;
		try {
			charset = detector.detectCodepage(document.toURL());
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (charset == null) {
			return "utf-8";
		}

		return charset.name();

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

			encode = getEncode(new File(path));

		} catch (Exception e) {
			e.printStackTrace(SearchServlet.ps);
		}
		return encode;
	}

}
