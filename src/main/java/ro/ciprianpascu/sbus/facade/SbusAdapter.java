package ro.ciprianpascu.sbus.facade;

import java.net.InetAddress;

import ro.ciprianpascu.sbus.SbusException;
import ro.ciprianpascu.sbus.io.SbusUDPTransaction;
import ro.ciprianpascu.sbus.msg.ReadRgbwRequest;
import ro.ciprianpascu.sbus.msg.ReadRgbwResponse;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsRequest;
import ro.ciprianpascu.sbus.msg.ReadStatusChannelsResponse;
import ro.ciprianpascu.sbus.msg.ReadTemperatureRequest;
import ro.ciprianpascu.sbus.msg.ReadTemperatureResponse;
import ro.ciprianpascu.sbus.msg.SbusMessage;
import ro.ciprianpascu.sbus.msg.WriteRgbwRequest;
import ro.ciprianpascu.sbus.msg.WriteSingleChannelRequest;
import ro.ciprianpascu.sbus.net.UDPMasterConnection;
import ro.ciprianpascu.sbus.procimg.ByteRegister;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.Register;
import ro.ciprianpascu.sbus.procimg.WordRegister;

/**
 * Adapter class for SBUS UDP communication that provides high-level methods for interacting
 * with SBUS devices. This class handles UDP connections and protocol-specific message formatting.
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
     * Reads the status of all channels from a specified SBUS device.
     *
     * @param subnetId The subnet identifier of the target device
     * @param unitId The unit identifier within the subnet
     * @return An array of integer values representing the status of each channel
     * @throws SbusException If reading fails or an invalid response is received
     */
    public int[] readStatusChannels(int subnetId, int unitId) throws SbusException {
        ReadStatusChannelsRequest request = new ReadStatusChannelsRequest();
        request.setSubnetID(subnetId);
        request.setUnitID(unitId);

        SbusUDPTransaction transaction = new SbusUDPTransaction(connection);
        transaction.setRequest(request);

        try {
            transaction.execute();
            SbusMessage response = transaction.getResponse();
            if (response instanceof ReadStatusChannelsResponse) {
                ReadStatusChannelsResponse statusResponse = (ReadStatusChannelsResponse) response;
                InputRegister[] registers = statusResponse.getRegisters();

                int[] statuses = new int[registers.length];
                for (int i = 0; i < registers.length; i++) {
                    statuses[i] = registers[i].getValue();
                }
                return statuses;

            } else {
                throw new SbusException("Invalid response received");
            }
        } catch (Exception e) {
            throw new SbusException("Error reading status channels: " + e.getMessage());
        }
    }

    /**
     * Writes a state value to a single channel, optionally with a timer.
     *
     * @param subnetId The subnet identifier of the target device
     * @param unitId The unit identifier within the subnet
     * @param channelNumber The channel number to write to
     * @param state The state value to write (between 0 and 100)
     * @param timer Timer value in seconds, or negative value for no timer
     * @throws SbusException If writing fails
     */
    public void writeSingleChannel(int subnetId, int unitId, int channelNumber, int state, int timer)
            throws SbusException {
        WriteSingleChannelRequest request = new WriteSingleChannelRequest(timer >= 0);
        request.setSubnetID(subnetId);
        request.setUnitID(unitId);
        request.setChannelNo(channelNumber);

        // Create registers for value and timer
        Register[] registers;
        if (timer >= 0) {
            registers = new Register[2];
            registers[0] = new ByteRegister((byte) state); // Value register
            registers[1] = new WordRegister((short) timer); // Timer register, default to 0
        } else {
            registers = new Register[1];
            registers[0] = new ByteRegister((byte) state); // Value register
        }

        request.setRegisters(registers); // Set the registers

        SbusUDPTransaction transaction = new SbusUDPTransaction(connection);
        transaction.setRequest(request);

        try {
            transaction.execute();
            // Optionally handle the response if needed
        } catch (Exception e) {
            throw new SbusException("Error writing to single channel: " + e.getMessage());
        }
    }

    /**
     * Reads temperature values from all temperature sensors on a specified SBUS device.
     *
     * @param subnetId The subnet identifier of the target device
     * @param unitId The unit identifier within the subnet
     * @param temperatureUnit The unit of measurement (e.g., 0 for Fahrenheit, 1 for Celsius)
     * @return An array of float values representing temperatures from each sensor
     * @throws SbusException If reading fails or an invalid response is received
     */
    public float[] readTemperatures(int subnetId, int unitId, int temperatureUnit) throws SbusException {
        ReadTemperatureRequest request = new ReadTemperatureRequest();
        request.setTemperatureUnit(temperatureUnit); // Celsius
        request.setSubnetID(subnetId);
        request.setUnitID(unitId);

        SbusUDPTransaction transaction = new SbusUDPTransaction(connection);
        transaction.setRequest(request);

        try {
            transaction.execute();
            SbusMessage response = transaction.getResponse();
            if (response instanceof ReadTemperatureResponse) {
                ReadTemperatureResponse tempResponse = (ReadTemperatureResponse) response;
                InputRegister[] registers = tempResponse.getRegisters();
                float[] values = new float[registers.length];
                for (int i = 0; i < registers.length; i++) {
                    values[i] = registers[i].getValue();
                }
                return values;

            } else {
                throw new SbusException("Invalid response received");
            }
        } catch (Exception e) {
            throw new SbusException("Error reading temperature: " + e.getMessage());
        }
    }

    /**
     * Reads RGBW (Red, Green, Blue, White) values from a specified channel.
     *
     * @param subnetId The subnet identifier of the target device
     * @param unitId The unit identifier within the subnet
     * @param channelNumber The channel number to read from
     * @return An array of 4 integers representing RGBW values (0-255 each)
     * @throws SbusException If reading fails or an invalid response is received
     */
    public int[] readRgbw(int subnetId, int unitId, int channelNumber) throws SbusException {
        ReadRgbwRequest request = new ReadRgbwRequest();
        request.setSubnetID(subnetId);
        request.setUnitID(unitId);
        request.setLoopNumber(channelNumber);

        SbusUDPTransaction transaction = new SbusUDPTransaction(connection);
        transaction.setRequest(request);

        try {
            transaction.execute();
            SbusMessage response = transaction.getResponse();
            if (response instanceof ReadRgbwResponse) {
                ReadRgbwResponse rgbwResponse = (ReadRgbwResponse) response;
                InputRegister[] registers = rgbwResponse.getRegisters();

                int[] rgbw = new int[registers.length];
                for (int i = 0; i < registers.length; i++) {
                    rgbw[i] = registers[i].toUnsignedShort();
                }
                return rgbw;
            } else {
                throw new SbusException("Invalid response received");
            }
        } catch (Exception e) {
            throw new SbusException("Error reading RGBW values: " + e.getMessage());
        }
    }

    /**
     * Writes RGBW (Red, Green, Blue, White) values to a specified channel.
     *
     * @param subnetId The subnet identifier of the target device
     * @param unitId The unit identifier within the subnet
     * @param channelNumber The channel number to write to
     * @param color An array of 4 integers representing RGBW values (0-255 each)
     * @throws SbusException If writing fails
     */
    public void writeRgbw(int subnetId, int unitId, int channelNumber, int[] color) throws SbusException {
        // Create registers for RGBW values
        Register[] registers = new Register[4];
        registers[0] = new ByteRegister((byte) color[0]);
        registers[1] = new ByteRegister((byte) color[1]);
        registers[2] = new ByteRegister((byte) color[2]);
        registers[3] = new ByteRegister((byte) color[3]);

        WriteRgbwRequest request = new WriteRgbwRequest(channelNumber, registers);
        request.setSubnetID(subnetId);
        request.setUnitID(unitId);

        SbusUDPTransaction transaction = new SbusUDPTransaction(connection);
        transaction.setRequest(request);

        try {
            transaction.execute();
            // Response handling is optional as per the test case
        } catch (Exception e) {
            throw new SbusException("Error writing RGBW values: " + e.getMessage());
        }
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
