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

import java.net.Inet4Address;
import java.net.InetAddress;

import ro.ciprianpascu.sbus.Modbus;
import ro.ciprianpascu.sbus.io.ModbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsRequest;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsResponse;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

/**
 * Class that implements a simple commandline
 * tool for reading a digital input.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPDITest {

    public static void main(String[] args) {

        UDPMasterConnection conn = null;
        ModbusUDPTransaction trans = null;
        ReadStatusChannelsRequest req = null;
        ReadStatusChannelsResponse res = null;

        int repeat = 1;
        int port = Modbus.DEFAULT_PORT;

        try {

            // 1. Setup the parameters
            if (args.length < 2) {
                printUsage();
                System.exit(1);
            } else {
                try {
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
                if (Modbus.debug) {
                    System.out.println("Response: " + res.getHexMessage());
                }
                System.out.println("Digital Inputs Status=" + res.getRegisters().toString());
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
                "java ro.ciprianpascu.sbus.cmd.UDPDITest <register [int16]> <bitcount [int16]> {<repeat [int]>}");
    }// printUsage

}// class DITest
