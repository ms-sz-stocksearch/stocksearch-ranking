package com.microsoft.stocksearch.ranking.service.impl;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;



import com.microsoft.stocksearch.ranking.service.SummaryService;
import com.microsoft.stocksearch.ranking.servlets.SearchServlet;
import com.microsoft.stocksearch.ranking.utils.ConfigUtil;

public class SummaryServiceImpl implements SummaryService {
	String HTMLContent;
	static final int SummaryLength=80;
	
	private static final String PAGE_FILE_PATH = ConfigUtil.get("PageFilePath");
	
	private String readFileContent(String fileName) throws IOException 
	{

		File file = new File(fileName);
		InputStream is = new FileInputStream(file);  
        InputStreamReader isr = new InputStreamReader(is, resolveCode(fileName) ); 
        
		BufferedReader bf = new BufferedReader(isr);

		String content = "";
		StringBuilder sb = new StringBuilder();

		while(content != null)
		{
			content = bf.readLine();
	
			if(content == null)
			{
				break;
			}
	
			sb.append(content.trim());
		}

		bf.close();
		return sb.toString();
	}
		
	public static String htmlRemoveTag(String inputString) {
		if (inputString == null)
			return null;
		String htmlStr = inputString; // 含html标签的字符串
		String textStr = "";
		java.util.regex.Pattern p_script;
		java.util.regex.Matcher m_script;
		java.util.regex.Pattern p_style;
		java.util.regex.Matcher m_style;
		java.util.regex.Pattern p_html;
		java.util.regex.Matcher m_html;
		try {
			//定义script的正则表达式{或<script[^>]*?>[\\s\\S]*?<\\/script>
			String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>"; 
			//定义style的正则表达式{或<style[^>]*?>[\\s\\S]*?<\\/style>
			String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>"; 
			String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式
			p_script = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			m_script = p_script.matcher(htmlStr);
			htmlStr = m_script.replaceAll(""); // 过滤script标签
			p_style = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			m_style = p_style.matcher(htmlStr);
			htmlStr = m_style.replaceAll(""); // 过滤style标签
			p_html = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			m_html = p_html.matcher(htmlStr);
			htmlStr = m_html.replaceAll(""); // 过滤html标签
			textStr = htmlStr;
		} catch (Exception e) {
			e.printStackTrace(SearchServlet.ps);
		}
		return textStr;// 返回文本字符串
	}
	public static String resolveCode(String path)  {  
		try
		{
	        InputStream inputStream = new FileInputStream(path);    
	        byte[] head = new byte[3];    
	        inputStream.read(head);      
	        String code = "gb2312";  //或GBK  
	        if (head[0] == -1 && head[1] == -2 )    
	            code = "UTF-16";    
	        else if (head[0] == -2 && head[1] == -1 )    
	            code = "Unicode";    
	        else if(head[0]==-17 && head[1]==-69 && head[2] ==-65)    
	            code = "UTF-8";    
	            
	        inputStream.close();  
	          
	        System.out.println("Source HTML Code: "+ code);   
	        return code;  
		}
		catch (Exception e)
		{
		}
		return "";
		
    }
	public String getSummary(int id,List<String> keywords)
	{
		int StartIndex=0;
		int MaxScore=-1;
		try { 
			String path = PAGE_FILE_PATH;
			HTMLContent=readFileContent(path + Integer.toString(id)+".html");
			HTMLContent=htmlRemoveTag(HTMLContent);
			for (int i=0;i<HTMLContent.length()-SummaryLength+1;i++)
			{
				int NowScore=0;
				for (int j=0;j<SummaryLength;j++)
					for (String NowWord:keywords)
					{
						if (i+j+NowWord.length()-1 >= i+SummaryLength-1 ) continue;

						if (NowWord.equals( HTMLContent.substring(i+j,i+j+NowWord.length()) ) )
							NowScore++;
					}
				if (NowScore>MaxScore)
				{
					MaxScore=NowScore;
					StartIndex=i;
				}
			}
			return HTMLContent.substring(StartIndex, StartIndex+SummaryLength);
			
		} catch (Exception e) {
			e.printStackTrace(SearchServlet.ps);
		}
		return "";
	}

}
