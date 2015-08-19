package com.microsoft.stocksearch.ranking.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.stocksearch.ranking.beans.QueryResult;
import com.microsoft.stocksearch.ranking.service.CorrectService;
import com.microsoft.stocksearch.ranking.service.RankService;
import com.microsoft.stocksearch.ranking.service.SegmentService;
import com.microsoft.stocksearch.ranking.service.impl.CorrectServiceImpl;
import com.microsoft.stocksearch.ranking.service.impl.RankServiceImpl;
import com.microsoft.stocksearch.ranking.service.impl.SegmentServiceImpl;
import com.microsoft.stocksearch.ranking.utils.CodeUtils;
import com.microsoft.stocksearch.ranking.utils.ConfigUtil;
import com.microsoft.stocksearch.ranking.utils.StockMapUtils;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

/**
 * Servlet implementation class SearchServlet
 */
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static List<String> dictionary = null;
	private SegmentService ss = null;
	
	private static CorrectService correctService = null;
	private static RankService rankservice = null;
	
	
	public static PrintStream ps;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }
    
    private void initService() {
    	
    	try {
    		ps = new PrintStream(new FileOutputStream(ConfigUtil.get("OutputFile")));
    		System.setOut(ps);
    	} catch (Exception e) {
    		e.printStackTrace();
    		throw new RuntimeException(e.getMessage());
    	}
    	
    	dictionary = new ArrayList<String>();
		try {
			String dir = ConfigUtil.getDictionaryPath();
			System.out.println("dir->" + dir);
			File file = new File(dir);
			InputStream in = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
			String str = null;
			while((str = br.readLine()) != null) {
				String[] tmp = str.split("\\s+");
				dictionary.add(tmp[0]);
			}
			br.close();
			in.close();
		} catch (Exception e) {
			e.printStackTrace(ps);
		}
		ss = new SegmentServiceImpl();
		ss.initDictionary(dictionary);
		System.out.println("load dictionary success. size: " + dictionary.size());
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		
		
		// TODO 
		
		
		String queryString = request.getParameter("s");
		
		JSONObject res = new JSONObject();
		
		if (queryString == null) {
			res.put("status", 300);
			res.put("msg", "parameter s is null");
			response.getWriter().append(res.toString()).flush();
			return ;
		}
		
		queryString = new String(queryString.getBytes("ISO8859-1"), "UTF-8");
		
		
		if(ss == null) {
			initService();
		}
		
		if(correctService == null) {
			correctService = new CorrectServiceImpl();
		}
		
		if(rankservice == null) {
			rankservice = new RankServiceImpl();
		}
		
		List<String> segments = ss.segment(queryString);
		
		segments = correctService.correct(queryString, segments);
		
		
		String stockId = null;
		
		System.out.println("segments size: " + segments.size());
		for (String s : segments) {
			if(StockMapUtils.getStockMap().containsKey(s)) {
				stockId = StockMapUtils.getStockMap().get(s);
				break;
			}
			if(StockMapUtils.getStockIds().contains(s)) {
				stockId = s;
			}
		}
		
		segments.remove(segments.size()-1);
		
		System.out.println("==+==+");
		List<QueryResult> queryResult = rankservice.sort(segments);
		System.out.println("call end");
		//List<QueryResult> queryResult = new ArrayList<QueryResult>();
		
		//Set<String> ss = new HashSet<String>(segments);
		//segments = new ArrayList(ss);
		
		res.put("status", 200);
		res.put("msg", "ok");
		
		res.put("query", queryString);
		JSONArray segs = new JSONArray();
		
		for (String word : segments) {
			JSONObject wd = new JSONObject();
			wd.put("word", word);
			segs.add(wd);
		}
		
		res.put("segments", segs);
		
		res.put("stock_code", stockId);
		
		JSONArray result = new JSONArray();
		
		for (QueryResult qr : queryResult) {
			JSONObject obj = new JSONObject();
			String title = qr.getTitle();
			if (title == null) {
				title = "";
			}
			String url = qr.getUrl();
			if (url == null) {
				url = "";
			}
			String summary = qr.getSummary();
			if (summary == null) {
				summary = "";
			}
			obj.put("title", title);
			obj.put("url", url);
			obj.put("summary", summary);
			result.add(obj);
		}
		
		System.out.println("result");
		res.put("result", result);
		
		response.getWriter().append(res.toString()).flush();
		response.getWriter().close();
		
	}

	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
