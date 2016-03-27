package cn.hucl.dmina.data;

import org.apache.mina.core.session.IoSession;

public class SessionValue {
	private IoSession ioSesion;
	private short retryTimes;
	public SessionValue(int i, IoSession session) {
		this.ioSesion=session;
		this.retryTimes=(short) i;
	}
	public IoSession getIoSesion() {
		return ioSesion;
	}
	public void setIoSesion(IoSession ioSesion) {
		this.ioSesion = ioSesion;
	}
	public short getRetryTimes() {
		return retryTimes;
	}
	public void setRetryTimes(short retryTimes) {
		this.retryTimes = retryTimes;
	}
	
}
