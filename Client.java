
/*
* Name : Abhishek Jaykrishna Khapare.
* WIU ID : 915-19-1447.
* Project : Multi User Chat Application
* Client : Clients establishes connection with server 
*           and exchanges messages with all the clients connected to server 
*           and can send a message to a perticular client.
*
*/

/**
 *
 * @author Abhishek
 */
import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import javax.swing.JTextArea;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JComboBox;

public class Client extends JFrame {
	private String ip;
	private int port;
	private Socket socket;
	private String name;
	private DataInputStream inputStream;
	private DataOutputStream outputStream;

	private JPanel contentPane;
	private JTextField textFieldIp;
	private JTextField textFieldPort;
	private JTextField textField;
	private JTextArea textArea;
	private JTextField textFieldMessage;
	private JButton btnConnect;
	private JButton btnSend;
	private JComboBox<String> comboBox;

	
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Client frame = new Client();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Client() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 500, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 123, 494, 237);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		scrollPane.setViewportView(textArea);
		
		JLabel lblServerIp = new JLabel("Server IP:");
		lblServerIp.setBounds(0, 13, 88, 18);
		contentPane.add(lblServerIp);
		
		textFieldIp = new JTextField();
		textFieldIp.setBounds(110, 10, 233, 24);
		contentPane.add(textFieldIp);
		textFieldIp.setColumns(10);
		
		JLabel lblServerPort = new JLabel("Server Port:");
		lblServerPort.setBounds(0, 50, 102, 18);
		contentPane.add(lblServerPort);
		
		textFieldPort = new JTextField();
		textFieldPort.setBounds(110, 47, 153, 24);
		contentPane.add(textFieldPort);
		textFieldPort.setColumns(10);
		
		btnConnect = new JButton("Connect");
		btnConnect.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				ip = textFieldIp.getText().trim();
				port = Integer.parseInt(textFieldPort.getText().trim());
				name = textField.getText().trim();
				if (ip.isEmpty() || name.isEmpty())
					return;
				try {
					socket = new Socket(ip, port);
					inputStream = new DataInputStream(socket.getInputStream());
					outputStream = new DataOutputStream(socket.getOutputStream());
					outputStream.writeUTF(name);
					Connection connection = new Connection();
					new Thread(connection).start();
					btnSend.setEnabled(true);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnConnect.setBounds(322, 83, 113, 27);
		contentPane.add(btnConnect);
		
		btnSend = new JButton("Send");
		btnSend.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String message = (String)comboBox.getSelectedItem() + "->";
				message += textFieldMessage.getText().trim() + "\n";
				try {
					outputStream.writeUTF(message);
                                        textFieldMessage.setText("");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		btnSend.setEnabled(false);
		btnSend.setBounds(356, 406, 113, 27);
		contentPane.add(btnSend);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(0, 87, 72, 18);
		contentPane.add(lblName);
		
		textField = new JTextField();
		textField.setBounds(110, 84, 153, 24);
		contentPane.add(textField);
		textField.setColumns(10);
		
		textFieldMessage = new JTextField();
		textFieldMessage.setBounds(10, 407, 333, 24);
		contentPane.add(textFieldMessage);
		textFieldMessage.setColumns(10);
		
		comboBox = new JComboBox();
		comboBox.setBounds(10, 373, 201, 24);
		contentPane.add(comboBox);
	}
	
		//display the messages which are sent by other clients
		public class Connection implements Runnable {

			@Override
			public void run() {
				try
				{
					while(socket.isConnected())
					{
						//read message
						String message = inputStream.readUTF();
					
                                                if (message.contains("::"))//add to combobox
						{
							String[] names = message.split("::");
							comboBox.removeAllItems();
                                                        comboBox.addItem("All");
							for (int i = 0; i < names.length; i++)
							{
								if (!names[i].equals(name))
								{
									comboBox.addItem(names[i]);
								}
							}
						}
                                                else //display message
						{
							textArea.append(message);
						}
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}//Connection run ends
			
		}//Connection ends
}//Client ends
