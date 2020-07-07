package application;

/**
 * Class for retrieving keys and values from the sorted map.
 * @author NickS
 *
 */
public class FetchedWords 
{
	public Integer PrimaryKey;
	public String Key;
	public Integer Value;
	
	public FetchedWords()
	{
		
	}
	
	public FetchedWords(int PrimaryKey, String Key,int Value)
	{
		this.PrimaryKey = PrimaryKey;
		this.Key = Key;
		this.Value = Value;
	}
}
