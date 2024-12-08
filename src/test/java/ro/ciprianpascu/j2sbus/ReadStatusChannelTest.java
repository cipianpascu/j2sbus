/**
 * 
 */
package ro.ciprianpascu.j2sbus;

import org.junit.Test;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsRequest;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

/**
 * 
 */
public class ReadStatusChannelTest {

	@Test
	public void testDataIn() {
        UDPMasterConnection conn = null;
        ModbusUDPTransaction trans = null;
        ReadStatusChannelsRequest req = null;
        ReadStatusChannelsResponse res = null;

        int repeat = 1;
        int port = Modbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();

            // 3. Prepare the request
            req = new ReadStatusChannelsRequest();
            req.setSubnetID(1);
            req.setUnitID(75);
             
            
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

                res = (ReadStatusChannelsResponse) trans.getResponse();
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
	
    private static void printUsage() {
        System.out.println(
                "java ro.ciprianpascu.sbus.cmd.UDPDITest <register [int16]> <bitcount [int16]> {<repeat [int]>}");
    }// printUsage

}
