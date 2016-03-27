package hht.dss.commmodule.data;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import hht.dss.commmodule.transport.Client;
import hht.dss.commmodule.util.MinaCommandParser;

import org.apache.mina.core.session.IoSession;
import org.dom4j.DocumentException;
import hht.dss.commmodule.data.ServerProtocol;



public class ClientBasicListener implements Listener {

	public ClientBasicListener() {

	}

	/**
	 * we can put our logic here , therefore , we need write at least 2 handler
	 * . one for server , the other for client
	 */
	public void handleMessage(Message msg) {
		System.out.print("----handle client message----");
		short type = msg.getType();
		
		switch (type) {
		     
		//终端连接成功
		case ServerProtocol.MESSAGE_SERVER_REGISTER_SUCCESS:{
			System.out.println("begin retriving server msg:注册成功"+msg.getBody());
		    break;
		}
		
		case ServerProtocol.MESSAGE_NOTIFYFILE:
		{
			System.out.println("来自服务器端的消息"+msg.getBody());
		}
		break;
		   //终端连接失败
		default:
			break;   
		}
	}

}
