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
import ro.ciprianpascu.sbus.procimg.ProcessImageImplementation;

/**
 * Class implementing a {@link WriteDateTimeRequest}.
 * Writes the current date, time and weekday to a logic module device.
 * It encapsulates the corresponding request message for OpCode 0x02C6.
 * The request payload is 7 bytes:
 * <ul>
 *   <li>Byte 0: Year offset (real year - 2000; e.g. 26 for 2026)</li>
 *   <li>Byte 1: Month (1-12)</li>
 *   <li>Byte 2: Day (1-31)</li>
 *   <li>Byte 3: Hour (0-23)</li>
 *   <li>Byte 4: Minute (0-59)</li>
 *   <li>Byte 5: Second (0-59)</li>
 *   <li>Byte 6: Weekday (device-specific encoding)</li>
 * </ul>
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class WriteDateTimeRequest extends SbusRequest {

    // instance attributes
    private int m_Year;    // stored as offset: real year = m_Year + 2000
    private int m_Month;
    private int m_Day;
    private int m_Hour;
    private int m_Minute;
    private int m_Second;
    private int m_Weekday;

    /**
     * Constructs a new {@link WriteDateTimeRequest} instance with default values.
     */
    public WriteDateTimeRequest() {
        super();
        setFunctionCode(Sbus.WRITE_DATETIME_REQUEST);
        setDataLength(7);
    }// constructor

    /**
     * Constructs a new {@link WriteDateTimeRequest} instance with all date/time fields.
     *
     * @param year    the full 4-digit year (e.g. 2026); stored as (year - 2000) on the wire
     * @param month   the month (1-12)
     * @param day     the day of month (1-31)
     * @param hour    the hour of day (0-23)
     * @param minute  the minute (0-59)
     * @param second  the second (0-59)
     * @param weekday the weekday (device-specific encoding)
     */
    public WriteDateTimeRequest(int year, int month, int day, int hour, int minute, int second, int weekday) {
        super();
        setFunctionCode(Sbus.WRITE_DATETIME_REQUEST);
        m_Year    = year - 2000;
        m_Month   = month;
        m_Day     = day;
        m_Hour    = hour;
        m_Minute  = minute;
        m_Second  = second;
        m_Weekday = weekday;
        setDataLength(7);
    }// constructor

    /**
     * Returns the full 4-digit year (e.g. 2026).
     *
     * @return the year as {@link int}
     */
    public int getYear() {
        return m_Year + 2000;
    }// getYear

    /**
     * Returns the month.
     *
     * @return the month (1-12) as {@link int}
     */
    public int getMonth() {
        return m_Month;
    }// getMonth

    /**
     * Returns the day of the month.
     *
     * @return the day (1-31) as {@link int}
     */
    public int getDay() {
        return m_Day;
    }// getDay

    /**
     * Returns the hour of the day.
     *
     * @return the hour (0-23) as {@link int}
     */
    public int getHour() {
        return m_Hour;
    }// getHour

    /**
     * Returns the minute.
     *
     * @return the minute (0-59) as {@link int}
     */
    public int getMinute() {
        return m_Minute;
    }// getMinute

    /**
     * Returns the second.
     *
     * @return the second (0-59) as {@link int}
     */
    public int getSecond() {
        return m_Second;
    }// getSecond

    /**
     * Returns the weekday.
     *
     * @return the weekday as {@link int} (device-specific encoding)
     */
    public int getWeekday() {
        return m_Weekday;
    }// getWeekday

    @Override
    public SbusResponse createResponse(ProcessImageImplementation procimg) {
        WriteDateTimeResponse response = new WriteDateTimeResponse(true);
        // transfer header data
        response.setSourceSubnetID(this.getSourceSubnetID());
        response.setSourceUnitID(this.getSourceUnitID());
        response.setSourceDeviceType(this.getSourceDeviceType());
        response.setSubnetID(this.getSubnetID());
        response.setUnitID(this.getUnitID());
        response.setFunctionCode(this.getFunctionCode());
        return response;
    }// createResponse

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_Year);
        dout.writeByte(m_Month);
        dout.writeByte(m_Day);
        dout.writeByte(m_Hour);
        dout.writeByte(m_Minute);
        dout.writeByte(m_Second);
        dout.writeByte(m_Weekday);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        m_Year    = din.readUnsignedByte();
        m_Month   = din.readUnsignedByte();
        m_Day     = din.readUnsignedByte();
        m_Hour    = din.readUnsignedByte();
        m_Minute  = din.readUnsignedByte();
        m_Second  = din.readUnsignedByte();
        m_Weekday = din.readUnsignedByte();
    }// readData

}// class WriteDateTimeRequest
