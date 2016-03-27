package hht.dss.commmodule.transport;

import hht.dss.commmodule.data.Message;
import hht.dss.commmodule.data.Packet;
import hht.dss.commmodule.util.CharSetUtil;
import hht.dss.commmodule.util.MessageFieldInfo;

import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoder;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class DataEncoder implements ProtocolEncoder {
	//final Logger logger = LoggerFactory.getLogger(DataEncoder.class);

	private static CharsetEncoder cse = Charset.forName("UTF-8").newEncoder();

	private boolean udp = false;

	public DataEncoder(Boolean isUDP) {
		udp = isUDP;
	}

	public void dispose(final IoSession arg0) throws Exception {
	}

	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
		Packet packet = (Packet) message;

		if (!packet.isValid()) {
			throw new PacketNotValidException();
		}

		int packetSize = packet.getSize();

		IoBuffer buffer = IoBuffer.allocate(packetSize + 4);
		// buffer.setAutoExpand(true);
		encodePacket(packet, buffer, true);

		buffer.flip();
		out.write(buffer);
	}

	/**
	 * Write an Packet into a data buffer.
	 * 
	 * For HTTP transmission set the writeSize flag to true so that the size of
	 * the packet is written before the data.
	 * 
	 * @param packet
	 *            An Packet
	 * @param buffer
	 *            The buffer to write into
	 * @param writeSize
	 *            Whether to send the size of the data packet before the data
	 *            itself . if Tcp write ,otherwise ignore
	 * @throws CharacterCodingException
	 */
	public void encodePacket(Packet packet, IoBuffer buffer, boolean writeSize)
			throws CharacterCodingException {
		//logger.debug("Encode Packet , valid : " + packet.isValid());
		// if tcp , write the size
		if (true) {
			buffer.putInt(packet.getSize());
		}
		encodeMessage((Message) packet, buffer);
	}

	/**
	 * Write Message into a buffer,note that the message size shouldn't be put
	 * into the buffer again as it has been put in in the last method , of
	 * course only when the transport protocol is tcp
	 * 
	 * @param message
	 * @param buffer
	 * @throws CharacterCodingException
	 */
	private void encodeMessage(Message message, IoBuffer buffer)
			throws CharacterCodingException {
		// we can't put msg size in this step
		write(message, buffer);
	}

	/**
	 * Protocol unrelated implement
	 * 
	 * @param arg
	 * @param buffer
	 * @throws CharacterCodingException
	 */
	private void write(Object obj, IoBuffer buffer)
			throws CharacterCodingException {
		try {
			Message msg=(Message)obj;
			buffer.putShort(msg.getProtrocol());
			buffer.putShort(msg.getVersion());
			buffer.putShort(msg.getType());
			buffer.putShort(msg.getSize());
			buffer.put(msg.getSeqnum());
			buffer.put(msg.getEpid());
			buffer.put(msg.getEncrypt());
			buffer.put(msg.getCompress());
			buffer.put(msg.getReverse1());
			buffer.put(msg.getReverse2());
			byte[] bytes = msg.getBody().getBytes(CharSetUtil.UTF8CHARSET);
			buffer.put(bytes);
			padBuffer(bytes.length,buffer);
			buffer.putInt(msg.getCrc32());
			
//			List<MessageFieldInfo> list = GetClassProperties
//					.getAllProperties(obj);
//			
//			for (MessageFieldInfo msgFieldInfo : list) {
//				for (Field field : obj.getClass().getDeclaredFields()) {
//					if (msgFieldInfo.getName()
//							.equalsIgnoreCase(field.getName())) {
//						writeUnitData(msgFieldInfo, buffer);
//					}
//				}
//			}

		} catch (Exception e) {

		}

	}

	/**
	 * write the data bytes into buffer,of course this method is not so profile.
	 * the best way to implement the function is using key words of instanceof
	 * 
	 * @param msgFieldInfo
	 * @param buffer
	 */
	private void writeUnitData(MessageFieldInfo msgFieldInfo, IoBuffer buffer) {
		String msgFieldType = msgFieldInfo.getType();
		if (msgFieldType.equals("Byte")) {
			byte value = (Byte) msgFieldInfo.getValue();
			buffer.put(value);
		}
		// byte array
		if (msgFieldType.equals("Bytes")) {
			byte[] value = (byte[]) msgFieldInfo.getValue();
			buffer.put(value);
		} else if (msgFieldType.equals("Short")) {
			short value = (Short) msgFieldInfo.getValue();
			buffer.putShort(value);
		} else if (msgFieldType.equals("Integer")) {
			int value = (Integer) msgFieldInfo.getValue();
			buffer.putInt(value);
		} else if (msgFieldType.equals("Long")) {
			long value = (Long) msgFieldInfo.getValue();
			buffer.putLong(value);
		} else if (msgFieldType.equals("Float")) {
			float value = (Float) msgFieldInfo.getValue();
			buffer.putFloat(value);
		} else if (msgFieldType.equals("Double")) {
			double value = (Double) msgFieldInfo.getValue();
			buffer.putDouble(value);
		} else if (msgFieldType.equals("String")) {
			String value = (String) msgFieldInfo.getValue();
			byte[] bytes = value.getBytes(CharSetUtil.UTF8CHARSET);
			buffer.put(bytes);
			// align the memory with 4
			padBuffer(bytes.length, buffer);
		}
	}

	// for the string which can't be divided by 4
	private void padBuffer(int itemLength, IoBuffer buffer) {
		int mod = itemLength % 4;
		if (mod > 0) {
			byte[] padding = new byte[4 - mod];
			buffer.put(padding);
		}
	}
}
