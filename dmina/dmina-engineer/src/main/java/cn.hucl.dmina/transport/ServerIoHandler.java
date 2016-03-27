package cn.hucl.dmina.transport;

import cn.hucl.dmina.data.Listener;
import cn.hucl.dmina.data.Message;
import cn.hucl.dmina.data.SessionValue;

import java.util.ArrayList;
import java.util.Iterator;

import org.apache.mina.core.future.CloseFuture;
import org.apache.mina.core.future.IoFuture;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

public class ServerIoHandler extends IoHandlerAdapter {

	//final Logger logger = LoggerFactory.getLogger(ServerIoHandler.class);

	public static final String INDEX_KEY = ServerIoHandler.class.getName()
			+ ".INDEX";
	private ArrayList<Listener> listeners;

	public void addListener(Listener listener) {
		if (listeners == null) {
			listeners = new ArrayList<Listener>();
		}
		listeners.add(listener);
	}

	public void removeListener(Listener listener) {
		listeners.remove(listener);
	}

	public void sessionOpened(IoSession session) throws Exception {
		session.setAttribute(INDEX_KEY, new Integer(0));
		if (listeners == null) {
			listeners = new ArrayList<Listener>();
		}
	}

	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		// SessionLog.warn(session, cause.getMessage(), cause);
		// Log.e(TAG, "Error handling OSC server IO.", cause);

		//logger.error("Error handling server IO.", cause);
	}

	@SuppressWarnings("unchecked")
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		if (message instanceof Message) {
			String epid=((Message) message).getStringEpid().trim();
			System.out.println("----receive msg,epid is----"+epid);
			if(!Server.sessionsMap.containsKey(epid)){
				/**
				 * comment this line when we are debugging 
				 */
//				if(Server.epidList.contains(epid))
				SessionValue sessionValue=new SessionValue(0,session);
					Server.sessionsMap.put(epid, sessionValue);
//				else {
//					//logger.warn("unauthronized end point: "+epid +" access denied");
//				}
			}
			for (Iterator<Listener> i = listeners.iterator(); i.hasNext();) {
				Listener listener = (Listener) i.next();
				listener.handleMessage((Message) message);
			}

			/*
			 * if we need to send byte array ,then add conditional control here
			 * invoke it
			 */
		} else {

			System.out.println("xintiao");
		}
	}

	/**
	 * this method can be optimized by implementing the data structure which
	 * can support Bidirection Association
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		if(Server.sessionsMap.containsValue(session)){
			synchronized (Server.sessionsMap) {
				for(Object key:Server.sessionsMap.keySet())
				if(((SessionValue)Server.sessionsMap.get(key)).getIoSesion().equals(session)){
					Server.sessionsMap.remove(key);
				}
			}
		}
		CloseFuture future = session.close(true);
		future.addListener(new IoFutureListener() {
			public void operationComplete(IoFuture future) {
				if (future instanceof CloseFuture) {
					((CloseFuture) future).setClosed();
					//logger.info("have do the future set to closed");
				}
			}
		});
	}

	@Override
	public void sessionIdle(IoSession session, IdleStatus arg1)
			throws Exception {
		//logger.info(session.getId() + "(SesssionID) is idle in the satate-->"+ arg1);
	}

}