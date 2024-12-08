/**
 * 
 */
package ro.ciprianpascu.j2sbus;

import org.junit.Test;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.WriteRgbwRequest;
import ro.ciprianpascu.sbus.msg.WriteRgbwResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 * 
 */
public class WriteRgbwTest {

	@Test
	public void testDataIn() {
        UDPMasterConnection conn = null;
        ModbusUDPTransaction trans = null;
        WriteRgbwRequest req = null;
        WriteRgbwResponse res = null;

        int repeat = 1;
        int port = Modbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();

            // 3. Prepare the request
            Register[] regs = new Register[5];
            regs[0] = new ByteRegister((byte)0);
            regs[1] = new ByteRegister((byte)100);
            regs[2] = new ByteRegister((byte)0);
            regs[3] = new ByteRegister((byte)0);
            regs[4] = new WordRegister((short)0);
            req = new WriteRgbwRequest(regs);
            req.setSubnetID(1);
            req.setUnitID(72);
             
            
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

                res = (WriteRgbwResponse) trans.getResponse();
                if(res == null) {
                	k++;
                	continue;
                }
                if (Modbus.debug) {
                    System.out.println("Response: " + res.getHexMessage());
                }
                System.out.println("Digital Inputs Status=" + res.getStatusValue());
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
