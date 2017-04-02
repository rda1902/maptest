package maptest.service.callback;

import java.util.List;
import maptest.service.data.Transport;


public interface NewTransportsAddedCallback {

	void onNewTransportsAdded(List<Transport> transports);
}
