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

public class SbusAdapter {

    private UDPMasterConnection connection;

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

    public float[] readTemperatures(int subnetId, int unitId) throws SbusException {
        ReadTemperatureRequest request = new ReadTemperatureRequest();
        request.setTemperatureUnit(1); // Celsius
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

    public void writeRgbw(int subnetId, int unitId, int channelNumber, int red, int green, int blue, int white)
            throws SbusException {
        // Create registers for RGBW values
        Register[] registers = new Register[4];
        registers[0] = new ByteRegister((byte) red);
        registers[1] = new ByteRegister((byte) green);
        registers[2] = new ByteRegister((byte) blue);
        registers[3] = new ByteRegister((byte) white);

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

    public void close() {
        if (connection != null) {
            connection.close();
        }
    }
}
