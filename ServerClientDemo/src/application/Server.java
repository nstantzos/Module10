package application;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.Scanner;

public class Server {

	public static void main(String[] args) throws IOException, ClassNotFoundException 
	{
		// Initialize variables
		int number = 0;
		String result;
		boolean flag = false;
		
		// Create socket, using the same port as the client
		ServerSocket server = new ServerSocket(2112);
		
		// Look for connection from the client. Program will hold here until a connection is made
		Socket fromClient = server.accept();
		
		// Get stream from the client side, store the integer
		Scanner inputFromClient = new Scanner(fromClient.getInputStream());
		
		//sortedWords = inputFromClient.
		number = inputFromClient.nextInt();
		
		// Loop through numbers between 2 and the entered number to look for primes
		for(int i = 2; i <= number/2; i++)
		{
			// Check for non-prime number
			if(number % i ==0)
			{
				flag = true;
				break;
			}
		}
		
		// Set result string
		if (!flag)
		{
			result = number + " is a prime number.";
		}
		else
		{
			result = number + " is not a prime number.";
		}
		
		// Print stream object to grab the output stream from the client side		
		PrintStream p = new PrintStream(fromClient.getOutputStream());
		
		// Print result to stream for client to receive
		p.println(result);
		
		// Close server socket
		server.close();		
	}

}
