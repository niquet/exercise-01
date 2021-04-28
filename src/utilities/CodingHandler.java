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
			System.out.println("Cant parse Charset");
		}
	}

	public byte[] fromString(String s) {
		byte[] retVal;
		retVal=s.getBytes(messageCharset);
		return retVal;
	}
	
	//doesnt work
	public String toString(ByteBuffer buff) {
	/*	CharsetDecoder dec = messageCharset.newDecoder();
		CharBuffer charBuff = null;
		try {
			charBuff = dec.decode(buff);
		} catch (CharacterCodingException e) {
			System.out.println("Cant decode Buffer");
		}
		return charBuff.toString();*/
		return null;
	}
}
