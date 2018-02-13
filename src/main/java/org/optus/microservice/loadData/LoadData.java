package org.optus.microservice.loadData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.optus.microservice.loadData.utility.ValueComparatorUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vishal
 *
 */
@RestController
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class LoadData {

	private static Scanner fileScanner;
	private static final String DELIMITER = "|";
	private static final String NEW_LINE = "\n";

	@Autowired
	private Environment environment;
	
	@RequestMapping("/loadFile")
	public Map<String, Integer> readFile (){

		
		String filePath = environment.getProperty("source.file");

		Map<String, Integer> countMap = null;

		try {
			File inputFile = new File(filePath);
			fileScanner = new Scanner(inputFile);
			
			countMap = new HashMap<String, Integer>();
			
			while (fileScanner.hasNextLine()) {
				String nextLine = fileScanner.nextLine();
				String[] words = nextLine.split("\\W+");
				List<String> wordList = new ArrayList<String>(Arrays.asList(words));

				for (String keyWord : wordList) {
					if (countMap.containsKey(keyWord.toLowerCase())) {
						countMap.put(keyWord.toLowerCase(), countMap.get(keyWord.toLowerCase()) + 1);
					} else {
						countMap.put(keyWord.toLowerCase(), 1);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// If something goes wrong fetching file
			e.printStackTrace();
		}
		return countMap;	
	}
	
	/**
	 * Task 1: Search the following texts, which will return the counts respectively.	
	 * @param keyWordList
	 * @return
	 */
	@RequestMapping(value="/wordCounter/{requestString}")
	public Map<String, Integer> selectedUserCount (@PathVariable("requestString") List<String> keyWordList){
		
		Map<String, Integer> allWords = readFile();

		Map<String, Integer> countMap = new HashMap<String, Integer>();
		for (String keyWord : keyWordList) {
			if (allWords.containsKey(keyWord.toLowerCase())) {
				countMap.put(keyWord, allWords.get(keyWord.toLowerCase()));
			}
		}
		return countMap;		
	}
	
	/**
	 * Task 2: Provide the top 20 (as Path Variable) Texts, which has the
	 * highest counts in the Sample Paragraphs provided.
	 * @param count
	 * @return
	 */
	@RequestMapping(value="/topWordsCounter/{count}")
	public Map<String, Integer> listTopFewWords (@PathVariable("count") String count){
		
		Map<String, Integer> allWordsMap = readFile();

		HashMap<String, Integer> sortedWordsMap = (HashMap<String, Integer>) ValueComparatorUtility.sortByValue(allWordsMap, Long.parseLong(count));

		writeToCsvFile(sortedWordsMap);
		
		return sortedWordsMap;
	}

	private void writeToCsvFile (HashMap<String, Integer> sortedWordsMap){
		// HashMap implements serializable, so can directly use for ObjectStream
		
		String filePath = environment.getProperty("output.file");

		try{

			FileWriter fileWriter = new FileWriter(filePath);
			for (Map.Entry<String, Integer> entry : sortedWordsMap.entrySet()) {
				fileWriter.append(entry.getKey())
						  .append(DELIMITER)
						  .append(String.valueOf(entry.getValue()))
						  .append(NEW_LINE);			
				}
			fileWriter.flush();
			fileWriter.close();
		    }catch(Exception e){
		    	System.out.println(e.getMessage());
		    }
		
	}

}