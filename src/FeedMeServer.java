import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;
import javax.swing.*;

public class FeedMeServer {

	private ServerSocket serverSocket;
	private Socket socket;

	public static void main(String[] args) {
		new FeedMeServer();
	}

	public FeedMeServer() {

		try {
			serverSocket = new ServerSocket(8000);
		}
		catch (BindException e) {
			JOptionPane.showMessageDialog(null, "server already running");
		}catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println("Server started.  \n\n");
		
		// connect to database
		SQLQuery.connect("root", "12345678");

		while (true) {

			// accept new connection request
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}

			// display new connected client (with IP and time)
			System.out.println("\nnew client (" + socket.getInetAddress().getHostAddress() + ") - " + new Date());

			// create a new task for the connection
			new Thread(new ServeAClient(socket)).start();

		}
	}
}
