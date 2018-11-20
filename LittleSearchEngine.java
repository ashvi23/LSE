package lse;

import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */

	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) 
			throws FileNotFoundException
			{
				HashMap<String, Occurrence> hmap = new HashMap<String, Occurrence>();
				Scanner sc = new Scanner(new File(docFile));
				
				while (sc.hasNext()){ 
					String key = getKeyword(sc.next());
				
					if (key != null)
					{
						if (hmap.containsKey(key)){
							Occurrence frq= hmap.get(key);
							frq.frequency++;
							hmap.put(key, frq);
						}
						else {
							Occurrence first = new Occurrence (docFile, 1);
							hmap.put(key, first);
						}
					
					}
			}	
			for(String key: hmap.keySet()) {
				System.out.println("key : " + key + " value "+ hmap.get(key));
			}
			sc.close();
			return hmap;
		}
	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {

		Iterator<String> loop = kws.keySet().iterator();
		
		while (loop.hasNext()) {
			ArrayList <Occurrence> occList = new ArrayList <Occurrence>();
			String key = (String)loop.next();
			
			if (keywordsIndex.containsKey(key)) {
				
				Occurrence toAdd = kws.get(key);
				occList = keywordsIndex.get(key);
				occList.add(toAdd);
				insertLastOccurrence(occList);
				keywordsIndex.put(key, occList);	
			}
			
			else {
				Occurrence toAdd = kws.get(key);
				occList.add(toAdd);
				keywordsIndex.put(key, occList);
			}
		}
	}
	
	/**
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation, consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
		int lastLet;
		//|| word.length()< 0 || !(Character.isLetter(word.charAt(0))) || (word.length() == 0 && !(Character.isLetter(word.charAt(0))))
		if (word == null ||containsNum(word)==true || word.length()< 0 || !(Character.isLetter(word.charAt(0))) || (word.length() == 0 && !(Character.isLetter(word.charAt(0))))  ) {
			return null;
		}
		
		word = word.toLowerCase();
		if(containsNum(word)==true) {
			return null;
		}
		if (containsPunc(word) == true) {
			
			lastLet = lastLetter(word); 
			if (lastLet == -1) {
				return null;
			}
			else {
				if (containsPunc(word.substring(0, lastLet+1)) == true) { // checks if punctuation occurred in the start of middle of word
					return null;
				}
				word = word.substring(0, lastLet+1);
				// skip trailing punc
				
				}
		}
		//System.out.println("pre noise words");
		
		if (noiseWords.contains(word)) {
		//	System.out.println("goin in");
			return null;
		}
		
		//System.out.println("word: " + word);
		//word = isNoise(word);
		//System.out.println("word after: " + word);
		if(word == null) {
			return null;
		}
		
		if (word.length() <= 1) {
			return null;
		}
	//	System.out.println(word);
		return word;
	}
	
	// helper methods for getKeyWord
	private boolean containsNum(String word) {
		int wordLength = word.length();
		for (int i = 0; i < wordLength; i++) {
			if ((Character.isDigit(word.charAt(i)))==true){
				return true;
			}	
		}
		return false;
	}
	private boolean containsPunc(String word) {
		int wordLength = word.length();
		for (int i = 0; i < wordLength; i++) {
			if ((Character.isLetter(word.charAt(i)))==false){
				return true;
			}	
		}
		return false;
	}
	
	private int lastLetter(String word ) {
		for (int i = word.length() -1 ; i >=0 ; i--) {
			char c = word.charAt(i);
			if (Character.isLetter(c)) {
				return i;
			}
		}
		return -1; 
	}
	
	
	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in t9he list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		/** COMPLETE THIS METHOD **/
		
		if (occs.size() ==1 ) {
			return null;
		}
		
		ArrayList<Integer> midpoints = new ArrayList<Integer>();
		int left = 0;
				
		int right = occs.size()-2;
			
		int target = occs.get(occs.size()-1).frequency ;
				
		Integer mid =0;
		
		while ( left<= right) {
			mid = (left+right)/2;
					
			midpoints.add(mid);
				
			
			if (occs.get(mid).frequency== target) {
			
				break;
			}
			else if (occs.get(mid).frequency < target) {
				right = mid-1 ;
				
			}
			else if (occs.get(mid).frequency > target) {
				left = mid+1 ;
			}
		}
		if (occs.get(mid).frequency== target) {
			occs.add(mid, occs.get(occs.size()-1));
		}
		else if (occs.get(mid).frequency < target) {
			occs.add(mid, occs.get(occs.size()-1) );
		}
		else if (occs.get(mid).frequency > target) {
			occs.add(mid+1, occs.get(occs.size()-1) );
			
		}
		
		occs.remove(occs.size()-1);
		
		return midpoints;
	}
	
	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}
	
	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. (Note that a
	 * matching document will only appear once in the result.) Ties in frequency values are broken
	 * in favor of the first keyword. (That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2
	 * also with the same frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, returns null.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {

		
		kw1 = kw1.toLowerCase();
		kw2 = kw2.toLowerCase();
		ArrayList<String> final5 = new ArrayList<String>(5); 
		
		if((!(keywordsIndex.containsKey(kw1) ) && !keywordsIndex.containsKey(kw2))) {
		
			return final5;
		}
		
		ArrayList<String> final20 = new ArrayList<String>(20);
		ArrayList<Occurrence> firstOcc = new ArrayList<Occurrence>();
		ArrayList<Occurrence> secondOcc = new ArrayList<Occurrence>();
	
		if(keywordsIndex.containsKey(kw1)) {
			firstOcc = keywordsIndex.get(kw1);
			if(firstOcc.size()>10) {
				firstOcc.subList(10, firstOcc.size()).clear();
			}
		}
		
		if(keywordsIndex.containsKey(kw2)) {
			secondOcc = keywordsIndex.get(kw2);
			if (secondOcc.size()>10) {
				secondOcc.subList(10, secondOcc.size()).clear();
			}
		}
		if (firstOcc.size()==1 && secondOcc.size() ==0 ) {
			final5.add(firstOcc.get(0).document);
		}
		else if (secondOcc.size()==1 && firstOcc.size() ==0 ) {
			final5.add(secondOcc.get(0).document);
		}
		
		if (firstOcc.size()==0 && secondOcc.size() != 0) {
			
			for (int i =0; i< secondOcc.size(); i++) {
				if (final5.size() == 5) {
					break;
				}
				final5.add(secondOcc.get(i).document);
			}			
		}
		
		else if (firstOcc.size() !=0 && secondOcc.size() ==0) {
			for (int i =0; i< firstOcc.size(); i++) {
				if (final5.size()== 5) {
					break;
				}
				final5.add(firstOcc.get(i).document);	
			}	
		}
		
		int frq1;
		int frq2;
		
		while((firstOcc.size() != 0 || (secondOcc.size() !=0))) {
			
			
			if (firstOcc.size()==0 && secondOcc.size()>0) {
			//	frq2 = secondOcc.get(0).frequency;
				for (int i =0; i< secondOcc.size(); i++) {
				final20.add(secondOcc.get(0).document);
				
				secondOcc.remove(0);
				}
			}
			else if (firstOcc.size()>0 && secondOcc.size()==0) {
				//frq1 = firstOcc.get(0).frequency;
				for (int i =0; i< firstOcc.size(); i++) {
				final20.add(firstOcc.get(0).document);
				firstOcc.remove(0);
				}
			}
			else {
				frq1 = firstOcc.get(0).frequency ;
				frq2 = secondOcc.get(0).frequency;
			if (frq1 > frq2) {
				final20.add(firstOcc.get(0).document);
				if (firstOcc.size()>=1) {
					firstOcc.remove(0);
					}
			}
			else if (frq2> frq1) {
				final20.add(secondOcc.get(0).document);
				if (secondOcc.size()>=1) {
				secondOcc.remove(0);
				}
			}
			else if (frq1 == frq2) {
				final20.add(firstOcc.get(0).document);
				if (firstOcc.size()>=1) {
				firstOcc.remove(0);
				}
				final20.add(secondOcc.get(0).document);
				if (secondOcc.size()>=1) {
				secondOcc.remove(0);	
				}
			}
			}
		}
		for (int i =0; i< final20.size(); i++) {
			System.out.println("Final 20 words are: " + final20.get(i)+ " , ");
		}
		
		if (final20.size()<5) {
		
			while (final20.size()>0) {
				
				if (final5.contains(final20.get(0))) {
					
					final20.remove(0);
				}
				else if(!(final5.contains(final20.get(0)))) {
					final5.add(final20.get(0));
					final20.remove(0);
				}
				
			}
		}
		else if (final20.size()>5){
		while(final20.size() > 0) {
			if (final5.size() == 5) {
				break;
			}
			if(!(final5.contains(final20.get(0)))) {
				final5.add(final20.get(0)); //adds to end
				final20.remove(0);
			}
			else if (final5.contains(final20.get(0))) {
				final20.remove(0);
			}
		}
		}
	
	return final5;
	}
	
}
