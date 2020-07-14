package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Client 
{

	public static void main(String[] args) throws UnknownHostException, IOException 
	{
		// Set variables
		int number = 0;
		String result;
		
		// Scanner object 
		Scanner userInput = new Scanner(System.in);
		// Create socket, define IP and port number specific to the machine you want to talk to
		// "localhost" specifies this machine
		Socket s = new Socket("localhost",2112);
		// Scanner object to get stream from the server
		Scanner fromServer = new Scanner(s.getInputStream());
		System.out.println("Enter any number");
		
		try
		{
			// Set the user-entered number to a local variable
			number = userInput.nextInt();
		}
		catch (Exception e)
		{
			System.out.println("Error. Please enter an integer.");
		}		
		
		// Create PrintStream object and initialize with the socket stream
		PrintStream p = new PrintStream(s.getOutputStream());
		
		// Print the number to the stream to be picked up by the server
		p.println(number);
		
		// Get the stream from the server
		result = fromServer.nextLine();
		userInput.close();
		s.close();
		System.out.println(result);
		
	}

}
