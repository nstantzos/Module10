package application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for the WordSearch method in main. 
 * @author NickS
 *
 */
class WordSearchTest 
{

	@Test
	void test() 
	{
		// Create new Main instance for testing
		Main mainTest = new Main();
		
		// Initialize bool variable to false
		boolean wordWasFoundTest = false;
		
		// Create Map, FetchedWords objects and initialize for testing.
		// This test will populate a map with strings and integers representing word frequencies
		Map<String, Integer> sortedWords = Map.of("Dog",4,"Cat",2,"Bird",1);
		FetchedWords fetchedWords = new FetchedWords();
		
		// Representative text of user input
		String textInputTest = "Cat";
		
		// Call the WordSearch method, search through the map for the specified key and return the value
		// If the submitted text is found, the wordWasFoundTest should be set as true
		wordWasFoundTest = mainTest.WordSearch(textInputTest, sortedWords, fetchedWords);
		
		// If the result is not equal to true, then the test fails
		assertEquals(true, wordWasFoundTest);
	}

}
