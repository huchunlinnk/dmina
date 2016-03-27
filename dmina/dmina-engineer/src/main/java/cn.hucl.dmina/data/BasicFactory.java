package cn.hucl.dmina.data;

public class BasicFactory implements PacketFactory {

	public Message createMessage() {
		return new Message();
	}

}
