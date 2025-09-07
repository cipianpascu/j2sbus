package ro.ciprianpascu.j2sbus;

import org.junit.Test;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.io.SbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadNineInOneStatusRequest;
import ro.ciprianpascu.sbus.msg.ReadNineInOneStatusResponse;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.SbusMessageListener;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

public class Read9in1Test {

    @Test
    public void testDataIn() {
        UDPMasterConnection conn = null;
        SbusUDPTransaction trans = null;
        ReadNineInOneStatusRequest req = null;
        ReadNineInOneStatusResponse res = null;

        int repeat = 2;
        int port = Sbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();
            conn.addMessageListener(new SbusMessageListener() {
				
				@Override
				public void onMessageReceived(SbusResponse response) {
					System.out.println("Notification: " + response.getHexMessage());
				}
			});

            // 3. Prepare the request
            req = new ReadNineInOneStatusRequest();
            req.setSubnetID(1);
            req.setUnitID(52);
            if (Sbus.debug) {
                System.out.println("Request: " + req.getHexMessage());
            }

            // 4. Prepare the transaction
            trans = new SbusUDPTransaction(conn);
            trans.setRequest(req);

            // 5. Execute the transaction repeat times
            int k = 0;
            do {
                trans.execute();

                res = (ReadNineInOneStatusResponse) trans.getResponse();
                if (res == null) {
                    k++;
                    continue;
                }
                if (Sbus.debug) {
                    System.out.println("Response: " + res.getHexMessage());
                }
                k++;
            } while (k < repeat);

            // 6. Close the connection
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
