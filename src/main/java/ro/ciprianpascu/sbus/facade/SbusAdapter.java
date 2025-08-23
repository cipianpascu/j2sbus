package ro.ciprianpascu.sbus.facade;

import java.net.InetAddress;

import ro.ciprianpascu.sbus.SbusException;
import ro.ciprianpascu.sbus.io.SbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.SbusRequest;
import ro.ciprianpascu.sbus.msg.SbusResponse;
import ro.ciprianpascu.sbus.net.SbusMessageListener;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;

/**
 * Minimal facade for SBUS UDP communication that provides core transaction execution
 * and listener management. Domain-specific request/response logic has been moved
 * to the handler layer for better separation of concerns.
 */
public class SbusAdapter {

    private UDPMasterConnection connection;

    /**
     * Initializes a new SBUS adapter with the specified host and port.
     *
     * @param host The hostname or IP address of the SBUS device
     * @param port The UDP port number for communication
     * @throws SbusException If connection initialization fails
     */
    public SbusAdapter(String host, int port) throws SbusException {
        try {
            // Initialize UDPMasterConnection
            connection = new UDPMasterConnection();
            connection.setRemoteAddress(InetAddress.getByName(host));
            connection.setPort(port);
            connection.connect();
        } catch (Exception e) {
            throw new SbusException("Error initializing SBUS connection: " + e.getMessage());
        }
    }

    /**
     * Executes a synchronous SBUS transaction.
     *
     * @param request The SBUS request to execute
     * @return The SBUS response
     * @throws SbusException If the transaction fails
     */
    public SbusResponse executeTransaction(SbusRequest request) throws SbusException {
        SbusUDPTransaction transaction = new SbusUDPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();
        return transaction.getResponse();
    }

    /**
     * Adds a message listener for unsolicited SBUS messages.
     * The listener will be notified when messages arrive that are not
     * part of a synchronous request/response transaction.
     *
     * This enables real-time monitoring of device status changes,
     * alarms, or other unsolicited communications from SBUS devices.
     *
     * @param listener the listener to add for unsolicited messages
     * @throws IllegalStateException if the connection is not established
     */
    public void addMessageListener(SbusMessageListener listener) {
        if (connection == null) {
            throw new IllegalStateException("Connection not established. Cannot add listener.");
        }
        connection.addMessageListener(listener);
    }

    /**
     * Removes a previously registered message listener.
     *
     * @param listener the listener to remove
     * @throws IllegalStateException if the connection is not established
     */
    public void removeMessageListener(SbusMessageListener listener) {
        if (connection == null) {
            throw new IllegalStateException("Connection not established. Cannot remove listener.");
        }
        connection.removeMessageListener(listener);
    }

    /**
     * Closes the UDP connection to the SBUS device.
     * This method should be called when the adapter is no longer needed to free system resources.
     */
    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
