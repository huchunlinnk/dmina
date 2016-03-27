package cn.hucl.dmina.transport;

import cn.hucl.dmina.data.Message;
import cn.hucl.dmina.data.SessionValue;

import java.util.Map.Entry;
import java.util.TimerTask;

public class ServerHeartBeartDect {
	public static boolean isInited = false;
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
//			System.out.println("begin iterate sessionmap");
			synchronized (Server.sessionsMap) {
				for (Entry<String, SessionValue> item : Server.sessionsMap
						.entrySet()) {
					if (item.getValue().getRetryTimes() > 2) {
						System.out.println("more than 3 times,endpoint is removed:"+item.getKey());
						Server.sessionsMap.remove(item.getKey());
					} else {
						item.getValue().setRetryTimes(
								(short) (item.getValue().getRetryTimes() + 1));
					}
				}
			}
		}
	};

	public TimerTask getTask() {
		return task;
	}

}
