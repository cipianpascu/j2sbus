/**
 * Copyright 2002-2010 jamod development team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***/

package ro.ciprianpascu.sbus.cmd;

import java.net.InetAddress;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.WriteSingleChannelRequest;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.SimpleRegister;

/**
 * Class that implements a simple commandline
 * tool for writing to a digital output.
* 
 * Note that if you write to a remote I/O with
 * a Modbus protocol stack, it will most likely
 * expect that the communication is <i>kept alive</i>
 * after the first write message.<br>
 * This can be achieved either by sending any kind of
 * message, or by repeating the write message within a
 * given period of time.<br>
 * If the time period is exceeded, then the device might
 * react by turning pos all signals of the I/O modules.
 * After this timeout, the device might require a
 * reset message.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPDOTest {

    public static void main(String[] args) {

        UDPMasterConnection conn = null;
        ModbusUDPTransaction trans = null;
        WriteSingleChannelRequest req = null;


        int ref = 0;
        boolean set = false;
        int repeat = 1;
        int port = Modbus.DEFAULT_PORT;

        try {

            // 1. Setup the parameters
            if (args.length < 3) {
                printUsage();
                System.exit(1);
            } else {
                try {
                    ref = Integer.parseInt(args[0]);
                    set = "true".equals(args[1]);

                    if (args.length == 3) {
                        repeat = Integer.parseInt(args[2]);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    printUsage();
                    System.exit(1);
                }
            }

            // 2. Open the connection
            conn = new UDPMasterConnection();
            conn.setPort(port);
            conn.connect();
            
            Register reg = new SimpleRegister();
            reg.setValue(set?1:0);

            // 3. Prepare a request
            req = new WriteSingleChannelRequest(ref, reg);
            req.setUnitID(0);
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

                if (Modbus.debug) {
                    System.out.println("Response: " + trans.getResponse().getHexMessage());
                }
                k++;
            } while (k < repeat);

            // 6. Close the connection
            conn.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }// main

    private static void printUsage() {
        System.out.println(
                "java ro.ciprianpascu.sbus.cmd.UDPDOTest <register [int16]> <state [boolean]> {<repeat [int]>}");
    }// printUsage

}// class DOTest
