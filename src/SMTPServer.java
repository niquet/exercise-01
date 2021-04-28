import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import controller.*;
import services.*;
import utilities.*;

public class SMTPServer {

	private Selector selector;
	private ServerSocketChannel serverSock;
	private Acceptor acc;

	
	public SMTPServer(){

		//creates Selector and listens on Port 25 for connection
		try {
			selector= Selector.open();
			serverSock = ServerSocketChannel.open();
			serverSock.configureBlocking(false);
			serverSock.socket().bind(new InetSocketAddress(25));
			
			serverSock.register(selector, SelectionKey.OP_ACCEPT);
			System.out.println("Opened Socket on Port 25");
		}catch(Exception e) {
			System.out.println("Couldnt start Server");
		}
	}
	
	public void start() {
		acc = new Acceptor(selector);
		try {
			acc.runLoop();
		} catch (IOException e) {
			System.out.println("Couldnt start Loop");
		}
	}
	
	//runtime loop
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SMTPServer serv = new SMTPServer();
		serv.start();
	}

}
