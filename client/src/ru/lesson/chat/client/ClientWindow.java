package ru.lesson.chat.client;

import ru.lesson.chat.network.TCPConnection;
import ru.lesson.chat.network.TCPConnectionListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientWindow extends JFrame implements ActionListener, TCPConnectionListener {

	private static final String IP = "192.168.1.21";
	private static final int PORT = 8189;
	private static final int WIDTH = 600;
	private static final int HEIGHT = 400;

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new ClientWindow();
			}
		});
	}

	private final JTextArea log = new JTextArea();
	private final JTextField nickName = new JTextField("unknown");
	private final JTextField input = new JTextField();

	private TCPConnection connection;

	private ClientWindow() {
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(WIDTH, HEIGHT);
		setLocationRelativeTo(null);
		setAlwaysOnTop(true);
		log.setEditable(false);
		log.setLineWrap(true);
		add(log, BorderLayout.CENTER);
		input.addActionListener(this);
		add(input, BorderLayout.SOUTH);
		add(nickName, BorderLayout.NORTH);
		setVisible(true);
		try {
			connection = new TCPConnection(this, IP, PORT);
		} catch (IOException e) {
			printMessage("Connection exception: " + e);
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		String msg = input.getText();
		if (msg.equals("")) return;
		input.setText(null);
		connection.sendString(nickName.getText() + ": " + msg);
	}

	@Override
	public void onConnectionReady(TCPConnection tcpConnection) {
		printMessage("Connection ready...");
	}

	@Override
	public void onReceiveString(TCPConnection tcpConnection, String value) {
		printMessage(value);
	}

	@Override
	public void onDisconnect(TCPConnection tcpConnection) {
		printMessage("Connection close...");
	}

	@Override
	public void onException(TCPConnection tcpConnection, Exception e) {
		printMessage("Connection exception: " + e);
	}

	private synchronized void printMessage(String msg) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				log.append(msg + "\n");
				log.setCaretPosition(log.getDocument().getLength());
			}
		});
	}
}
