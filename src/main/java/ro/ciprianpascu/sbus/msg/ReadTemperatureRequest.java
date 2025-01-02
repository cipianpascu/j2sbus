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

package ro.ciprianpascu.sbus.msg;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.procimg.IllegalAddressException;
import ro.ciprianpascu.sbus.procimg.InputRegister;
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * Class implementing a {@link ReadTemperatureRequest}.
 * The implementation directly correlates with the class 0
 * function <i>read multiple registers (FC 4)</i>. It
 * encapsulates the corresponding request message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public final class ReadTemperatureRequest extends SbusRequest {

    // instance attributes
    private int m_TemperatureUnit;

    /**
     * Constructs a new {@link ReadTemperatureRequest}
     * instance.
     */
    public ReadTemperatureRequest() {
        super();
        setFunctionCode(Sbus.READ_TEMPERATURE_REQUEST);
        setDataLength(1);
    }// constructor


    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        ReadTemperatureResponse response = null;
        InputRegister[] inpregs = null;

        // 1. get input registers range
        try {
            inpregs = procimg.getInputRegisterRange(0, 1);
        } catch (IllegalAddressException iaex) {
            return createExceptionResponse(Sbus.ILLEGAL_ADDRESS_EXCEPTION);
        }
        response = new ReadTemperatureResponse(inpregs);
        // transfer header data
        response.setSourceSubnetID(this.getSourceSubnetID());
		response.setSourceUnitID(this.getSourceUnitID());
		response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());
        return response;
    }// createResponse

    /**
     * Sets the temperature unit
     * from with this {@link ReadTemperatureRequest}.
     * 
     *
     * @param unit the temperature unit 0 Fahrenheit, 1 Celsius
     */
    public void setTemperatureUnit(int unit) {
        m_TemperatureUnit = unit;
        // setChanged(true);
    }// setReference

    /**
     * Returns the  temperature unit from this
     * {@link ReadTemperatureRequest}.
     * 
     *
     * @return the temperature unit 0 Fahrenheit, 1 Celsius
     */
    public int getTemperatureUnit() {
        return m_TemperatureUnit;
    }// getReference


    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_TemperatureUnit);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        m_TemperatureUnit = din.readUnsignedByte();
    }// readData

}// class ReadStatusChannelsRequest
