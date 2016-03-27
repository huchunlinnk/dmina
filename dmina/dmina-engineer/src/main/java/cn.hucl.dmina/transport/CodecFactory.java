package cn.hucl.dmina.transport;

import cn.hucl.dmina.data.PacketFactory;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class CodecFactory implements ProtocolCodecFactory {

	private DataEncoder encoder;
	private DataDecoder decoder;

	public CodecFactory(PacketFactory factory, Boolean isUDP) {
		encoder = new DataEncoder(isUDP);
		decoder = new DataDecoder(factory, isUDP);
	}

	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return decoder;
	}

	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return encoder;
	}
}
