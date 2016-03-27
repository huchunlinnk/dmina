package hht.dss.commmodule.data;

public class BasicFactory implements PacketFactory {

	public Message createMessage() {
		return new Message();
	}

}
