package cn.hucl.dmina.util;

import java.util.UUID;

public class UUIDUtil {

	public static byte[] getUUIDBytes(){
		return UUID.randomUUID().toString().getBytes();
	}
}
