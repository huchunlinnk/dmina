package cn.hucl.dmina.data;

import cn.hucl.dmina.util.CharSetUtil;
import cn.hucl.dmina.util.GetClassProperties;
import cn.hucl.dmina.util.MessageFieldInfo;
import cn.hucl.dmina.util.TypeConvert;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import javax.management.RuntimeErrorException;

/**
 * *
 * 
 * @author hucl
 * @since 2013-12-18
 * 
 *        the purpose of isolating the message here is to get ourself protocol
 *        may be we need to get some ,the detail meaning for each field can
 *        refer to the protocol document
 */
@SuppressWarnings("serial")
public class Message implements Serializable, Packet {
	private Short protrocol;
	private Short version;
	private Short type;
	private Short length;
	// use uuid(128 bit) to express it
	private byte[] seqnum;
	private byte[] epid;
	private Byte encrypt;
	private Byte compress;
	private Byte reverse1;
	private Byte reverse2;
	private String body;
	private Integer crc32;
	
	//default constructor
	public Message(){
		
	}
	
	/**
	 * the simplest constructor
	 * @param epid the epid get from the login process
	 * @param type the message type
	 * @param body the message body
	 */
	public Message(String epid,short type,String body){
		this.setProtrocol((short) 0x0001);
		this.setCompress((byte) 0x02);
		//should calculate in the future
		this.setCrc32(0x00000000);
		this.setEncrypt((byte) 0x05);
		//hex md5(128 bit)
		this.setEpid(epid);
		this.setReverse1((byte) 0x00);
		this.setReverse2((byte) 0x02);
		//hex guid(128 bit)
		this.setSeqnum(UUID.randomUUID());
		this.setType(type);
		this.setVersion((short) 0x1203);
		this.setBody(body);
		this.setLength(this.getSize());
	}
	
	//返回时的uuid与其上一条消息相同
	public Message(UUID seqNum,String epid,short type,String body){
		this.setProtrocol((short) 0x0001);
		this.setCompress((byte) 0x02);
		//should calculate in the future
		this.setCrc32(0x00000000);
		this.setEncrypt((byte) 0x05);
		//hex md5(128 bit)
		this.setEpid(epid);
		this.setReverse1((byte) 0x00);
		this.setReverse2((byte) 0x02);
		//hex guid(128 bit)
		this.setSeqnum(seqNum);
		this.setType(type);
		this.setVersion((short) 0x1203);
		this.setBody(body);
		this.setLength(this.getSize());
	}
	
	/**
	 * 如果不指定终端序号或者发给服务端
	 * @param type
	 * @param body
	 */
	public Message(short type,String body){
		String epid="00000000000000000000000000000000";
		Message message=new Message(epid, type, body);
		this.setProtrocol(message.getProtrocol());
		this.setCompress(message.getCompress());
		//should calculate in the future
		this.setCrc32(message.getCrc32());
		this.setEncrypt(message.getEncrypt());
		//hex md5(128 bit)
		this.setEpid(epid);
		this.setReverse1(message.getReverse1());
		this.setReverse2(message.getReverse2());
		//hex guid(128 bit)
		this.setSeqnum(message.getSeqnum());
		this.setType(type);
		this.setVersion(message.getVersion());
		this.setBody(body);
		this.setLength(message.getSize());
	}
	
	public Short getProtrocol() {
		return protrocol;
	}

	public void setProtrocol(short protrocol) {
		this.protrocol = protrocol;
	}

	public Short getVersion() {
		return version;
	}

	public void setVersion(short version) {
		this.version = version;
	}

	public Short getType() {
		return type;
	}

	public void setType(short type) {
		this.type = type;
	}

	public Short getLength() {
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public UUID getUUIDSeqnum() {
		String uuidStr=TypeConvert.bytesToHexString(seqnum);
		String convertedUUIDStr=uuidStr.substring(0,8)+"-"+uuidStr.substring(8, 12)+"-"+uuidStr.substring(12, 16)+"-"+uuidStr.substring(16, 20)+"-"+uuidStr.substring(20, 32);
		return UUID.fromString(convertedUUIDStr);
	}
	
	public byte[] getSeqnum(){
		return seqnum;
	}

	//UUID parameter
	public void setSeqnum(UUID seqnum) {
		//if the string has converted once , ignore it
		this.seqnum=TypeConvert.hexStringToBytes(seqnum.toString().replaceAll("-", ""));
	}
	
	//UUID parameter
	public void setSeqnum(byte[] seqnum) {
		//if the string has converted once , ignore it
		this.seqnum=seqnum;
	}
	

	public String getStringEpid() {
//		return TypeConvert.bytesToHexString(this.epid).toUpperCase();
		return new String(this.epid);
	}
	
	public byte[] getEpid(){
		return epid;
	}
	
	public static void main(String[] args) {
		new Message().setEpid("hucl");
	}

	// String parameter ,used for encode
	public void setEpid(String epid) {
		byte[] byteArr=new byte[16];
		if(epid.length()>16){
			throw new RuntimeException("invalid arg , should not be more than 16 byte value");
		}
		StringBuffer buffer=new StringBuffer();
		byte[] epidBytes=epid.getBytes();
		for(int i=0;i<epidBytes.length;i++){
			byteArr[i]=epidBytes[i];
		}
		for(int i=epid.getBytes().length;i<16;i++){
			byteArr[i]=0x00;
		}
		System.out.println(byteArr.length);
		this.epid=byteArr;
//		this.epid = TypeConvert.hexStringToBytes(epid);
		System.out.println("长度："+this.epid.length);
	}

	
	public void setEpid(byte[] epid) {
		this.epid=epid;
	}
	

	public Byte getEncrypt() {
		return encrypt;
	}

	public void setEncrypt(byte encrypt) {
		this.encrypt = encrypt;
	}

	public Byte getCompress() {
		return compress;
	}

	public void setCompress(byte compress) {
		this.compress = compress;
	}

	public Byte getReverse1() {
		return reverse1;
	}

	public void setReverse1(byte reverse1) {
		this.reverse1 = reverse1;
	}

	public Byte getReverse2() {
		return reverse2;
	}

	public void setReverse2(byte reverse2) {
		this.reverse2 = reverse2;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public Integer getCrc32() {
		return crc32;
	}

	public void setCrc32(int crc32) {
		this.crc32 = crc32;
	}

//	public static void main(String[] args) {
		
//		UUID uuid = UUID.randomUUID();
//		Message message = new Message(uuid, "12341234123412341234123412341234", (short)3, "");
//		
//		UUID uidUuid = message.getUUIDSeqnum();
//		
//		System.out.println();
//	}

	//we may return the const() directly in the future
	public static int getHeaderSize() {
		int size = 0;
		try {
			List<MessageFieldInfo> list = GetClassProperties
					.getAllProperties(new Message());
			int properSize = Message.class.getDeclaredFields().length;
			int properHeaderSize = 0;
			for (MessageFieldInfo msgFieldInfo : list) {
				// the body content and crc32 will not take into account as the
				// header part
				if ((properHeaderSize + 2) == properSize)
					break;
				for (Field field : Message.class.getDeclaredFields()) {
					if (msgFieldInfo.getName()
							.equalsIgnoreCase(field.getName())) {
						properHeaderSize++;
						String msgFieldType = msgFieldInfo.getType();
						if (msgFieldType.equals("Bytes")) {
							size += 16;
						} else if (msgFieldType.equals("Byte")) {
							size += 1;
						} else if (msgFieldType.equals("Short")) {
							size += 2;
						} else if (msgFieldType.equals("Integer")) {
							size += 4;
						} else if (msgFieldType.equals("Long")) {
							size += 4;
						} else if (msgFieldType.equals("Float")) {
							size += 4;
						}
						// epid will take 128 bit which is equal to 16 byte
						else if (msgFieldType.equals("String")) {
//							size += 16;
						} else {
							throw new Exception("unresolved type");
						}
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return size;
	}

	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	public short getSize() {
		int bodyTheorySize = this.getBody().getBytes(CharSetUtil.UTF8CHARSET).length;
		int actualSize = getHeaderSize() + bodyTheorySize
				+ TypeConvert.getPadBufferSize(bodyTheorySize) + 4;
		return (short) (actualSize);
	}

}



