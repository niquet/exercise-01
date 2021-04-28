package services;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

import controller.StagingController;
import utilities.CodingHandler;
import utilities.Stage;

public class Acceptor {
	private Selector selector;
	private CodingHandler coder;

	public Acceptor(Selector selector) {
		this.selector=selector;
		this.coder = new CodingHandler("US-ASCII");
	}
	
	//run Loop iterating over readySet
	public void runLoop() throws IOException {
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
				
				if(key.isReadable()) {
					printToConsole(key);
				}
				iter.remove();
			}
		}
	}
	
	//accepts new CLients on new socket and adds keys
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
			State state = new State();
			clientKey.attach(state);
			
			System.out.println("Accepted connection from" + client.socket().getInetAddress().getHostAddress()+".");
		}catch (Exception e){
			System.out.println("Failed to accept new Client");
		}
	}
	
	//dummy just to get a return to server
	private void printToConsole(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		System.out.println("Reading...");
		ByteBuffer buff = ByteBuffer.allocate(1024);
		client.read(buff);
		String data = new String(buff.array()).trim();
		System.out.println(data);
		if(data.equalsIgnoreCase("exit")) {
			client.close();
            System.out.println("Connection closed...");
		}
	}
}
