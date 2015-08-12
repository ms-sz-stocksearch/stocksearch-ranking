package com.microsoft.stocksearch.ranking.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;
import javax.swing.LayoutStyle;

import org.apache.catalina.ant.FindLeaksTask;

import com.microsoft.stocksearch.ranking.beans.QueryResult;
import com.microsoft.stocksearch.ranking.service.DataService;
import com.microsoft.stocksearch.ranking.service.RankService;
import com.sun.istack.internal.FinalArrayList;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import sun.misc.FloatingDecimal;
import sun.text.resources.FormatData_es_AR;

public class RankServiceImpl extends RankService {
	
	private final double TITLE = 7;
	private final double CONTENT = 3;

	private final String invertedTablePath = "C:\\Users\\v-junjzh\\ranking\\invertedindex.txt";
	private final String wordToIdPath = "C:\\Users\\v-junjzh\\ranking\\word2id.txt";
	private final String id2UrlPath = "C:\\Users\\v-junjzh\\ranking\\id2url.map";
	
	public class Node{
		int did;
		int cnt;
		public Node() {
		// TODO Auto-generated constructor stub
			did = cnt = 0;
		}
		public Node(int did,int cnt){
			this.did = did;
			this.cnt = cnt;
		}
		public int getDid(){
			return did;
		}
		public int getCnt(){
			return cnt;
		}
	}
    private Map<Integer, List<Node>> invertedTable = new HashMap<>();
	private Map<String, Integer> wordToIdTable = new HashMap<>();
	private Map<Integer, String> idToUrl = new HashMap<>();
	
	public RankServiceImpl() {
	}
	
	public  void InitialTable() {
		 //read invertedTable
		try {
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(invertedTablePath), "utf-8"));
			String read;
			while((read = cin.readLine()) != null){
				 String []tmp = read.split("\\s+");
				 Integer wordId = Integer.parseInt(tmp[0]);
				 String []list = tmp[1].split(";");
				 List<Node> NodeList = new ArrayList<>();
				 for (String element : list) {
					 String [] e = element.split("@");
					 int did = Integer.parseInt(e[0]);
					 int cnt = Integer.parseInt(e[1]);
					 NodeList.add(new Node(did,cnt));
				 }
				 invertedTable.put(wordId, NodeList);
			}
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try {
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(wordToIdPath), "utf-8"));
			String read;
			while((read = cin.readLine()) != null){
				String [] line = read.split("\\s+");
				String word = line[0];
				int id = Integer.parseInt(line[1]);
				wordToIdTable.put(word, id);
			}
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		try {
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(id2UrlPath), "utf-8"));
			String read;
			while((read = cin.readLine()) != null){
				String[] line = read.split("\\s+");
				int id = Integer.parseInt(line[0]);
				String url = line[1];
				idToUrl.put(id, url);
			}
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}
	public static <T> Set<T> intersection(Set<T> A, Set<T> B){
		Set<T> ans = new HashSet<T>();
		for(T x : A)
			if(B.contains(x))ans.add(x);
		return ans;
	}
	public String getTitle(int id){
		return "ss";
		/*
		String filePath = "/home/ubuntu/stocksearch/crawler/SpiderOut/" + id + ".title";
		String title = null;
		try {
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(filePath)));
			title = cin.readLine();
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return title;
		*/
	}
	public String getUrl(int id){
		return idToUrl.get(id);
	}
	public String getSummary(int id,List<String>keywords){
		return "";
	}
	
	private List<QueryResult> test() {
		List<QueryResult> re = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			QueryResult qr = new QueryResult();
			qr.setTitle("tttile" + i);
			qr.setUrl("http://www.baidu.com");
			qr.setSummary("sdfbksdfysdkhfkljl" + i);
			re.add(qr);
		}
		return re;
	}
	
	@Override
	public List<QueryResult> sort(List<String> keywords) {
		System.out.println("in sort");
		//System.out.println("=====>++>>ss " + (wordToIdTable == null) + " ; " + keywords == null);
		if(invertedTable.isEmpty() || wordToIdTable.isEmpty() || idToUrl.isEmpty()){
			InitialTable();
		}
		List<QueryResult> ans = new ArrayList<>();
		
		Set<Integer> documentSet = new HashSet<>();
		for(int i = 0; i < keywords.size(); i++){
			if(i == 0){
				System.out.println("in for +");
				System.out.println("=====>++>>ss " + keywords.get(i).equals("东"));
				System.out.println("in for -");
				Integer iid = wordToIdTable.get(keywords.get(i));
				if(iid == null) {
					continue;
				}
				int id = iid;
				List<Node>list = invertedTable.get(id);
				for (Node node : list) {
					documentSet.add(node.getDid());
				}
			}else{
				Set<Integer> now = new HashSet<>();
				Integer iid = wordToIdTable.get(keywords.get(i));
				if (iid == null) {
					continue;
				}
				int id = iid;
				List<Node>list = invertedTable.get(id);
				for (Node node : list) {
					now.add(node.getDid());
				}
				documentSet = intersection(documentSet,now);
			}
		}
		
		int index = 0;
		Pair[] ws = new Pair[documentSet.size()];
		
		for(int id : documentSet){
			ws[index] = new Pair();
			QueryResult qs = new QueryResult();
			qs.setTitle(getTitle(id));
			qs.setUrl(getUrl(id));
			qs.setSummary(getSummary(id, keywords));
			
			int tc = 0;
			for(String word : keywords){
				int ix = wordToIdTable.get(word);
				List<Node>list = invertedTable.get(ix);
				for(Node node : list){
					if(node.getDid() == id){
						tc += node.getCnt();
						break;
					}
				}
			}
			
			ws[index].qs = qs;
			ws[index++].weight = TITLE * tc;
		}
	

		Arrays.sort(ws);
		for(Pair q : ws) {
			ans.add(q.qs);
		}

		return ans;
	
	}
 
	class Pair implements Comparable<Pair>{
		double weight;
		QueryResult qs;
		@Override
		public int compareTo(Pair o) {
			if(weight > o.weight) {
				return -1;
			} else if(weight < o.weight) {
				return 1;
			}
			return 0;
		}
	}

}