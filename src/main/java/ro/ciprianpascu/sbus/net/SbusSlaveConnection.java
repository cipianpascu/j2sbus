
/**
 * Copyright (c) 2010-2017 by the respective copyright holders.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 */
package ro.ciprianpascu.sbus.net;

/**
 * Interface defining the connection between a slave device and the S-Bus network.
 * This interface provides methods for establishing, checking, and resetting connections
 * between S-Bus slave devices and the network. It abstracts the underlying connection
 * details and provides a consistent API for managing slave device connectivity.
 * 
 * @author Sami Salonen
 **/
public interface SbusSlaveConnection {
    /**
     * Connects the connection to the endpoint
     *
     * @return whether connection was successfull
     * @throws Exception on any connection errors
     */
    public boolean connect() throws Exception;

    /**
     * Close connection and free associated resources
     */
    public void resetConnection();

    /**
     * Checks if the connection is currently established and active.
     *
     * @return whether connection is now fully connected
     */
    public boolean isConnected();

}
