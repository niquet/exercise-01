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
	public void runLoop() {
		while (true) {
			try {
				if(selector.select()==0) {
					continue;
				}
			} catch (IOException e) {
				System.out.println("Selector died");
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
						try {
							sendToClient(key);
						} catch (IOException e) {
							System.out.println("client disconnected");
							SocketChannel client = (SocketChannel) key.channel();
							try {
								client.close();
							} catch (IOException ioException) {
								System.out.println("cant close channel");
								ioException.printStackTrace();
							}
						}
					}
				}
				if(key.isReadable()) {
					try {
						readKey(key);
					} catch (IOException e) {
						System.out.println("client disconnected");
						SocketChannel client = (SocketChannel) key.channel();
						try {
							client.close();
						} catch (IOException ioException) {
							System.out.println("cant close channel");
							ioException.printStackTrace();
						}
					}
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

			//start and initialize state machine and attach it to client key
			StateHandler state = new StateHandler();
			clientKey.attach(state);
			
			System.out.println("Accepted connection from" + client.socket().getInetAddress().getHostAddress()+".");

			// Get BEFORE server reply
			String reply = state.executeCommand("before");
			state.setByteBuffer(coder.stringToByteBufer(reply));
			//client.write(state.getByteBuffer());
			state.setReturnFlag(true);

		}catch (Exception e){
			System.out.println("Failed to accept new Client");
		}
	}

	//dummy just to get a return to server
	private void readKey(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		//System.out.println("Reading...");
		ByteBuffer buff = ByteBuffer.allocate(1024);

		//reads from client and flips buffer to read
		client.read(buff);
		buff.flip();

		// Data as string
		String data = coder.byteBufferToString(buff);
		// System.out.println(data);

		//gets Stage from channel and sets vals to return what was sent
		StateHandler state = (StateHandler) key.attachment();
		String reply;
		if (coder.getLineFlag() && state.getState().equals("RECEIVING_MESSAGE_DATA")){
			reply = state.executeCommand(data+"newline.newline");
		}else if(data.trim().equals(".") && state.getState().equals("RECEIVING_MESSAGE_DATA")){
			reply = state.executeCommand("newline.newline");
		}else{
			reply = state.executeCommand(data);
		}
		System.out.println("C:"+data);
		if(!reply.equals("")){

			System.out.println("R:"+reply);
			state.setByteBuffer(coder.stringToByteBufer(reply));
			//client.write(state.getByteBuffer());
			state.setReturnFlag(true);

		}else {
			System.out.println("no reply generated");
		}
		if(state.getState().equals("FINISHED")) {
			state.setConncetionToClose();
			client.write(state.getByteBuffer());
			client.close();
			System.out.println("Connection closed...");
		}
	}

	private void sendToClient(SelectionKey key) throws IOException {
		SocketChannel client = (SocketChannel) key.channel();
		StateHandler state = (StateHandler) key.attachment();
		client.write(state.getByteBuffer());
	}
}
