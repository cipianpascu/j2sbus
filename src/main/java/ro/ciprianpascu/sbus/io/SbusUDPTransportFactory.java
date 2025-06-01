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
package ro.ciprianpascu.sbus.io;

import ro.ciprianpascu.sbus.net.UDPTerminal;

/**
 * Factory interface for creating UDP-based SbusTransport instances.
 * This interface defines a factory method for creating transport objects
 * that use UDP terminals for communication, allowing for flexible
 * instantiation of transport implementations for UDP-based SBus communication.
 *
 * @author Sami Salonen
 */
public interface SbusUDPTransportFactory {
    /**
     * Creates a new SbusTransport instance based on the provided UDP terminal.
     *
     * @param terminal the UDP terminal to use for transport communication
     * @return a new SbusTransport instance
     */
    public SbusTransport create(UDPTerminal terminal);
}
