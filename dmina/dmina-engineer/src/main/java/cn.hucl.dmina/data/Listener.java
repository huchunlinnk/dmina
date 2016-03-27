/**
 * any one who has implemented this interface can handle the message received
 * 
 * @author hucl
 * @since 2013-12-23
 */
package hht.dss.commmodule.data;

import org.apache.mina.core.session.IoSession;


public interface Listener {
	void handleMessage(Message msg);
}
