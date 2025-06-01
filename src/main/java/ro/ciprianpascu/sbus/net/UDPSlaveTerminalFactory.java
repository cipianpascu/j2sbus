/**
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
package ro.ciprianpascu.sbus.net;

import java.net.InetAddress;

/**
 * Factory interface for creating UDPSlaveTerminal instances.
 * This interface defines a factory method for creating UDP slave terminals
 * bound to specific network interfaces and ports, allowing for flexible
 * instantiation of terminal objects.
 *
 * @author Sami Salonen
 */
public interface UDPSlaveTerminalFactory {
    /**
     * Creates a new UDPSlaveTerminal instance bound to the specified network interface and port.
     *
     * @param interfac the network interface to bind to
     * @param port the port number to listen on
     * @return a new UDPSlaveTerminal instance
     */
    public UDPSlaveTerminal create(InetAddress interfac, int port);
}
