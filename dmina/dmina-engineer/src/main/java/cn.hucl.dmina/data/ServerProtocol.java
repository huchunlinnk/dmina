package cn.hucl.dmina.data;

public class ServerProtocol {
	public static final short MESSAGE_SERVER_REGISTER = 0x0001;
	public static final short MESSAGE_SERVER_REGISTER_SUCCESS = 0x0002;
	public static final short MESSAGE_SERVER_REGISTER_REPEAT = 0x0003;
	public static final short MESSAGE_SERVER_REGISTER_FAIL = 0x0004;
	
	public static final short MESSAGE_SERVER_CONNECT = 0x0010;
	public static final short MESSAGE_SERVER_CONNECT_SUCCESS = 0x0011;
	public static final short MESSAGE_SERVER_CONNECT_FAILED = 0x0012;
	
	public static final short MESSAGE_KEEPALIVE_REQUEST=0x0020;
	public static final short MESSAGE_KEEPALIVE_RESPONSE = 0x0021;
	
	public static final short MESSAGE_LOGIN=0x0030;
	public static final short MESSAGE_LOGIN_SUCCESS = 0x0031;
	public static final short MESSAGE_LOGIN_FAIL=0x0032;
	
	public static final short MESSAGE_GETFRIEND=0x0040;
	public static final short MESSAGE_GETFRIEND_SUCCESS= 0x0041;
	public static final short MESSAGE_GETFRIEND_FAIL=0x0042;
	
	public static final short MESSAGE_FORWARD_REQUEST=0x0050;
	public static final short MESSAGE_FORWARD_RESPONSE= 0x0051;
	
	public static final short MESSAGE_NOTIFYFILE=0x0060;
	
}
