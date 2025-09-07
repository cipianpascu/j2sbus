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

package ro.ciprianpascu.j2sbus;

import ro.ciprianpascu.sbus.io.SbusTransaction;
import ro.ciprianpascu.sbus.io.SbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsRequest;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.SbusMessageListener;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

/**
 * Example demonstrating the new UDP Master listener functionality.
 * This shows how to use both synchronous request/response transactions
 * and asynchronous message listeners simultaneously.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPMasterListenerExample {

    public static void main(String[] args) {
        UDPMasterConnection connection = null;

        try {
            // Create connection
            connection = new UDPMasterConnection();
            // connection.setRemoteAddress(InetAddress.getByName("192.168.1.100"));
            connection.setPort(6000);

            // Add a listener for unsolicited messages
            connection.addMessageListener(new SbusMessageListener() {
                @Override
                public void onMessageReceived(SbusResponse response) {
                    System.out.println("Received unsolicited message: " + "SubnetID=" + response.getSubnetID()
                            + ", UnitID=" + response.getUnitID() + ", FunctionCode=" + response.getFunctionCode());
                }

                @Override
                public void onError(Exception error, byte[] rawMessage) {
                    System.err.println("Error processing message: " + error.getMessage());
                }
            });

            // Connect
            connection.connect();

            // Example 1: Synchronous request/response (existing behavior)
            System.out.println("Sending synchronous request...");
            SbusTransaction transaction = new SbusUDPTransaction(connection);
            SbusRequest request = new ReadStatusChannelsRequest();
            request.setSubnetID(1);
            request.setUnitID(1);
            transaction.setRequest(request);

            // This will work exactly as before - no interference from listener
            transaction.execute();
            SbusResponse response = transaction.getResponse();
            System.out.println("Synchronous response received: " + response);

            // Example 2: The listener will automatically handle any unsolicited messages
            System.out.println("Listening for unsolicited messages...");
            System.out.println("(The listener will automatically handle any messages that arrive)");

            // Keep the connection alive to demonstrate listener functionality
            Thread.sleep(10000); // Wait 10 seconds for unsolicited messages

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }
}
