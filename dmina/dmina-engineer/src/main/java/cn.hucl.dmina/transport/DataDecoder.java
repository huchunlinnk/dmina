package cn.hucl.dmina.transport;

/**
 * decode the message received
 * 
 * @author hucl
 * @since 2013-12-19
 */
import cn.hucl.dmina.data.*;
import cn.hucl.dmina.util.*;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Arrays;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class DataDecoder extends CumulativeProtocolDecoder {
	//final Logger logger = LoggerFactory.getLogger(DataDecoder.class);

	private static CharsetDecoder csd = Charset.forName("UTF-8").newDecoder();

	private PacketFactory packetFactory;

	private Boolean udp;

	public DataDecoder(PacketFactory factory, Boolean isUDP) {
		packetFactory = factory;
		udp = isUDP;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput out) throws Exception {
		Message message = new Message();
		in.setAutoExpand(true);
		// here we have put the packet size in buffer if protocol is tcp/ip
		if (udp || in.prefixedDataAvailable(4)) {
			if (!decodePacketHeader((in), message))
				in.reset();
			Packet packet = decodePacket(in, message);
			out.write(packet);
			return true;
		} else {
			in.mark();
			//logger.debug("Waiting for more data : " + in.getInt());
			in.reset();
			return false;
		}
	}

	private boolean decodePacketHeader(IoBuffer in, Message message)
			throws CharacterCodingException {
		in.skip(4);
		byte[] inArray = new byte[Message.getHeaderSize()];
		in.get(inArray);
		short msgProtocol = TypeConvert.getShort(TypeConvert
				.reverseBytes(Arrays.copyOfRange(inArray, 0, 2)));
		short msgVersion = TypeConvert.getShort(TypeConvert.reverseBytes(Arrays
				.copyOfRange(inArray, 2, 4)));
		short msgType = TypeConvert.getShort(TypeConvert.reverseBytes(Arrays
				.copyOfRange(inArray, 4, 6)));
		short msgLength = TypeConvert.getShort(TypeConvert.reverseBytes(Arrays
				.copyOfRange(inArray, 6, 8)));
		byte[] msgSeqNum = Arrays.copyOfRange(inArray, 8, 24);
//		byte[] msgEpid = TypeConvert.reverseBytes(Arrays
//				.copyOfRange(inArray, 24, 40));
		byte[] msgEpid =Arrays
				.copyOfRange(inArray, 24, 40);
		byte msgEncrypt = Arrays.copyOfRange(inArray, 40, 41)[0];
		byte msgCompress = Arrays.copyOfRange(inArray, 41, 42)[0];
		byte msgReverse1 = Arrays.copyOfRange(inArray, 42, 43)[0];
		byte msgReverse2 = Arrays.copyOfRange(inArray, 43, 44)[0];
		if (msgProtocol != 0x0001 || msgVersion < 0)
			return false;
		if (msgLength < Message.getHeaderSize()
				|| msgLength > Packet.MAX_MSG_LENGTH)
			return false;
		message.setProtrocol(new Short(msgProtocol));
		message.setVersion(msgVersion);
		message.setType(msgType);
		message.setLength(msgLength);
		message.setSeqnum(msgSeqNum);
		message.setEpid(msgEpid);
		message.setEncrypt(msgEncrypt);
		message.setCompress(msgCompress);
		message.setReverse1(msgReverse1);
		message.setReverse2(msgReverse2);
		return true;
	}

	private Packet decodePacket(IoBuffer in, Message message)
			throws CharacterCodingException {
		// the length-headersize will be the body and crc32
		byte[] msgBodyAndCrc = new byte[message.getLength()
				- Message.getHeaderSize()];
		in.get(msgBodyAndCrc);
		byte[] msgBody = Arrays.copyOfRange(msgBodyAndCrc, 0,
				msgBodyAndCrc.length - 4);
		msgBody=TypeConvert.trimEndNull(msgBody);
		byte[] crc32 = Arrays.copyOfRange(msgBodyAndCrc,
				msgBodyAndCrc.length - 4, msgBodyAndCrc.length);
		String strMsgBody = new String(msgBody, CharSetUtil.UTF8CHARSET);
		message.setBody(strMsgBody);
		message.setCrc32(TypeConvert.getInt(TypeConvert.reverseBytes(crc32)));
		return message;
	}

	public static byte[] ioBufferToByte(Object message) {
		if (!(message instanceof IoBuffer)) {
			return null;
		}
		IoBuffer ioBuffer = (IoBuffer) message;
		byte[] b = new byte[ioBuffer.position()];
		ioBuffer.get(b);
		return b;
	}

	private Object readBlob(IoBuffer buffer) {
		int size = buffer.getInt();
		byte[] data = new byte[size];
		buffer.get(data);
		return data;
	}

}
