package hht.dss.commmodule.transport;

import hht.dss.commmodule.data.*;
import hht.dss.commmodule.util.ConfigUtil;

import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.nio.charset.CharacterCodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.UUID;

import javax.naming.AuthenticationException;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteToClosedSessionException;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioDatagramConnector;
import org.apache.mina.transport.socket.nio.NioSocketConnector;


/**
 * @author hucl
 * @since 2013-12-19
 * 
 *        used in pad terminal
 */
public class Client extends IoHandlerAdapter implements
		IoFutureListener<IoFuture> {

	/***
	 * how long to wait in ms before the connection attempt fails.
	 */
	public static final int CONNECTION_TIMEOUT = 10 * 1000;

	public static boolean IS_ALIVE = false;

	/***
	 * how many times to retry the connection before the connection attempt
	 * fails. 6*60*24 will make sure the attempt times will cover all day
	 */
	public static final int CONNECTION_ATTEMPTS = 6 * 60 * 24;

	/***
	 * time in ms to wait in between connection attempts
	 */
	public static final long CONNECTION_RETRY_DELAY = 10 * 1000;

	private ArrayList<Listener> listeners;

	public volatile int curConnectionAttempt = 0;

	private static final int IDELTIMEOUT = 30;

	private static String ENDPOINT_EPID = null;

	public String getEpid() {
		return ENDPOINT_EPID;
	}

	// final Logger logger = LoggerFactory.getLogger(Client.class);
	private IoConnector connector;
	private volatile IoSession session;
	private List<Message> packetQueue;
	private DataEncoder encoder;
	private PacketFactory packetFactory;
	private final int RETRY_CONNECT_TIME_LIMIT = 3;
	private int retryConnectTimes = 0;

	private InetSocketAddress address;

	private boolean udp = true;

	public Client(boolean isUDP) {
		packetQueue = new ArrayList<Message>();
		udp = isUDP;
		if (packetFactory == null) {
			packetFactory = new BasicFactory();
		}
		connector = createConnector();

		// this will only be used in java
		connector.getFilterChain().addLast("protocol",
				new ProtocolCodecFilter(new CodecFactory(packetFactory, udp)));

		// add heartbeat filter
		connector.getFilterChain().addLast("heartbeat",
				new KeepAliveFactory().createClientKeepAliveFilter());
		connector.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE,
				IDELTIMEOUT);
		connector.setHandler(this);
		encoder = new DataEncoder(udp);
	}

	public boolean isActive() {
		return session.isConnected();
	}

	private void attemptReconnect() throws CharacterCodingException,
			InterruptedException {

		while ((session == null) || (session.isClosing()) || !IS_ALIVE) {
			if (++curConnectionAttempt < CONNECTION_ATTEMPTS) {
				// logger.warn("Attempting to reconnect in "+
				// (CONNECTION_RETRY_DELAY / 1000) + " seconds.");

				// TODO: unregister future listener!
				long endTime = System.currentTimeMillis()
						+ CONNECTION_RETRY_DELAY;
				while (System.currentTimeMillis() < endTime) {
					synchronized (this) {
						try {
							wait(endTime - System.currentTimeMillis());
						} catch (Exception e) {
						}
					}
				}
				connect(address, Client.ENDPOINT_EPID);
			}
		}
	}

	public synchronized void connect(InetSocketAddress addr, String epid)
			throws CharacterCodingException, InterruptedException {
		if ((epid == null) && (epid.length() != 32))
			return;
		Client.ENDPOINT_EPID = epid;
		address = addr;
		connector.setConnectTimeoutMillis(CONNECTION_TIMEOUT);
		if (!udp) {
			// only tcp need to set keepalive
			((NioSocketConnector) connector).getSessionConfig().setKeepAlive(
					true);
		}
		ConnectFuture connFuture = connector.connect(address);
		connFuture.addListener(this);
		connFuture.awaitUninterruptibly(CONNECTION_TIMEOUT);
		// send the authentication after completing connect

		if (!HeartBeatTask.isFirstTimeStarted) {
			Timer timer = new Timer();
			timer.schedule(new HeartBeatTask(this).getTask(), 10 * 1000,
					10 * 1000);
		}
	}

	private IoConnector createConnector() {
		if (udp) {
			return new NioDatagramConnector();
		} else {
			return new NioSocketConnector();
		}
	}

	public void disconnect() {
		curConnectionAttempt = CONNECTION_ATTEMPTS; // deny any reconnection
		session.close();
	}

	public void exceptionCaught(IoSession arg0, Throwable arg1)
			throws Exception {
		// logger.warn("Exception Caught ");
		if (arg1 instanceof PortUnreachableException) {

			attemptReconnect();
		} else if (arg1 instanceof WriteToClosedSessionException) {
			// logger.warn("Tried to write to closed sessson " +
			// arg1.toString());

			// don't reconnect if the session has ended after being started
			// correctly!
			// attemptReconnect();
		} else {
			arg1.printStackTrace();
		}
	}

	public PacketFactory getPacketFactory() {
		return packetFactory;
	}

	public void messageReceived(IoSession arg0, Object message)
			throws Exception {
		// logger.info("client receive msg");
		if (message instanceof Message) {
			Message msg = (Message) message;
			for (Iterator<Listener> i = listeners.iterator(); i.hasNext();) {
				Listener listener = (Listener) i.next();
				if (msg.getType() == ServerProtocol.MESSAGE_KEEPALIVE_RESPONSE) {
					Client.IS_ALIVE = true;
				} else {
					listener.handleMessage(msg);
				}
			}
		} else {
			System.out.println("xintiao");
		}
	}

	public void messageSent(IoSession arg0, Object arg1) throws Exception {
		// logger.info("Message Sent");
	}

	public void operationComplete(IoFuture future) {
		ConnectFuture connFuture = (ConnectFuture) future;
		if (connFuture.isConnected()) {
			session = future.getSession();
			if (packetQueue.size() > 0) {
				for (int i = 0; i < packetQueue.size(); i++) {
					Message packet = packetQueue.get(i);
					if (session == null)
						break;
					try {
						sendPacket(packet);
						packetQueue.remove(i);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} else {
		}

	}

	public void sendPacket(Message packet) throws InterruptedException,
			CharacterCodingException {
		System.out.println(packet.getSize());
		packetQueue.add(packet);
		sendPacket(packet, false);
	}

	private void sendPacket(Message packet, boolean removeFlag)
			throws CharacterCodingException, InterruptedException {
		if (session == null) {
			attemptReconnect();
		} else {
			int size = packet.getSize();
			IoBuffer buffer = IoBuffer.allocate(size);
			buffer.setAutoExpand(true);
			encoder.encodePacket(packet, buffer, false);
			buffer.flip();
			session.write(buffer);
			if (!removeFlag) {
				if (packetQueue.contains(packet))
					packetQueue.remove(packet);
			}
		}
	}

	public void sessionClosed(IoSession arg0) {
		try {
			System.out.println("session closed");
			attemptReconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void sessionCreated(IoSession arg0) throws Exception {
		// logger.info("session created...");
	}

	public void sessionIdle(IoSession arg0, IdleStatus arg1) throws Exception {
		// logger.info("Session Idle");
	}

	public void sessionOpened(IoSession arg0) throws Exception {
		// logger.info("Session Opened");
	}

	public void setPacketFactory(PacketFactory packetFactory) {
		this.packetFactory = packetFactory;
	}

	public void addListener(Listener listener) {
		if (listeners == null) {
			listeners = new ArrayList<Listener>();
		}
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void authenticatateToServer(String epid,String body)
			throws CharacterCodingException, InterruptedException {

		// Message msg=new Message(epid, (short)0x0001,
		// "<root><epid>8AC55C0574CF4F24AB35E4E821993244</epid></root>");
		System.out.println(body);
		Message msg = new Message(epid, ServerProtocol.MESSAGE_SERVER_REGISTER, body);
		System.out.println("开始注册");
		sendPacket(msg);
	}

	public void authenticatateToServer(String username,
			Map<String, String> mapInfo) throws CharacterCodingException,
			InterruptedException {
		StringBuffer body = new StringBuffer();
		body.append("<root>");
		for (Entry<String, String> entry : mapInfo.entrySet()) {
			body.append("<");
			body.append(entry.getKey());
			body.append(">");
			body.append(entry.getValue());
			body.append("</");
			body.append(entry.getKey());
			body.append(">");
		}
		body.append("</root>");
		Message msg = new Message(username,
				ServerProtocol.MESSAGE_SERVER_REGISTER, body.toString());
		sendPacket(msg);
	}

	public void dealStatus(boolean b) {
		retryConnectTimes++;
		if (retryConnectTimes > RETRY_CONNECT_TIME_LIMIT - 1) {
			IS_ALIVE = false;
		}
	}

	/*
	 * test the client
	 */
	public static void main(String[] args) throws InterruptedException {
		// the complex way
		/*
		 * Message msg = new Message(); msg.setProtrocol((short) 0x0001);
		 * msg.setCompress((byte) 0x02); msg.setCrc32(0x00000003);
		 * msg.setEncrypt((byte) 0x05); //hex md5(128 bit)
		 * msg.setEpid("8AC55C0574CF4F24AB35E4E821993244");
		 * msg.setReverse1((byte) 0x00); msg.setReverse2((byte) 0x02); //hex
		 * guid(128 bit) msg.setSeqnum(UUID.randomUUID()); msg.setType((short)
		 * 0x0000); msg.setVersion((short) 0x1203); msg.setBody("3214");
		 * msg.setLength(msg.getSize());
		 */
		Client client = new Client(false);
		client.addListener(new ClientBasicListener());
		try {
			client.connect(new InetSocketAddress(ConfigUtil.IP_ADDRESS, Integer.parseInt(ConfigUtil.SERVER_PORT)),
					ConfigUtil.LOCAL_IP);
			HeartBeatTask heartBeatTask = new HeartBeatTask(client);
			client.authenticatateToServer(ConfigUtil.LOCAL_IP,"<root><ip>"+ConfigUtil.LOCAL_IP+
					"</ip><servername>"+ConfigUtil.CLOUD_NAME+"</servername><location>"+
							ConfigUtil.PROVIDER_LOCATION+"</location></root>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	//
	// }.start();

	// }

}
// }
