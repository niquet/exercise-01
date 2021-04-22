package com.SMTP_Server.Server;
import java.io.IOException;
import java.nio.channels.*;
import java.util.*;
import java.net.InetSocketAddress;

public class Server {
	
	private Selector selector;
	private ServerSocketChannel serverSock;

	
	public Server() throws IOException{

		//creates Selector and listens on Port 25 for connection
		selector= Selector.open();
		serverSock = ServerSocketChannel.open();
		serverSock.configureBlocking(false);
		serverSock.socket().bind(new InetSocketAddress(25));
		
		serverSock.register(selector, SelectionKey.OP_ACCEPT);
		runLoop();
	}
	
	//runtime loop
	private void runLoop() throws IOException {
		while (true) {
			if(selector.select()==0) {
				continue;
			}
			
			Set<SelectionKey> selectKeys = selector.selectedKeys();
			Iterator<SelectionKey> iter = selectKeys.iterator();
			
			while(iter.hasNext()) {
				SelectionKey key = iter.next();
				
				if(key.isAcceptable()){
					acceptClient(key);
				}
				
				iter.remove();
			}
		}
	}
	
	private void acceptClient(SelectionKey key) {
		ServerSocketChannel serv = (ServerSocketChannel) key.channel();
		SocketChannel client;
		try {
			//accept client
			client = serv.accept();
			client.configureBlocking(false);
			
			//register client for read and write
			SelectionKey clientKey = client.register(selector, SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			
			//create new Stage instance and attach it to client key
			Stage fresh = new Stage();
			clientKey.attach(fresh);
			
			System.out.print("Accepted connection from" + client.socket().getInetAddress().getHostAddress()+".");
		}catch (Exception e){
			System.out.print("Failed to accept new Client");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
