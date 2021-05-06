package utilities;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;
import java.sql.SQLOutput;

public class CodingHandler {
	private Charset messageCharset = null;
	private boolean lineFlag =false;
	
	public CodingHandler(String charSet) {
		try {
			this.messageCharset = Charset.forName(charSet);
		}catch(UnsupportedCharsetException e) {
			System.out.println("Can't parse Charset");
		}
	}

	public ByteBuffer stringToByteBufer(String s) {
		byte[] retVal;
		s=s+"\r\n";
		retVal=s.getBytes(messageCharset);
		ByteBuffer buff = ByteBuffer.allocate(retVal.length);
		buff.put(retVal);
		buff.flip();
		return buff;
	}
	

	public String byteBufferToString(ByteBuffer buff) {
		this.lineFlag=false;
		if (checkLine(buff)){
			this.lineFlag=true;
		}
		CharsetDecoder dec = messageCharset.newDecoder();
		CharBuffer charBuff = null;
		try {
			charBuff = dec.decode(buff);
		} catch (CharacterCodingException e) {
			System.out.println("Cant decode Buffer");
		}
		String retVal = charBuff.toString();
		return retVal;
	}

	private boolean checkLine(ByteBuffer buffer) {

		int pos = buffer.position();
		for(int i = 0; i < buffer.limit(); i++) {
			if(buffer.get(i) == '\n') {
				if((i-1) >= 0 && buffer.get(i-1) == '\r') {
					if((i-2) >= 0 && buffer.get(i-2) == '.') {
						if((i-3) >= 0 && buffer.get(i-3) == '\n') {
							if((i-4) >= 0 && buffer.get(i-4) == '\r') {
								return true;
							}
						}
					}
				}
			}
		}

		return false;
	}

	public boolean getLineFlag(){return this.lineFlag;}
}
