package hht.dss.commmodule.data;

import java.nio.charset.CharacterCodingException;
import java.util.UUID;

import hht.dss.commmodule.transport.Server;

import org.apache.mina.core.session.IoSession;

public class ServerBasicListener implements Listener {

	public ServerBasicListener() {

	}

	/**
	 * we can put our logic here , therefore , we need write at least 2 handler
	 * . one for server , the other for client
	 */
	public void handleMessage(Message msg) {
		System.out.print("----handle message----");
		short type = msg.getType();
		String msgEpidString=msg.getStringEpid();
		switch (type) {
		case ServerProtocol.MESSAGE_SERVER_REGISTER:
			System.out.println("注册成功："+msg.getBody());
//			System.out.println("开始注册");
//			System.out.println("begin retriving msg" + msg.getBody());
//			Message packet = new Message();
//			packet.setProtrocol((short) 0x0001);
//			packet.setCompress((byte) 0x02);
//			packet.setCrc32(0x00000003);
//			packet.setEncrypt((byte) 0x05);
//			packet.setEpid("8AC55C0574CF4F24AB35E4E821993245");
//			packet.setReverse1((byte) 0x00);
//			packet.setReverse2((byte) 0x02);
//			packet.setSeqnum(UUID.randomUUID());
//			packet.setType((short) 0x0000);
//			packet.setVersion((short) 0x1203);
//			packet.setBody("我是服务器端，给你回消息啦，开心不？"+UUID.randomUUID());
//			packet.setLength(packet.getSize());
//			try {
//				Server.sendPacket(packet, msg.getStringEpid());
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
			break;
		default:
			break;
		}
	}
}
