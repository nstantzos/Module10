package application;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.Test;

/**
 * Test class for the SortDictionary method in main.
 * @author NickS
 *
 */
class SortedDictionaryTest 
{

	@Test
	void test() 
	{
		// Create new Main instance for testing
		Main mainTest = new Main();

		// Create unsorted Map for testing
		// This test will populate a map with strings and integers representing word frequencies
		Map<String, Integer> unsortedWords = Map.of("Bird",1,"Cat",2,"Dog",4);
						
		// Call the SortDictionary method while passing in an unsorted dictionary (map)
		Map<String, Integer> sortedWords = mainTest.SortDictionary(unsortedWords);
				
		// Make use of the iterator method to grab the first indexed item in the map
		Map.Entry<String,Integer> entry = sortedWords.entrySet().iterator().next();
		String key = entry.getKey();
		int value = entry.getValue();
		
		// Grab the first indexed item from the unsorted words map
		Map.Entry<String,Integer> unsortedEntry = unsortedWords.entrySet().iterator().next();
		
		// Print the first entry of each list (unsorted and sorted) to console
		System.out.println("Unsorted map first entry: ");
		System.out.println(unsortedEntry.getKey() + ", " + unsortedEntry.getValue());
		System.out.println();
		System.out.println("Sorted map first entry: ");
		System.out.println(key + ", " + value);
		
		// If the result is not equal to dog, then the test fails (this means that the map was
		// not sorted properly)
		assertEquals("Dog", key);
		assertEquals("4", value);
	}

}
