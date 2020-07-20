package application;

import static java.util.stream.Collectors.toMap;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class WordCounterServer 
{

	/**
	 * Method to initiate server side connection with the word counter client.
	 * @param args
	 */
	public static void main(String[] args) 
	{
		try 
		{
			ServerSocket server2 = new ServerSocket(2112);
			Socket fromClient2 = server2.accept();
			// Receive the map from the client
		    final InputStream yourInputStream = fromClient2.getInputStream(); // InputStream from where to receive the map, in case of network you get it from the Socket instance.
		    final ObjectInputStream mapInputStream = new ObjectInputStream(yourInputStream);
		    final Map<String, Integer> counterMap = (Map) mapInputStream.readObject();
			// Sort the map in decreasing order of value
			Map<String, Integer>sorted = counterMap
			        .entrySet()
			        .stream()
			        .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
			        .collect(
			            toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
			                LinkedHashMap::new));
			
			// Send map back to client via a socket instance
			final OutputStream yourOutputStream = fromClient2.getOutputStream();
		    final ObjectOutputStream mapOutputStream = new ObjectOutputStream(yourOutputStream);
		    mapOutputStream.writeObject(sorted);
		    server2.close(); 
		} 
		catch (Exception e)
		{
			System.out.println("Something went wrong on the server side. I suppose I should say something like"
					+ "'Consult your local network admin for help' to sound official.");
		}
	}

}
