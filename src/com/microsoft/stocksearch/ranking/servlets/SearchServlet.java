package com.microsoft.stocksearch.ranking.servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

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
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }
    
    private void initService() {
    	dictionary = new ArrayList<String>();
		try {
			InputStream conf = getClass().getResourceAsStream("/dictionary.conf");
			BufferedReader confrd = new BufferedReader(new InputStreamReader(conf, "utf-8"));
			String dir = confrd.readLine();
			confrd.close();
			conf.close();
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
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ss = new SegmentServiceImpl();
		ss.initDictionary(dictionary);
		
		//correctService = new CorrectServiceImpl();
		
		//rankservice = new RankServiceImpl();
		
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
			response.getWriter().append(res.toString());
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
		
		String stockId = segments.get(segments.size()-1);
		
		segments.remove(segments.size()-1);
		
		List<QueryResult> queryResult = rankservice.sort(segments);
		
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
			obj.put("title", qr.getTitle());
			obj.put("url", qr.getUrl());
			obj.put("summary", qr.getSummary());
			result.add(obj);
		}
		
		res.put("result", result);
		
		response.getWriter().append(res.toString());
		
		
		/*
		CorrectService correct=new CorrectServiceImpl();
		segments=correct.correct(queryString,segments);
		
		response.getWriter().append("<br/>Query string: " + queryString + "<br/>After correct:<br/>");
		for (String word : segments) {
			response.getWriter().append(word).append("<br/>");
		}
		
		response.getWriter().append("<br/>");
		
		response.getWriter().append("Result :<br/>");
		
		
		RankService rs = new RankServiceImpl();
		List<QueryResult> qr = rs.sort(segments);
		
		for (QueryResult q : qr) {
			response.getWriter().append("<a href=\"" + q.getUrl() + "\">" + q.getTitle() + "</P>");
		}
		
		*/
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
