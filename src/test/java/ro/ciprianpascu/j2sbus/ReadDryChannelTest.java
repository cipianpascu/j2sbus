package ro.ciprianpascu.j2sbus;

import org.junit.Test;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.io.SbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadDryChannelsRequest;
import ro.ciprianpascu.sbus.msg.ReadDryChannelsResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

public class ReadDryChannelTest {

    @Test
    public void testDataIn() {
        UDPMasterConnection conn = null;
        SbusUDPTransaction trans = null;
        ReadDryChannelsRequest req = null;
        ReadDryChannelsResponse res = null;

        int repeat = 1;
        int port = Sbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();

            // 3. Prepare the request
            req = new ReadDryChannelsRequest();
            req.setSubnetID(1);
            req.setUnitID(101);
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

                res = (ReadDryChannelsResponse) trans.getResponse();
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
