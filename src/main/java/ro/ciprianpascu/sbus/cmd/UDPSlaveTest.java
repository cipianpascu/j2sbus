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

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.net.SbusUDPListener;
import ro.ciprianpascu.sbus.procimg.SimpleDigitalIn;
import ro.ciprianpascu.sbus.procimg.SimpleDigitalOut;
import ro.ciprianpascu.sbus.procimg.SimpleProcessImage;

/**
 * Test class implementing a simple SBus UDP slave.
 * This class demonstrates the basic setup and operation of a SBus slave
 * over UDP. It creates a simple process image with digital inputs and outputs
 * to test the functionality and behavior of the implementation.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class UDPSlaveTest {

    /**
     * Main entry point for the UDP slave test application.
     * Sets up and starts a UDP listener with a simple process image.
     * The listener will accept connections on the default port unless
     * a different port is specified as a command line argument.
     *
     * @param args command line arguments - optional port number as first argument
     */
    public static void main(String[] args) {
        SbusUDPListener listener = null;
        SimpleProcessImage spi = null;
        int port = Sbus.DEFAULT_PORT;

        try {
            if (args != null && args.length == 1) {
                port = Integer.parseInt(args[0]);
            }

            System.out.println("SBus UDP Slave Test Application");

            // Create and initialize the process image
            spi = new SimpleProcessImage();
            spi.addDigitalOut(new SimpleDigitalOut(true));
            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));
            spi.addDigitalIn(new SimpleDigitalIn(false));
            spi.addDigitalIn(new SimpleDigitalIn(true));

            // Setup and start the UDP listener
            listener = new SbusUDPListener();
            listener.setProcessImage(spi);
            listener.setPort(port);
            listener.start();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
