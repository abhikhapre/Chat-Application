/*
* Name : Abhishek Jaykrishna Khapare.
* WIU ID : 915-19-1447.
* Project : Multi User Chat Application
* Server : Server recieves connection request from client and then 
*            establishes a connection with clients. server then recieves a message from client 
*            check for destination and forwards the message to destination or broadcast
*            the message if requested
*/

/**
 *
 * @author Abhishek
 */
import java.awt.EventQueue;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;


import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Server extends JFrame {
	private Map<String, Connection> connections = new HashMap<>();
	private int port;
	private ServerSocket serverSocket;
	private Thread listener;

	private JPanel contentPane;
	private JTextField textField;
	private JButton btnStart;
	private JButton btnStop;
	private JLabel labelDisplay;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Server frame = new Server();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public Server() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 180);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
                
		JLabel lblPortNumber = new JLabel("Port Number:");
		lblPortNumber.setBounds(61, 26, 104, 18);
		contentPane.add(lblPortNumber);
		
		textField = new JTextField();
		textField.setBounds(179, 23, 135, 24);
		contentPane.add(textField);
		textField.setColumns(10);
		
		btnStart = new JButton("Start");
		btnStart.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					port = Integer.parseInt(textField.getText().trim());
				}
				catch (NumberFormatException e) {
					labelDisplay.setText("Please set an available port");
					return;
				}
				listener = new Thread(new Listener());
				listener.start();
				btnStop.setEnabled(true);
				btnStart.setEnabled(false);
				labelDisplay.setText("Server is running");
			}
		});
		btnStart.setBounds(71, 60, 113, 27);
		contentPane.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				try
				{
					serverSocket.close();
					listener.interrupt();
					listener = null;
					for (String key : connections.keySet())
					{
						connections.get(key).socket.close();
					}
				}
				catch (IOException exception)
				{
					
				}
				labelDisplay.setText("");
				connections.clear();
				btnStart.setEnabled(true);
				btnStop.setEnabled(false);
			}
		});
		btnStop.setEnabled(false);
		btnStop.setBounds(201, 60, 113, 27);
		contentPane.add(btnStop);
		
		labelDisplay = new JLabel("");
		labelDisplay.setBounds(61, 102, 268, 18);
		contentPane.add(labelDisplay);
	}
	
	public class Listener implements Runnable
	{

		@Override
		public void run() {
			try
			{
				serverSocket = new ServerSocket(port);
				
                                while(true)
				{
					Socket socket = serverSocket.accept(); //
					Connection connection = new Connection(socket);//create connection
					connections.put(connection.getClientName(), connection);
					new Thread(connection).start();//create multiple clients as a threads
					connection.furtherSend(String.join("::", connections.keySet().toArray(new String[connections.size()])));//to store the client names into combo box
					connection.furtherSend(connection.getClientName() + " has entered the chat room \n");
				}
			}
			catch (IOException e)
			{
				
			}
		}
		
	}
	
	public class Connection implements Runnable {
		private Socket socket;
		private DataInputStream inputStream;
		private DataOutputStream outputStream;
		private String clientName;
		
		public String getClientName() {
			return clientName;
		}
		
		public Connection(Socket socket) throws IOException {
			this.socket = socket;
			inputStream = new DataInputStream(socket.getInputStream());
			outputStream = new DataOutputStream(socket.getOutputStream());
			clientName = inputStream.readUTF(); //read client name
		}
		
		@Override
		public void run() {
			try
			{
				while(socket.isConnected())
				{
					String message = inputStream.readUTF();
					//forward message to client
					if (message.contains("->"))
					{
                                            
						if (connections.containsKey(message.split("->")[0]))
						{
							//send to source
                                                        send(clientName + ": " + message.split("->")[1]);
							//send message to destination
                                                        connections.get(message.split("->")[0]).send(clientName + ": " + message.split("->")[1]);
						}
                                                else //send to all
                                                {
                                                furtherSend(clientName + ": " + message.split("->")[1]);
                                                }
					}
					else
					{
						furtherSend(clientName + ": " + message);
					}
				}
			}
			catch(IOException exception)
			{
				try
				{
					socket.close();
					connections.remove(this);
					furtherSend(clientName + " quits the chat room \n");
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
		
                //send message to all clients
		private void furtherSend(String message) throws IOException {
			for (String key : connections.keySet())
			{
				connections.get(key).send(message);;
			}
		}
		//write to client
		private void send(String message) throws IOException
		{
			outputStream.writeUTF(message);
		}
	}
}
