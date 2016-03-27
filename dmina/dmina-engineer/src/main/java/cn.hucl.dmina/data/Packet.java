package cn.hucl.dmina.data;

import java.io.Serializable;

public interface Packet extends Serializable {
	static final long serialVersionUID = 1L;
	static final int MAX_MSG_LENGTH = 1024;

	public boolean isValid();

	public short getSize();
}
