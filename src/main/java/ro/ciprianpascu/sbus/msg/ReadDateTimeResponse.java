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

/**
 * Class implementing a {@link ReadDateTimeResponse}.
 * Carries the current date and time read from a logic module device.
 * It encapsulates the corresponding response message for OpCode 0x02C1.
 * The response payload is always 6 bytes:
 * <ul>
 *   <li>Index 0: Year offset (real year = value + 2000)</li>
 *   <li>Index 1: Month (1-12)</li>
 *   <li>Index 2: Day (1-31)</li>
 *   <li>Index 3: Hour (0-23)</li>
 *   <li>Index 4: Minute (0-59)</li>
 *   <li>Index 5: Second (0-59)</li>
 * </ul>
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public final class ReadDateTimeResponse extends SbusResponse {

    // instance attributes
    private int m_Year;   // stored as offset: real year = m_Year + 2000
    private int m_Month;
    private int m_Day;
    private int m_Hour;
    private int m_Minute;
    private int m_Second;

    /**
     * Constructs a new {@link ReadDateTimeResponse} instance. Reader focus.
     */
    public ReadDateTimeResponse() {
        super();
        setFunctionCode(Sbus.READ_DATETIME_REQUEST + 1);
        setDataLength(6);
    }// constructor

    /**
     * Constructs a new {@link ReadDateTimeResponse} instance. Writer focus.
     *
     * @param year   the full 4-digit year (e.g. 2024); stored as (year - 2000) on the wire
     * @param month  the month (1-12)
     * @param day    the day of month (1-31)
     * @param hour   the hour of day (0-23)
     * @param minute the minute (0-59)
     * @param second the second (0-59)
     */
    public ReadDateTimeResponse(int year, int month, int day, int hour, int minute, int second) {
        super();
        setFunctionCode(Sbus.READ_DATETIME_REQUEST + 1);
        m_Year   = year - 2000;
        m_Month  = month;
        m_Day    = day;
        m_Hour   = hour;
        m_Minute = minute;
        m_Second = second;
        setDataLength(6);
    }// constructor

    /**
     * Returns the full 4-digit year (e.g. 2024).
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

    @Override
    public void writeData(DataOutput dout) throws IOException {
        dout.writeByte(m_Year);
        dout.writeByte(m_Month);
        dout.writeByte(m_Day);
        dout.writeByte(m_Hour);
        dout.writeByte(m_Minute);
        dout.writeByte(m_Second);
    }// writeData

    @Override
    public void readData(DataInput din) throws IOException {
        m_Year   = din.readUnsignedByte();
        m_Month  = din.readUnsignedByte();
        m_Day    = din.readUnsignedByte();
        m_Hour   = din.readUnsignedByte();
        m_Minute = din.readUnsignedByte();
        m_Second = din.readUnsignedByte();
        setDataLength(6);
    }// readData

}// class ReadDateTimeResponse
