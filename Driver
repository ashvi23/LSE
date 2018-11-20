package lse;

import java.io.*;
import java.util.*;

public class Driver 
{
	static Scanner sc = new Scanner(System.in);
	
	static String getOption() 
	{
		System.out.print("getKeyWord(): ");
		String response = sc.next();
		return response;
	}
	
	public static void main(String args[])
	{
		LittleSearchEngine lse = new LittleSearchEngine();
		
		try
		{
			lse.makeIndex("docs.txt", "noisewords.txt");
		} 
		catch (FileNotFoundException e)
		{
		}		
		
//		String input;
		System.out.println("loaded words: ");
		for (String hi : lse.keywordsIndex.keySet())
			System.out.println(hi+" "+lse.keywordsIndex.get(hi));
//				
//		while (!(input = getOption()).equals("q"))
//		{
//				System.out.println(lse.getKeyWord(input));
//		}
		System.out.println();
		System.out.println();
		System.out.println();
		
		System.out.print("top5 results: PLEASE ");
		System.out.println(lse.top5search("eithghfhgdfer", "lITTLE"));
	}
}
