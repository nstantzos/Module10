package application;
	
import static java.util.stream.Collectors.toMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


/**
 * The purpose of this program is to read in text from a website that contains 
 * the poem "The Raven" by Edgar Allan Poe and count the number of occurrences 
 * of each word. The user will be presented with a main window in which they 
 * can enter a word into a text box and be given the frequency count of that 
 * word within "The Raven". The user can also click a button that will display 
 * a bar chart with the top 20 most frequently occurring words. The full 
 * frequency results are also displayed in the console window.
 * 
 * 5.1.0 Update
 * Under the guise that the process of sorting the <String, Integer> map is
 * very resource demanding, a feature was added that will send the word map
 * to a representative "server" to be sorted. The map is then returned to the 
 * client for further processing and entry into the SQL database.
 * @author NickS
 * @version 5.1.0
 *
 */
public class Main extends Application 
{
	// Declare global Map for use in the start method
	static Map<String, Integer> sortedWords;
	// Declare global ArrayList to store data collected from database
	static ArrayList<FetchedWords> fetchedWords;
	
	// Global flag to be accessed by multiple methods
	public boolean wordWasFound;
	
	/**
	 * Method to handle all JavaFX GUI implementation. Contains calls to methods 
	 * that will build the main window and bar chart.
	 */
	@Override
	public void start(Stage primaryStage) 
	{
		// Call method to set up the second scene containing the full bar chart
				Scene barChartScene = BarChartConfiguration();
				
				// Create fetched words object to store key and value for user defined search
				FetchedWords fetchedWords = new FetchedWords();
			    
				// Create a text box
				TextField wordEntry = new TextField();
				
				Label mainSceneText = new Label("Enter a word. Then click the button to calculate how often"
						+ " that word appears in the poem 'The Raven'. Alternatively, click the bottom"
						+ " button to see word frequency statistics for the top 20 words in the poem.");
				mainSceneText.setWrapText(true);
				
				// Create a label to update with the specified word's statistics. Initialize with no text
				final Label wordStatisticsLabel = new Label("");
				wordStatisticsLabel.setText("");
				
				// Create a label containing whitespace for aesthetics
				Label blankLabel = new Label("");

				// Create button to display the bar chart showing the top 20 word statistics
				Button button = new Button("Total Word Frequency");
				
				// Associate an action with this button. This uses a lambda expression (at least I think
				// that this is Java's version of lambda expressions) to call a different scene to be set
				// in the primaryStage
				button.setOnAction(e ->  primaryStage.setScene(barChartScene));
				
				// Create a second button to perform calculations on the user-defined word
				Button button2 = new Button("Entered Word Frequency");

				// This button calls an event handler that contains a method
				button2.setOnAction(new EventHandler<ActionEvent>()
				{
					@Override
					public void handle(ActionEvent arg0) 
					{

						// A bool flag is returned after calling the method to search the map for the word
						// entered by the user
						wordWasFound = WordSearch(wordEntry.getText(), sortedWords, fetchedWords);
						try
						{
							// Check if the word that the user entered exists in the map
							// If yes, update a label with the frequency of the word
							if (wordWasFound)
							{
								wordStatisticsLabel.setText(fetchedWords.Value.toString());
							}
							else 
							{
								wordStatisticsLabel.setText("0");
							}
							
						}
						catch (Exception e)
						{
							System.out.println("Error. No word entered.");
						}
					}
					
				});
				
				System.out.println();
				
				// Vertical box for the main scene
				VBox mainPage = new VBox(10);
				
				// Add controls to the vertical box and format the padding
				mainPage.getChildren().addAll(mainSceneText,wordEntry, wordStatisticsLabel,button2,blankLabel,button);
				mainPage.setPadding(new Insets(20,20,20,20));
				
				// Add the vertical box to the main scene and set the dimensions
				Scene scene = new Scene(mainPage, 300, 300);
				mainSceneText.prefHeightProperty().bind(scene.heightProperty());
				primaryStage.setScene(scene);
				primaryStage.setTitle("The Raven Word Count Statistics");
				primaryStage.show();
	}
	
	/**
	 * Method for constructing the bar chart to be displayed after the user clicks a button
	 * @return Returns a scene containing a formatted bar chart.
	 */
	private Scene BarChartConfiguration() 
	{
		// Create axes
	    CategoryAxis xAxis    = new CategoryAxis();
	    xAxis.setLabel("Word");
	    NumberAxis yAxis = new NumberAxis();
	    yAxis.setLabel("Number of Occurences");

	    // Add axes to bar chart
	    BarChart barChart = new BarChart(xAxis, yAxis);

	    // Create Series object for data to be loaded into
	    XYChart.Series algorithmData = new XYChart.Series();
	    
//	    sortedWords.entrySet().forEach(entry->
//	    {
//	    	if (entry.getValue() > 7) 
//	    	{
//		    	algorithmData.getData().add(new XYChart.Data(entry.getKey(), entry.getValue()));
//	    	}
//	    });
	    // Add data to the series from the ArrayList of FetchedWords. This ArrayList was populated from the database
	    for(FetchedWords w : fetchedWords)
	    {
	    	// Coincidentally, grabbing values above 7 leaves us with the top 20 most frequent words
	    	if (w.Value > 7)
	    	{
		    	algorithmData.getData().add(new XYChart.Data(w.Key,w.Value));
	    	}
	    }

	    // Add data to chart
	    barChart.getData().add(algorithmData);
	    
	    // Organize children into vertical line
	    VBox barVerticalBox = new VBox(barChart);

	    // Add vertical box to scene
	    Scene sceneTwo = new Scene(barVerticalBox, 800, 400);
		return sceneTwo;
	}
	

	/**
	 * Main method of the WordCounterDatabase program.
	 * @param args Not used
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	public static void main(String[] args) throws MalformedURLException, IOException 
	{		
        //Create table within database
        CreateTable();
        
		// Call method to read in URL object and parse text into a dictionary/hash map
		Map<String, Integer> counterMap = ParseText();
		
		// Call method to sort the dictionary into descending frequency of word usage
	    sortedWords = SortDictionary(counterMap);
	 		    
        // Call method to insert words into the database
        InsertWords(sortedWords);
        
        // Call method to retrieve the words that were previously entered into the database
        fetchedWords = RetrieveWordsFromDatabase();
	    
	    // Print the sorted dictionary contents to the console window
	    PrintDictionaryToConsole(sortedWords);	
        
	    // Launch GUI portion of code
		launch(args);
	}
	
	/**
	 * Method to handle sorting within the dictionary (map), such that the resulting map is sorted
	 * from highest to lowest frequency words. This method sends a map to the "server" for processing.
	 * @param counterMap The map to be sorted. Should contain strings as keys and integers as values.
	 * @return Returns a sorted map (string, integer) in decreasing value order.
	 */
	public static Map<String, Integer> SortDictionary(Map<String, Integer> counterMap) 
	{
		// This chunk of code will send a hash map to the "server"
		try 
		{
			Socket t = new Socket("localhost",2112);
			System.out.println("Initialized socket on the client side");
			// Map to send
		    final OutputStream yourOutputStream = t.getOutputStream(); // OutputStream where to send the map in case of network you get it from the Socket instance.
		    final ObjectOutputStream mapOutputStream = new ObjectOutputStream(yourOutputStream);
		    mapOutputStream.writeObject(counterMap);
		    
		    // Receive the map back from the server, from a socket instance
		    final InputStream yourInputStream = t.getInputStream();
		    final ObjectInputStream mapInputStream = new ObjectInputStream(yourInputStream);
		    final Map<String, Integer> sorted = (Map) mapInputStream.readObject();
		    t.close();
		    
		    return sorted;
		} 
		catch (Exception e)
		{
			System.out.println("Error in connecting to server. Make sure to run the WordCounterServer.jar file before this .jar.");
			return counterMap;
		}
	}
	
	/**
	 * Method to print dictionary to console window. Future feature: print dictionary to .csv file
	 * @param sorted Sorted (descending value order) map containing string keys and integer values.
	 */
	private static void PrintDictionaryToConsole(Map<String, Integer> sorted) 
	{
		// Format the console text into columns
		System.out.printf("%-10s %-10s\n", "Word", "Number of Occurrences");
		System.out.printf("%-10s %-10s\n", "----", "---------------------");
		    
		// Print out the key and values from the hash map
//		sorted.entrySet().forEach(entry->
//		{
//			System.out.printf("%-10s %-10s\n", entry.getKey(),entry.getValue());
//		});
		// Print out the key and value using the new ArrayList that took data from the database
		for(FetchedWords i : fetchedWords)
		{
			System.out.printf("%-10s %-10s\n", i.Key, i.Value );
		}
		
	}
	
	/**
	 * Method that reads URL object and parses text into a hash map. Lots 
	 * of extraneous characters/text must be removed
	 * @return Returns an unsorted map of string keys and integer values. 
	 * The keys represent every word that appears in the poem, while the 
	 * values represent the number of times they appear in the poem.
	 * @throws MalformedURLException Thrown when the specified URL is invalid.
	 * @throws IOException Thrown when errors occur in opening the URL stream.
	 */
	private static Map<String, Integer> ParseText() throws MalformedURLException, IOException 
	{
			// Load URL object using specified website.
			URL oracle = new URL("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm");
			
			// Open stream to read text from URL object
			BufferedReader in = new BufferedReader(new InputStreamReader(oracle.openStream()));
			
			// Create new RegexMatcher object, compile stored patterns
			RegexMatcher regexmatcher = new RegexMatcher();
			Pattern firstLineOfPoem = Pattern.compile(regexmatcher.PoemBegin);
			Pattern lastLineOfPoem = Pattern.compile(regexmatcher.PoemEnd);
			Pattern BRTagsRegex = Pattern.compile(regexmatcher.BRTags);
			Pattern ParagraphTagRegex = Pattern.compile(regexmatcher.ParagraphTags);
			Pattern ItalicsRegex = Pattern.compile(regexmatcher.Italics);
			Pattern ItalicsAfterRegex = Pattern.compile(regexmatcher.ItalicsAfter);
			Pattern EndingDashRegex = Pattern.compile(regexmatcher.MDashAfter);
			
			String inputLine;
			
			// Create hash map object to store keys and values
			Map<String, Integer> counterMap = new HashMap<>();
			
			// Flag for parsing body of poem. Used to distinguish between poem body and other website text
			boolean doWordAnalysis = false;
			
			// Create array strings for text processing
			ArrayList<String> words = new ArrayList<String>();
			ArrayList<String> processedWords = new ArrayList<String>();
			
			while ((inputLine = in.readLine()) != null) 
			{
				// Regex matcher objects. If found, they indicate the start and end of the poem
				Matcher poemStart = firstLineOfPoem.matcher(inputLine);
				Matcher poemEnd = lastLineOfPoem.matcher(inputLine);
				
				// If the beginning of the poem is found (defined in RegexMatcher.java class)
				if(poemStart.find()) 
				{
					// Set bool flag
					doWordAnalysis = true;
					
					// Split line on spaces and add to a string array
					String[] wordsInLine = inputLine.split(" ");
					for (String i : wordsInLine) 
					{
						// Remove excess whitespace strings
						String withoutspace = i.replaceAll("\\s", "");
						if (i.contentEquals("")) 
						{
							// Don't add split strings with no characters
						}
						else 
						{
							// Add everything else
							words.add(i);
						}
					}
				}
				// If the end of the poem is found
				else if(doWordAnalysis && poemEnd.find()) 
				{
					// Set bool flag
					doWordAnalysis = false;
					
					// Remove excess whitespace strings
					String[] wordsInLine = inputLine.split(" ");
					for (String i : wordsInLine) 
					{
						String withoutspace = i.replaceAll("\\s", "");
						if (i.contentEquals("")) 
						{
							// Don't add split strings with no characters
						}
						else 
						{
							// Add everything else
							words.add(i);
						}
					}				
				}
				// If the text processing flag is true
				else if (doWordAnalysis && !poemEnd.find()) 
				{
					String[] wordsInLine = inputLine.split(" ");
					for (String i : wordsInLine) 
					{
						// Remove excess whitespace strings
						String withoutspace = i.replaceAll("\\s", "");
						if (i.contentEquals("")) 
						{
							// Don't add split strings with no characters
						}
						else 
						{
							// Add everything else
							words.add(i);
						}
					}
				}
			}
			
			// Close the file stream
			in.close();
			
			// List of deleted words, used for error checking
			ArrayList<String> deletedWords = new ArrayList<String>();
			
			// Loop through 
			for (String i : words) 
			{
				// This weeds out text phrases/characters that are undesirable. Certain lines contain font/formatting
				// html code, and this if-statement is intended to weed those out. This would not be required if I knew
				// of a html-parser package in Java that would do this for me.
				if (!i.contains("margin-left:") && !i.contains("%") && !i.contains("<SPAN") && !i.contains("CLASS")) 
				{
					// Regex patterns for paragraph tags, line break tags, italic tags, and specially hyphenated text
					Matcher ParagraphTag = ParagraphTagRegex.matcher(i);
					Matcher BRTag = BRTagsRegex.matcher(i);
					Matcher ItalicsTags = ItalicsRegex.matcher(i);
					Matcher ItalicsAfterTags = ItalicsAfterRegex.matcher(i);
					Matcher EndingDash = EndingDashRegex.matcher(i);

					// If a line contains a line break tag
					if (BRTag.find()) 
					{
						// Find the line break text and remove it
						String str = i.replaceAll(regexmatcher.BRTags, "$1");
						
						// Also check for a specially hyphenated text block after the line break is removed
						Matcher EndingDashSubString = EndingDashRegex.matcher(str);
						if(EndingDashSubString.find()) 
						{
							// Remove the specially hyphenated text
							String substr = str.replaceAll(regexmatcher.MDashAfter, "$1");
							
							// Add cleaned up string to processed word list
							processedWords.add(substr);
						}
						else 
						{
							// If no specially hyphenated text is found after the line break tag is removed, add the string
							// to the processed word list
							processedWords.add(str);
						}
					}
					// If a paragraph tag is found
					else if(ParagraphTag.find()) 
					{
						// Don't add paragraph tags
						deletedWords.add(i);
					}
					// If an italic tag is found at the beginning and end of the string
					else if(ItalicsTags.find()) 
					{
						// Remove the italic tag
						String str = i.replaceAll(regexmatcher.Italics, "$1");
						processedWords.add(str);
					}
					// If an italic tag is found in the middle of the string, after some text
					else if(ItalicsAfterTags.find()) 
					{
						// Remove the italic tags
						String str = i.replaceAll(regexmatcher.ItalicsAfter, "$1$2");
						processedWords.add(str);
					}
					// If a special hyphen is found at the end of the text
					else if(EndingDash.find()) 
					{
						// Remove specially hyphenated text
						String str = i.replaceAll(regexmatcher.MDashAfter, "$1");
						// Note that this does not find special hyphens in the middle of the string (dealt with later)
						processedWords.add(str);
					}
					// For all other clean strings
					else 
					{
						// All other strings are added
						processedWords.add(i);
					}

				}
				// If any of the specified patterns are found, add the string to a deleted words list
				else 
				{
					// Don't add words with undesired characters listed above
					deletedWords.add(i);
				}
			}
			
			// Array list for removing punctuation
			ArrayList<String>postProcessedWords = new ArrayList<String>();
			
			// Loop through list of post processed words
			for (String word : processedWords) 
			{
				// Split each string upon the special hyphens to separate the strings into multiple entries
				String newWords[] = word.split("&mdash;");
				
				// If the new string array containing split text has elements
				if (newWords.length > 0) 
				{
					// Loop through each of those elements
					for (String i : newWords) 
					{
						// Check for (and remove) <SPAN> tags (I think these are used for CSS markups? not sure)
						String str = i.replaceAll("(.*?)</SPAN>", "$1");
						postProcessedWords.add(str);
					}
				}
				else
				{
					postProcessedWords.add(word);
				}
			}
			
			// Loop through the post processed words to remove punctuation and add keys and increment values to the hash map
			for(String word : postProcessedWords)
			{
				// Use regex to replace (remove) punctuation at the beginning and end of the string
				String punctuationSuffix = word.replaceAll("^(.*?)\\p{Punct}+$", "$1");
				String punctuationPrefix = punctuationSuffix.replaceAll("^\\p{Punct}+(.*?)$", "$1");
				
				// If a key doesn't exist in a hashmap, null is returned
				if(counterMap.get(punctuationPrefix) == null) 
				{
				    // At this point, we know that this key doesn't exist, so create a new entry with 1 as the count
				    counterMap.put(punctuationPrefix, 1);
				} 
				else 
				{
				    //We know this key already exists. Get the existing value and increment it, then update
				    //the value
				    int count = counterMap.get(punctuationPrefix);
				    counterMap.put(punctuationPrefix, count + 1);
				}
				
			}
			return counterMap;
	}
	 
	/**
	 * Method to search the map for the user-entered word. This method returns a bool flag, which
	 * indicates if the entered word was found. If yes, it updates a label in the GUI with the word
	 * frequency. 
	 * @param inputValue Parameter passed to method to retrieve word that user entered in the GUI text box.
	 * @param sortedWords The map containing sorted keys and values (words and frequencies) from the poem.
	 * @param fetchedWords Class containing fields to retrieve key value pairs from the sorted list.
	 * @return Returns a boolean to indicate if the entered word was found in the map.
	 */
	public boolean WordSearch(String inputValue, Map<String, Integer> sortedWords, FetchedWords fetchedWords) 
	{
		try 
		{
			// Get text from the text box
			String word = inputValue;
			
			// Print a message to the console window affirming the entered word with the user
			System.out.println("Calculating frequency of " + word + " within 'The Raven'...");
			
			if(sortedWords.containsKey(word))
			{
				// Retrieve frequency count from map and print it to the console
				fetchedWords.Value = sortedWords.get(word);
				System.out.println("The entered word appears " + fetchedWords.Value + " times.");
				return true;
			}
			else
			{
				System.out.println("Error: The word you entered does not exist within the poem. "
						+ "Please make sure the word you typed is punctually correct.");
				return false;
			}
		}
		catch (Exception e)
		{
			System.out.println("Error: Invalid input.");
			return false;
		}
	}
	

	/**
	 * Method that fetches words from the database and stores them in an ArrayList of type FetchedWords
	 * @return Returns an ArrayList object of type FetchedWords. 
	 */
    public static ArrayList<FetchedWords> RetrieveWordsFromDatabase()
    {
        Connection connection = null;
        Statement statement = null;
        //Make an array list full of FetchedWords objects
        ArrayList<FetchedWords> fetchedWords = new ArrayList<>();

        try 
        {
        	// Initialize connection and SQL statement
            connection = GetConnection();
            connection.setAutoCommit(false);
            statement = connection.createStatement();
            
            //Query all rows of data from words table
            ResultSet words = statement.executeQuery("SELECT * FROM words");
            int counter = 0;
            
            while ( words.next())
            {
            	// Add each word to the words array list
            	fetchedWords.add(new FetchedWords(words.getInt("ID"), words.getString("WORD"), words.getInt("FREQUENCY")));
            	counter++;
            }
            
            // Print out word totals
            System.out.println("There are a total of " + counter + " unique words in 'The Raven', listed below.");

            words.close();
            statement.close();
            connection.close();
        } 
        catch ( Exception e ) 
        {
            System.out.println(e);
            fetchedWords = null;
        }

        // Optional code for printing database values to the console
//        for(FetchedWords i : fetchedWords)
//        {
//        	System.out.println("Primary Key: " + i.PrimaryKey + " Word: " + i.Key + " Value: " + i.Value);
//        }
        
        return fetchedWords;

    }
    
    
    /**
     * Method for inserting parsed words into the database
     * @param sortedWords Map of String,Integer keys and values
     */
    public static void InsertWords(Map<String,Integer> sortedWords)
    {
    	// Print out the key and values from the hash map
    	sortedWords.entrySet().forEach(entry->
    	{
            try 
            {
				ExecuteSQLStatement(entry);
			}
            catch (SQLException e) 
            {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	});
    	System.out.println("Words entered into SQL database, under the 'words' table");
    }

    /**
     * Method for executing SQL statements 
     * @param entry Map containing words and frequencies to be entered via SQL into database
     * @throws SQLException
     */
	private static void ExecuteSQLStatement(Entry<String, Integer> entry) throws SQLException 
	{
		// Define strings and ints to retrieve data from SQL database
		String sqlKey = null;
		int sqlValue = 0;
		
		// Check word for apostrophes. Single apostrophes must be doubled, as SQL does not accept single apostrophes
		if(entry.getKey().contains("'")) 
		{
			String singleQuoteWord=entry.getKey();  
			// Replace single quote with two single quotes to tell SQL that we want a single quote character in the string
			String replacementString=singleQuoteWord.replaceAll("'","''");
			sqlKey = replacementString;
		}
		else 
		{
			sqlKey = entry.getKey();
		}
		
		// Set the frequency value
		sqlValue = entry.getValue();
		Connection connection = null;
		Statement statement = null;
		connection = GetConnection();
		
		//This setting allows SQL statements to be grouped instead of individual transactions
		connection.setAutoCommit(false);
		
		statement = connection.createStatement();
		
		// Old code for printing database data to the console
		//System.out.printf("%-10s %-10s\n", entry.getKey(),entry.getValue());
		
		//Construct SQL string to insert word data into table
		String sql = "INSERT INTO words (WORD,FREQUENCY) " +
		        "VALUES ("+
		              "'" + sqlKey + "'," +
		                  + sqlValue + ")";
		statement.executeUpdate(sql);
		statement.close();
		//Commit the SQL statement group before closing
		connection.commit();
		connection.close();
	}

	/**
	 * Method to create a table within the database
	 * @return Returns a connection object
	 */
    public static Connection CreateTable()
    {
        Connection connection = null;
        Statement statement = null;
        try 
        {
            connection = GetConnection();
            
            statement = connection.createStatement();
            //Drop the table in the database before creating it to make sure nothing is overwritten or duplicated
            statement.execute("DROP TABLE IF EXISTS words");
			//Construct SQL string to initialize table with columns and expected value types
            String sql = "CREATE TABLE words " +
                         "(ID INTEGER PRIMARY KEY        AUTOINCREMENT, " +
                         "WORD           CHAR(30)   NOT NULL, " +
                         "FREQUENCY      INT                ) ";
            statement.executeUpdate(sql);
            statement.close();
            connection.close();

            System.out.println("Table created successfully");
        }
        catch (Exception e)
        {
            System.out.println(e);
            connection = null;
        }

        return connection;
    }

    /**
     * Method to open a connection to the locally hosted database. This will need to be changed before it can be run on a different machine
     * @return
     */
    public static Connection GetConnection()
    {
        Connection connection = null;
        try
        {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/Users/NickS/Nicks Stuff/School stuff/Valencia/CEN 3024C 33719/Repos/Module10/Module10/WordCounterDatabase/sqlite/db/MyDatabase.db");
            //System.out.println("Opened database successfully");
        }
        catch (Exception e)
        {
            System.out.println(e);
            connection = null;
        }

        return connection;
    }
	
	
}
