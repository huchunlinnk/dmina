package cn.hucl.dmina.transport;

import cn.hucl.dmina.data.BasicFactory;
import cn.hucl.dmina.data.KeepAliveFactory;
import cn.hucl.dmina.data.Listener;
import cn.hucl.dmina.data.Message;
import cn.hucl.dmina.data.PacketFactory;
import cn.hucl.dmina.data.ServerBasicListener;
import cn.hucl.dmina.data.SessionValue;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.DatagramSessionConfig;
import org.apache.mina.transport.socket.nio.NioDatagramAcceptor;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 * digital campus central Server
 * 
 * @author hucl
 * @since 2013-10-29
 */
public class Server {
	//final Logger logger = LoggerFactory.getLogger(Server.class);

	public static int PORT = 10050;
	private static final int IDELTIMEOUT = 30;
	private IoAcceptor acceptor;
	private ServerIoHandler handler;
	private int port;
	private PacketFactory packetFactory;
	private int processThreadPoolNum=0;
	public static DataEncoder encoder;
	public static Map<String,SessionValue> sessionsMap=new Hashtable<String,SessionValue>();
	public static List epidList=new Vector<String>();
	//when the server send packet to client,no needs to fill the epid
	public static final String COMMON_EPID="00000000000000000000000000000000";
	
	public int getProcessThreadPoolNum() {
		return processThreadPoolNum;
	}

	public void setProcessThreadPoolNum(int processThreadPoolNum) {
		this.processThreadPoolNum = processThreadPoolNum;
	}

	// here the udp determinate whether use udp or tcp
	private boolean udp = false;

	public Server(int port) {
		Server.PORT=port;
		this.port = port;
		this.handler = new ServerIoHandler();
		this.encoder = new DataEncoder(udp);
	}

	/**
	 * @param port the port binded by server
	 * @param processThreadPoolNum the number of processor thread 
	 */
	public Server(int port, int processThreadPoolNum) {
		Server.PORT=port;
		this.port=port;
		this.processThreadPoolNum = processThreadPoolNum;
		this.handler = new ServerIoHandler();
		this.encoder = new DataEncoder(udp);
	}

	public void setUDP(boolean udp) {
		this.udp = udp;
	}

	public void setFactory(PacketFactory factory) {
		packetFactory = factory;
	}

	public void addListener(Listener listener) {
		handler.addListener(listener);
	}

	public void removeListener(Listener listener) {
		handler.removeListener(listener);
	}

	public void start() throws IOException {
		if (packetFactory == null) {
			packetFactory = new BasicFactory();
		}

		acceptor = createAcceptor();
		acceptor.getFilterChain().addLast("logger", new LoggingFilter());
		acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDELTIMEOUT);
		// allow we define our protrocol
		acceptor.getFilterChain().addLast("protocol",
				new ProtocolCodecFilter(new CodecFactory(packetFactory, udp)));
		acceptor.getFilterChain().addLast("heartbeat",
				new KeepAliveFactory().createServerKeepAliveFilter());
		acceptor.setHandler(handler);
		acceptor.bind(new InetSocketAddress(port));
		//start the timer
		if(!ServerHeartBeartDect.isInited){
			ServerHeartBeartDect.isInited=true;
			TimerTask task=new ServerHeartBeartDect().getTask();
			Timer timer=new Timer();
			timer.schedule(task, 20*1000, 10*1000);
		}
		
		//logger.info("server is listening at port " + port);
	}

	// both tcp/ip and udp/ip is supported
	private IoAcceptor createAcceptor() {
		if (udp) {
			NioDatagramAcceptor nda = new NioDatagramAcceptor();
			DatagramSessionConfig dcfg = nda.getSessionConfig();
			dcfg.setReuseAddress(true);
			return nda;
		} else {
			if(this.processThreadPoolNum==0)
				return new NioSocketAcceptor();
			else {
				return new NioSocketAcceptor(this.processThreadPoolNum);
			}
		}
	}

	public void stop() {
		acceptor.unbind();
	}
	
	public static void sendPacket(Message packet, String epid)
			throws Exception {
		if (!sessionsMap.containsKey(epid)) {
			throw new RuntimeException("the epid is not online");
		} else {
			IoSession session=(IoSession) ((SessionValue)Server.sessionsMap.get(epid)).getIoSesion();
			int size = packet.getSize();
			IoBuffer buffer = IoBuffer.allocate(size);
			buffer.setAutoExpand(true);
			encoder.encodePacket(packet, buffer, false);
			buffer.flip();
			session.write(buffer);
		}
	}
	
	public static boolean isOnline(String epid){
		return sessionsMap.containsKey(epid);
	}
	
	public static void main(String[] args) throws IOException {
		Server server=new Server(PORT,5);
		//Server server = new Server(PORT);
		server.start();
		server.addListener(new ServerBasicListener());
	}

}
