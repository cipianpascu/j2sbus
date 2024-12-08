package ro.ciprianpascu.j2sbus;

import org.junit.Test;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadRgbwRequest;
import ro.ciprianpascu.sbus.msg.ReadRgbwResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

public class ReadRgbwTest {
	
	@Test
	public void testDataIn() {
        UDPMasterConnection conn = null;
        ModbusUDPTransaction trans = null;
        ReadRgbwRequest req = null;
        ReadRgbwResponse res = null;

        int repeat = 1;
        int port = Modbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();

            // 3. Prepare the request
            req = new ReadRgbwRequest();
            req.setSubnetID(1);
            req.setUnitID(72);
            req.setLimitType(1);
            if (Modbus.debug) {
                System.out.println("Request: " + req.getHexMessage());
            }

            // 4. Prepare the transaction
            trans = new ModbusUDPTransaction(conn);
            trans.setRequest(req);

            // 5. Execute the transaction repeat times
            int k = 0;
            do {
                trans.execute();

                res = (ReadRgbwResponse) trans.getResponse();
                if(res == null) {
                	k++;
                	continue;
                }
                if (Modbus.debug) {
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
