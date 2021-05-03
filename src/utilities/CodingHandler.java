package utilities;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.UnsupportedCharsetException;

public class CodingHandler {
	private Charset messageCharset = null;
	
	public CodingHandler(String charSet) {
		try {
			this.messageCharset = Charset.forName(charSet);
		}catch(UnsupportedCharsetException e) {
			System.out.println("Can't parse Charset");
		}
	}

	public ByteBuffer stringToByteBufer(String s) {
		byte[] retVal;
		retVal=s.getBytes(messageCharset);
		ByteBuffer buff = ByteBuffer.allocate(retVal.length);
		buff.put(retVal);
		buff.flip();
		return buff;
	}
	
	//doesnt work
	public String byteBufferToString(ByteBuffer buff) {
		CharsetDecoder dec = messageCharset.newDecoder();
		CharBuffer charBuff = null;
		try {
			charBuff = dec.decode(buff);
		} catch (CharacterCodingException e) {
			System.out.println("Cant decode Buffer");
		}
		return charBuff.toString();
	}
}
