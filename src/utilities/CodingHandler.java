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

	public byte[] stringToByteBufer(String s) {
		byte[] retVal;
		retVal=s.getBytes(messageCharset);
		return retVal;
	}
	
	//doesnt work
	public String byteBufferToString(ByteBuffer buff) {
		CharsetDecoder decoder = this.messageCharset.newDecoder();
		CharBuffer charBuff = null;
		try {
			charBuff = decoder.decode(buff);
		} catch (CharacterCodingException e) {
			System.out.println("Can't decode Buffer");
		}
		return charBuff.toString();
	}
}
