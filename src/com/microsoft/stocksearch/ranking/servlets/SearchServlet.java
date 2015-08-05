package com.microsoft.stocksearch.ranking.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.microsoft.stocksearch.ranking.beans.QueryResult;
import com.microsoft.stocksearch.ranking.service.CorrectService;
import com.microsoft.stocksearch.ranking.service.DataService;
import com.microsoft.stocksearch.ranking.service.Dictionary;
import com.microsoft.stocksearch.ranking.service.RankService;
import com.microsoft.stocksearch.ranking.service.SegmentService;
import com.microsoft.stocksearch.ranking.service.impl.CorrectServiceImpl;
import com.microsoft.stocksearch.ranking.service.impl.RankServiceImpl;
import com.microsoft.stocksearch.ranking.service.impl.SegmentServiceImpl;

/**
 * Servlet implementation class SearchServlet
 */
public class SearchServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setHeader("content-type", "text/html;charset=UTF-8");
		
		
		// TODO 
		
		
		DataService das = new DataService();
		try {
			das.connect();
			List<QueryResult> list = das.getData();
			das.close();
			
			for (QueryResult q : list) {
				//response.getWriter().append(q.getTitle()).append("<br/>");
			}
			
		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
		
		
		String queryString = request.getParameter("s");
		
		String add = request.getParameter("add");
		
		
		if (queryString == null) {
			response.getWriter().append("<br/>Query string(parameter: s) is null");
			return ;
		}
		
		queryString = new String(queryString.getBytes("ISO8859-1"), "UTF-8");
		
		
		SegmentService ss = new SegmentServiceImpl();
		List<String> segments = ss.segment(queryString);
		
		response.getWriter().append("<br/>Query string: " + queryString + "<br/>After segment:<br/>");
		
		for (String word : segments) {
			response.getWriter().append(word).append("<br/>");
		}
		
		response.getWriter().append("<br/>");
		
		
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
		
		/*
		for (String word : segments) {
			response.getWriter().append(word).append("<br/>");
		}
		
		response.getWriter().append("<br/>");
		
		
		Dictionary dic = new Dictionary();
		
		try {
			dic.init();
			
			if(add != null) {
				add = new String(add.getBytes("ISO8859-1"), "UTF-8");
				dic.add(add);
			}
			
			String del = request.getParameter("del");
			if (del != null) {
				del = new String(del.getBytes("ISO8859-1"), "UTF-8");
				dic.delete(del);
			}
			
			List<String> ws = dic.getDictionary();
			for (String s : ws) {
				response.getWriter().append(s).append("<br/>");
			}
			
			dic.close();
			
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			response.getWriter().append("<br/>" + e.getMessage());
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
