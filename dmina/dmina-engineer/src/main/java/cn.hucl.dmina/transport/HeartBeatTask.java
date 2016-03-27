package hht.dss.commmodule.transport;

import hht.dss.commmodule.data.Message;
import hht.dss.commmodule.data.ServerProtocol;

import java.nio.charset.CharacterCodingException;
import java.util.TimerTask;

public class HeartBeatTask {
    
	private Client client;
	public static boolean isFirstTimeStarted=false;

	public HeartBeatTask(Client client){
		this.client=client;
	}

	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			isFirstTimeStarted=true;
			System.err.println("开始心跳了....");
			String body="KEEPALIVEREAUEST";
			Message msg=new Message(client.getEpid(), ServerProtocol.MESSAGE_KEEPALIVE_REQUEST,body);
			try {
				client.sendPacket(msg);
			} catch (Exception e) {
				client.dealStatus(false);
			}
		}   
		
	};
	
	public TimerTask getTask(){
		return task;
	}

}
