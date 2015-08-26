package com.microsoft.stocksearch.ranking.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.microsoft.stocksearch.ranking.beans.QueryResult;
import com.microsoft.stocksearch.ranking.service.RankService;
import com.microsoft.stocksearch.ranking.service.SummaryService;
import com.microsoft.stocksearch.ranking.servlets.SearchServlet;
import com.microsoft.stocksearch.ranking.utils.CodeUtils;
import com.microsoft.stocksearch.ranking.utils.ConfigUtil;
import com.microsoft.stocksearch.ranking.utils.StockMapUtils;

import sun.misc.CompoundEnumeration;

public class RankServiceImpl extends RankService {

	private final double TITLE = 7;
	private final double CONTENT = 3;
	private final double STOCK = 1.5;
	private final double NORMAL = 1.0;
	// private final String invertedTablePath =
	// "C:\\Users\\v-junjzh\\ranking\\invertedindex.txt";
	// private final String wordToIdPath =
	// "C:\\Users\\v-junjzh\\ranking\\word2id.txt";
	// private final String id2UrlPath =
	// "C:\\Users\\v-junjzh\\ranking\\id2url.map";

	private final String invertedTablePath = ConfigUtil.get("InvertedTable");
	private final String wordToIdPath = ConfigUtil.get("WordToId");
	private final String id2UrlPath = ConfigUtil.get("Id2Url");
	private final String PAGE_FILE_PATH = ConfigUtil.get("PageFilePath");

	private SummaryService summary = null;

	public class Node {
		int did;
		int cnt;

		public Node() {
			// TODO Auto-generated constructor stub
			did = cnt = 0;
		}

		public Node(int did, int cnt) {
			this.did = did;
			this.cnt = cnt;
		}

		public int getDid() {
			return did;
		}

		public int getCnt() {
			return cnt;
		}
	}

	private Map<Integer, List<Node>> invertedTable = new HashMap<>();
	private Map<String, Integer> wordToIdTable = new HashMap<>();
	private Map<Integer, String> idToUrl = new HashMap<>();

	public RankServiceImpl() {
	}

	public void InitialTable() {
		// read invertedTable
		try {
			BufferedReader cin = new BufferedReader(
					new InputStreamReader(new FileInputStream(invertedTablePath), "utf-8"));
			String read;
			while ((read = cin.readLine()) != null) {
				String[] tmp = read.split("\\s+");
				Integer wordId = Integer.parseInt(tmp[0]);
				String[] list = tmp[1].split(";");
				List<Node> NodeList = new ArrayList<>();
				for (String element : list) {
					String[] e = element.split("@");
					int did = Integer.parseInt(e[0]);
					int cnt = Integer.parseInt(e[1]);
					NodeList.add(new Node(did, cnt));
				}
				invertedTable.put(wordId, NodeList);
			}
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(SearchServlet.ps);
		}

		try {
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(wordToIdPath), "utf-8"));
			String read;
			while ((read = cin.readLine()) != null) {
				String[] line = read.split("\\s+");
				String word = line[0];
				int id = Integer.parseInt(line[1]);
				wordToIdTable.put(word, id);
			}
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(SearchServlet.ps);
		}

		try {
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(id2UrlPath), "utf-8"));
			String read;
			while ((read = cin.readLine()) != null) {
				String[] line = read.split("\\s+");
				int id = Integer.parseInt(line[0]);
				String url = line[1];
				idToUrl.put(id, url);
			}
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace(SearchServlet.ps);
		}

	}

	public static <T> Set<T> intersection(Set<T> A, Set<T> B) {
		Set<T> ans = new HashSet<T>();
		for (T x : A)
			if (B.contains(x))
				ans.add(x);
		return ans;
	}

	public static <T> Set<T> setUnion(Set<T> A, Set<T> B) {
		Set<T> ans = new HashSet<T>();
		for (T x : A)
			ans.add(x);
		for (T x : B)
			if (!ans.contains(x))
				ans.add(x);
		return ans;
	}

	public String getTitle(int id) {
		String filePath = PAGE_FILE_PATH + id + ".title";
		String title = null;
		try {
			String curEncode = CodeUtils.getFileEncode(filePath);
			System.out.println("file " + filePath + " encode is " + curEncode);
			BufferedReader cin = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), curEncode));
			title = cin.readLine();
			title = new String(title.getBytes(curEncode), "urf-8");
			cin.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		return title;
	}

	public String getUrl(int id) {
		return idToUrl.get(id);
	}

	public String getSummary(int id, List<String> keywords) {
		if (summary == null) {
			summary = new SummaryServiceImpl();
		}
		return summary.getSummary(id, keywords);
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
		// System.out.println("=====>++>>ss " + (wordToIdTable == null) + " ; "
		// + keywords == null);
		if (invertedTable.isEmpty() || wordToIdTable.isEmpty() || idToUrl.isEmpty()) {
			InitialTable();
		}
		List<QueryResult> ans = new ArrayList<>();

		Set<Integer> documentSet = new HashSet<>();

		for (int move = 0; move <= keywords.size(); move++) {
			Set<Integer> documentSetTmp = new HashSet<>();
			for (int i = 0; i < keywords.size(); i++) {
				if (i == move)
					continue;
				if (i == 0) {
					Integer iid = wordToIdTable.get(keywords.get(i));
					if (iid == null) {
						continue;
					}
					int id = iid;
					List<Node> list = invertedTable.get(id);
					for (Node node : list) {
						documentSetTmp.add(node.getDid());
					}
				} else {
					Set<Integer> now = new HashSet<>();
					Integer iid = wordToIdTable.get(keywords.get(i));
					if (iid == null) {
						continue;
					}
					int id = iid;
					List<Node> list = invertedTable.get(id);
					for (Node node : list) {
						now.add(node.getDid());
					}
					documentSetTmp = intersection(documentSetTmp, now);
				}
			}
			documentSet = setUnion(documentSet, documentSetTmp);
		}
		/*
		 * for (int i = 0; i < keywords.size(); i++) { if (i == 0) { Integer iid
		 * = wordToIdTable.get(keywords.get(i)); if (iid == null) { continue; }
		 * int id = iid; List<Node> list = invertedTable.get(id); for (Node node
		 * : list) { documentSet.add(node.getDid()); } } else { Set<Integer> now
		 * = new HashSet<>(); Integer iid = wordToIdTable.get(keywords.get(i));
		 * if (iid == null) { continue; } int id = iid; List<Node> list =
		 * invertedTable.get(id); for (Node node : list) {
		 * now.add(node.getDid()); } documentSet = intersection(documentSet,
		 * now); } }
		 */

		int index = 0;
		Pair[] ws = new Pair[documentSet.size()];

		for (int id : documentSet) {
			ws[index] = new Pair();
			QueryResult qs = new QueryResult();
			qs.setTitle(getTitle(id));
			qs.setUrl(getUrl(id));
			qs.setSummary(getSummary(id, keywords));

			double totWeight = 0;
			for (String word : keywords) {
				System.out.println("wordToIdTable is null ? : " + wordToIdTable.isEmpty());
				System.out.println("word is null ? : " + word.isEmpty());
				Integer iix = wordToIdTable.get(word);
				if (iix == null) {
					continue;
				}
				int ix = iix;
				List<Node> list = invertedTable.get(ix);
				int wordNumber = 0;
				for (Node node : list) {
					if (node.getDid() == id) {
						wordNumber = node.getCnt();
						break;
					}
				}
				double wordWeight;
				if (StockMapUtils.getStockMap().containsKey(word))
					wordWeight = STOCK;
				else
					wordWeight = NORMAL;

				if (wordNumber == 0)
					wordWeight = -wordWeight;
				else
					wordWeight = wordWeight * Math.sqrt(wordNumber * 1.0);
				totWeight += wordWeight;
			}

			ws[index].qs = qs;
			ws[index++].weight = totWeight;
		}

		Arrays.sort(ws);
		for (Pair q : ws) {
			ans.add(q.qs);
		}

		return ans;

	}

	class Pair implements Comparable<Pair> {
		double weight;
		QueryResult qs;

		@Override
		public int compareTo(Pair o) {
			if (weight > o.weight) {
				return -1;
			} else if (weight < o.weight) {
				return 1;
			}
			return 0;
		}
	}

}