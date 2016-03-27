package hht.dss.commmodule.data;

import hht.dss.commmodule.transport.ServerIoHandler;

import java.util.concurrent.ExecutorService;

import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveMessageFactory;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.apache.mina.transport.socket.SocketAcceptor;

public class KeepAliveFactory implements KeepAliveMessageFactory {

	private static final int PORT = 8889;
	private static final int IDELTIMEOUT = 30;
	private static final int HEARTBEATRATE = 10;
	private static final String HEARTBEATREQUEST = "HEARTBEATREQUEST";
	private static final String HEARTBEATRESPONSE = "HEARTBEATRESPONSE";

	private static SocketAcceptor acceptor;
	private static ExecutorService filterExecutor = new OrderedThreadPoolExecutor();
	private static IoHandler handler = new ServerIoHandler();

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mina.filter.keepalive.KeepAliveMessageFactory#getRequest
	 * (org.apache.mina.core.session.IoSession)
	 */
	public Object getRequest(IoSession session) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mina.filter.keepalive.KeepAliveMessageFactory#getResponse
	 * (org.apache.mina.core.session.IoSession, java.lang.Object)
	 */
	public Object getResponse(IoSession session, Object request) {
//		System.out.println("get request");
		return HEARTBEATRESPONSE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.mina.filter.keepalive.KeepAliveMessageFactory#isRequest
	 * (org.apache.mina.core.session.IoSession, java.lang.Object)
	 */
	public boolean isRequest(IoSession session, Object message) {
//		System.out.println("invoking is  request: " + message);
		if (message.equals(HEARTBEATREQUEST))
			return true;
		return false;
	}

	public boolean isResponse(IoSession session, Object message) {
		if (message.equals(HEARTBEATRESPONSE))
			return true;
		return false;
	}

	public KeepAliveFilter createServerKeepAliveFilter() {
		KeepAliveMessageFactory heartBeatFactory = new KeepAliveFactory();
		KeepAliveRequestTimeoutHandler heartBeatHandler = new KeepAliveRequestTimeoutHandlerImpl();
		KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
				IdleStatus.BOTH_IDLE, heartBeatHandler);
		heartBeat.setForwardEvent(true);
		heartBeat.setRequestInterval(HEARTBEATRATE);
		return heartBeat;
	}

	public KeepAliveFilter createClientKeepAliveFilter() {
		KeepAliveMessageFactory heartBeatFactory = new KeepAliveFactory();
		KeepAliveRequestTimeoutHandler heartBeatHandler = new KeepAliveRequestTimeoutHandlerImpl();
		KeepAliveFilter heartBeat = new KeepAliveFilter(heartBeatFactory,
				IdleStatus.BOTH_IDLE, heartBeatHandler);
		heartBeat.setForwardEvent(true);
		heartBeat.setRequestInterval(HEARTBEATRATE);
		return heartBeat;
	}

}

class KeepAliveRequestTimeoutHandlerImpl implements
		KeepAliveRequestTimeoutHandler {

	public void keepAliveRequestTimedOut(KeepAliveFilter filter,
			IoSession session) throws Exception {
	}

}
