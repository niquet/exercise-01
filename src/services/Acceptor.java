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
import utilities.StateHandler;

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

				if(key.isWritable()) {
					StateHandler state = (StateHandler) key.attachment();
					if(state.getReturnFlag()) {
						sendToClient(key);
					}
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
			StateHandler state = new StateHandler();
			clientKey.attach(state);
			
			System.out.println("Accepted connection from" + client.socket().getInetAddress().getHostAddress()+".");

			//start and initialize state machine
			state.advance();

		}catch (Exception e){
			System.out.println("Failed to accept new Client");
		}
	}

	//dummy just to get a return to server
	private void printToConsole(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		System.out.println("Reading...");
		ByteBuffer buff = ByteBuffer.allocate(64);

		//reads from client and flips buffer to read
		client.read(buff);
		buff.flip();

		//prints to consol
		String data = coder.byteBufferToString(buff).trim();
		System.out.println(data);

		//gets Stage from channel and sets vals to return what was sent
		StateHandler state = (StateHandler) key.attachment();
		state.setByteBuffer(coder.stringToByteBufer("From Server:"+data));
		state.setReturnFlag(true);

		//chacks for "exit to close connection"
		if(data.equalsIgnoreCase("exit")) {
			client.close();
			System.out.println("Connection closed...");
		}
	}

	private void sendToClient(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		StateHandler state = (StateHandler) key.attachment();
		client.write(state.getByteBuffer());
		state.setReturnFlag(false);
	}
}
