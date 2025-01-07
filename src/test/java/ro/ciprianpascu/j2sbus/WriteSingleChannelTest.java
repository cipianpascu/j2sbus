/**
 *
 */
package ro.ciprianpascu.j2sbus;

import org.junit.Test;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.io.SbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.WriteSingleChannelRequest;
import ro.ciprianpascu.sbus.msg.WriteSingleChannelResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 *
 */
public class WriteSingleChannelTest {

    @Test
    public void testDataIn() {
        UDPMasterConnection conn = null;
        SbusUDPTransaction trans = null;
        WriteSingleChannelRequest req = null;
        WriteSingleChannelResponse res = null;

        int repeat = 1;
        int port = Sbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();

            // 3. Prepare the request
            Register[] registers = new Register[2];
            registers[0] = new ByteRegister((byte) 0);
            registers[1] = new WordRegister((short) 0);
            req = new WriteSingleChannelRequest(24, registers);
            req.setSubnetID(11);
            req.setUnitID(175);

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

                res = (WriteSingleChannelResponse) trans.getResponse();
                if (res == null) {
                    k++;
                    continue;
                }
                if (Sbus.debug) {
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

    public void testDataOut() {
        UDPMasterConnection conn = null;
        SbusUDPTransaction trans = null;
        WriteSingleChannelRequest req = null;

        int ref = 24;
        boolean set = false;
        int repeat = 1;
        int port = Sbus.DEFAULT_PORT;

        try {

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();

            Register[] registers = new Register[2];
            registers[0] = new ByteRegister((byte) 100);
            registers[1] = new WordRegister((short) 0);

            // 3. Prepare a request
            req = new WriteSingleChannelRequest(ref, registers);
            req.setSubnetID(1);
            req.setUnitID(75);
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

                if (Sbus.debug) {
                    System.out.println("Response: " + trans.getResponse().getHexMessage());
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
