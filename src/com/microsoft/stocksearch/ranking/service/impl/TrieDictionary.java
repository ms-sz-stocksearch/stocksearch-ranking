package com.microsoft.stocksearch.ranking.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.microsoft.stocksearch.ranking.service.Dictionary;

import sun.nio.cs.ext.MacTurkish;

public class TrieDictionary extends Dictionary {

	private Node head;
	
	@Override
	public String maxMatch(String str) {
		return match(str);
	}

	

	@Override
	public void build(List<String> list) {
		head = new Node();
		for (String str : list) {
			Node current = head;
			for (char c : str.toCharArray()) {
				if (!current.next.containsKey(c)) {
					current.next.put(c, new Node());
				}
				current = current.next.get(c);
			}
			current.end = true;
		}
	}

	public String match(String str) {
		String lastMatch = null;
		Node current = head;
		int index = 0;
		for (char c : str.toCharArray()) {
			index++;
			if (current.next.containsKey(c)) {
				current = current.next.get(c);
				if (current.end) {
					lastMatch = str.substring(0, index);
				}
			} else {
				break;
			}
		}
		return lastMatch;
	}

	class Node {
		Map<Character, Node> next;
		boolean end;

		public Node() {
			next = new HashMap<>();
			end = false;
		}

	}

}
