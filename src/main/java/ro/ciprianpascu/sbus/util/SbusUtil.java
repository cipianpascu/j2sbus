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

package ro.ciprianpascu.sbus.util;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ro.ciprianpascu.sbus.Sbus;
import ro.ciprianpascu.sbus.io.BytesOutputStream;
import ro.ciprianpascu.sbus.msg.SbusMessage;

/**
 * Helper class that provides utility methods for the SBus protocol implementation.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @author John Charlton
 * @version %I% (%G%)
 */
public final class SbusUtil {

    private static BytesOutputStream m_ByteOut = new BytesOutputStream(Sbus.MAX_MESSAGE_LENGTH);

    /**
     * Converts a {@link SbusMessage} instance into a hex encoded string representation.
     *
     * @param msg the message to be converted
     * @return the converted hex encoded string representation of the message
     */
    public static final String toHex(SbusMessage msg) {
        String ret = "-1";
        try {
            synchronized (m_ByteOut) {
                msg.writeTo(m_ByteOut);
                ret = toHex(m_ByteOut.getBuffer(), 0, m_ByteOut.size());
                m_ByteOut.reset();
            }
        } catch (IOException ex) {
        }
        return ret;
    }

    /**
     * Returns the given byte array as hex encoded string.
     *
     * @param data a byte array to convert
     * @return a hex encoded String
     */
    public static final String toHex(byte[] data) {
        return toHex(data, 0, data.length);
    }

    /**
     * Returns a String containing unsigned hexadecimal numbers as digits.
     * The String will contain two hex digit characters for each byte from 
     * the passed in byte array. The bytes will be separated by a space character.
     *
     * @param data the array of bytes to be converted into a hex-string
     * @param off the offset to start converting from
     * @param length the number of bytes to be converted
     * @return the generated hexadecimal representation as String
     */
    public static final String toHex(byte[] data, int off, int length) {
        StringBuffer buf = new StringBuffer(data.length * 2);
        for (int i = off; i < length; i++) {
            if ((data[i] & 0xff) < 0x10) {
                buf.append("0");
            }
            buf.append(Long.toString(data[i] & 0xff, 16));
            if (i < data.length - 1) {
                buf.append(" ");
            }
        }
        return buf.toString();
    }

    /**
     * Returns a byte array containing the given byte as unsigned hexadecimal number digits.
     *
     * @param i the integer to be converted into a hex string
     * @return the generated hexadecimal representation as byte array
     */
    public static final byte[] toHex(int i) {
        StringBuffer buf = new StringBuffer(2);
        if ((i & 0xff) < 0x10) {
            buf.append("0");
        }
        buf.append(Long.toString(i & 0xff, 16).toUpperCase());
        return buf.toString().getBytes();
    }

    /**
     * Converts the register (a 16 bit value) into an unsigned short.
     * The value returned is: <code>(((a &amp; 0xff) &lt;&lt; 8) | (b &amp; 0xff))</code>
     *
     * @param bytes a register as byte[2]
     * @return the unsigned short value as int
     */
    public static final int registerToUnsignedShort(byte[] bytes) {
        return ((bytes[0] & 0xff) << 8 | (bytes[1] & 0xff));
    }

    /**
     * Converts the given unsigned short into a register (2 bytes).
     * The byte values in the register, in the order shown, are:
     * <code>(byte)(0xff &amp; (v &gt;&gt; 8))</code>
     * <code>(byte)(0xff &amp; v)</code>
     *
     * @param v the value to be converted
     * @return the register as byte[2]
     */
    public static final byte[] unsignedShortToRegister(int v) {
        byte[] register = new byte[2];
        register[0] = (byte) (0xff & (v >> 8));
        register[1] = (byte) (0xff & v);
        return register;
    }

    /**
     * Converts the given register (16-bit value) into a short.
     * The value returned is: <code>(short)((a &lt;&lt; 8) | (b &amp; 0xff))</code>
     *
     * @param bytes a register as byte[2]
     * @return the signed short value
     */
    public static final short registerToShort(byte[] bytes) {
        return (short) ((bytes[0] << 8) | (bytes[1] & 0xff));
    }

    /**
     * Converts the register (16-bit value) at the given index into a short.
     * The value returned is: <code>(short)((a &lt;&lt; 8) | (b &amp; 0xff))</code>
     *
     * @param bytes a byte array containing a short value
     * @param idx an offset into the given byte array
     * @return the signed short value
     */
    public static final short registerToShort(byte[] bytes, int idx) {
        return (short) ((bytes[idx] << 8) | (bytes[idx + 1] & 0xff));
    }

    /**
     * Converts the given short into a register (2 bytes).
     * The byte values in the register, in the order shown, are:
     * <code>(byte)(0xff &amp; (v &gt;&gt; 8))</code>
     * <code>(byte)(0xff &amp; v)</code>
     *
     * @param s the short value to convert
     * @return a register containing the given short value
     */
    public static final byte[] shortToRegister(short s) {
        byte[] register = new byte[2];
        register[0] = (byte) (0xff & (s >> 8));
        register[1] = (byte) (0xff & s);
        return register;
    }

    /**
     * Converts a byte[4] binary int value to a primitive int.
     * The value returned is:
     * <code>(((a &amp; 0xff) &lt;&lt; 24) | ((b &amp; 0xff) &lt;&lt; 16) |
     * ((c &amp; 0xff) &lt;&lt; 8) | (d &amp; 0xff))</code>
     *
     * @param bytes registers as byte[4]
     * @return the integer contained in the given register bytes
     */
    public static final int registersToInt(byte[] bytes) {
        return (((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | 
                ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff));
    }

    /**
     * Converts an int value to a byte[4] array.
     *
     * @param v the value to be converted
     * @return a byte[4] containing the value
     */
    public static final byte[] intToRegisters(int v) {
        byte[] registers = new byte[4];
        registers[0] = (byte) (0xff & (v >> 24));
        registers[1] = (byte) (0xff & (v >> 16));
        registers[2] = (byte) (0xff & (v >> 8));
        registers[3] = (byte) (0xff & v);
        return registers;
    }

    /**
     * Converts a byte[8] binary long value into a long primitive.
     *
     * @param bytes a byte[8] containing a long value
     * @return the long value
     */
    public static final long registersToLong(byte[] bytes) {
        return ((((long) (bytes[0] & 0xff) << 56) | ((long) (bytes[1] & 0xff) << 48) | 
                ((long) (bytes[2] & 0xff) << 40) | ((long) (bytes[3] & 0xff) << 32) | 
                ((long) (bytes[4] & 0xff) << 24) | ((long) (bytes[5] & 0xff) << 16) | 
                ((long) (bytes[6] & 0xff) << 8) | (bytes[7] & 0xff)));
    }

    /**
     * Converts a long value to a byte[8].
     *
     * @param v the value to be converted
     * @return a byte[8] containing the long value
     */
    public static final byte[] longToRegisters(long v) {
        byte[] registers = new byte[8];
        registers[0] = (byte) (0xff & (v >> 56));
        registers[1] = (byte) (0xff & (v >> 48));
        registers[2] = (byte) (0xff & (v >> 40));
        registers[3] = (byte) (0xff & (v >> 32));
        registers[4] = (byte) (0xff & (v >> 24));
        registers[5] = (byte) (0xff & (v >> 16));
        registers[6] = (byte) (0xff & (v >> 8));
        registers[7] = (byte) (0xff & v);
        return registers;
    }

    /**
     * Converts a byte[4] binary float value to a float primitive.
     *
     * @param bytes the byte[4] containing the float value
     * @return the float value
     */
    public static final float registersToFloat(byte[] bytes) {
        return Float.intBitsToFloat(
                (((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | 
                 ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff)));
    }

    /**
     * Converts a float value to a byte[4] binary float value.
     *
     * @param f the float to be converted
     * @return a byte[4] containing the float value
     */
    public static final byte[] floatToRegisters(float f) {
        return intToRegisters(Float.floatToIntBits(f));
    }

    /**
     * Converts a byte[8] binary double value into a double primitive.
     *
     * @param bytes a byte[8] to be converted
     * @return the double value
     */
    public static final double registersToDouble(byte[] bytes) {
        return Double.longBitsToDouble(
                ((((long) (bytes[0] & 0xff) << 56) | ((long) (bytes[1] & 0xff) << 48) | 
                  ((long) (bytes[2] & 0xff) << 40) | ((long) (bytes[3] & 0xff) << 32) | 
                  ((long) (bytes[4] & 0xff) << 24) | ((long) (bytes[5] & 0xff) << 16) | 
                  ((long) (bytes[6] & 0xff) << 8) | (bytes[7] & 0xff))));
    }

    /**
     * Converts a double value to a byte[8].
     *
     * @param d the double to be converted
     * @return a byte[8] containing the double value
     */
    public static final byte[] doubleToRegisters(double d) {
        return longToRegisters(Double.doubleToLongBits(d));
    }

    /**
     * Converts an unsigned byte to an integer.
     *
     * @param b the byte to be converted
     * @return an integer containing the unsigned byte value
     */
    public static final int unsignedByteToInt(byte b) {
        return b & 0xFF;
    }

    /**
     * Returns the low byte of an integer word.
     *
     * @param wd the word value
     * @return the low byte
     */
    public static final byte lowByte(int wd) {
        return (new Integer(0xff & wd).byteValue());
    }

    /**
     * Returns the high byte of an integer word.
     *
     * @param wd the word value
     * @return the high byte
     */
    public static final byte hiByte(int wd) {
        return (new Integer(0xff & (wd >> 8)).byteValue());
    }

    /**
     * Creates a word from high and low bytes.
     *
     * @param hibyte the high byte
     * @param lowbyte the low byte
     * @return the combined word value
     */
    public static final int makeWord(int hibyte, int lowbyte) {
        int hi = 0xFF & hibyte;
        int low = 0xFF & lowbyte;
        return ((hi << 8) | low);
    }

    /**
     * Calculates the CRC (Cyclic Redundancy Check) for the given data.
     *
     * @param data the data to calculate CRC for
     * @param len the length of data to include in calculation
     * @return a byte array containing the CRC value
     */
    public static final byte[] calculateCRC(byte[] data, int len) {
        int crc = 0;
        byte dat;
        int index = 0;
        
        while (len-- != 0) {
            dat = (byte)(crc >> 8);
            crc <<= 8;
            crc ^= mbufintCRCTable[(dat ^ data[index]) & 0xFF];
            index++;
        }
        return new byte[] { ((byte) (crc >> 8)), ((byte) (crc & 0x00FF)) };
    }

    /**
     * Verifies the CRC (Cyclic Redundancy Check) of a data packet.
     *
     * @param data the data to verify CRC for
     * @param len the length of data to include in verification
     * @return true if CRC is valid, false otherwise
     */
    public static boolean checkCRC(byte[] data, int len) {
        int crc = 0;
        byte dat;
        int index = 0;
        
        while (len-- != 0) {
            dat = (byte)(crc >> 8);
            crc <<= 8;
            crc ^= mbufintCRCTable[(dat ^ data[index]) & 0xFF];
            index++;
        }
        
        return (data[index] == (byte)(crc >> 8)) && (data[index + 1] == (byte)crc);
    }
    
    /** CRC lookup table for fast CRC calculation */
    private static final int[] mbufintCRCTable = {
        0x0000, 0x1021, 0x2042, 0x3063, 0x4084, 0x50a5, 0x60c6, 0x70e7,
        0x8108, 0x9129, 0xa14a, 0xb16b, 0xc18c, 0xd1ad, 0xe1ce, 0xf1ef,
        0x1231, 0x0210, 0x3273, 0x2252, 0x52b5, 0x4294, 0x72f7, 0x62d6,
        0x9339, 0x8318, 0xb37b, 0xa35a, 0xd3bd, 0xc39c, 0xf3ff, 0xe3de,
        0x2462, 0x3443, 0x0420, 0x1401, 0x64e6, 0x74c7, 0x44a4, 0x5485,
        0xa56a, 0xb54b, 0x8528, 0x9509, 0xe5ee, 0xf5cf, 0xc5ac, 0xd58d,
        0x3653, 0x2672, 0x1611, 0x0630, 0x76d7, 0x66f6, 0x5695, 0x46b4,
        0xb75b, 0xa77a, 0x9719, 0x8738, 0xf7df, 0xe7fe, 0xd79d, 0xc7bc,
        0x48c4, 0x58e5, 0x6886, 0x78a7, 0x0840, 0x1861, 0x2802, 0x3823,
        0xc9cc, 0xd9ed, 0xe98e, 0xf9af, 0x8948, 0x9969, 0xa90a, 0xb92b,
        0x5af5, 0x4ad4, 0x7ab7, 0x6a96, 0x1a71, 0x0a50, 0x3a33, 0x2a12,
        0xdbfd, 0xcbdc, 0xfbbf, 0xeb9e, 0x9b79, 0x8b58, 0xbb3b, 0xab1a,
        0x6ca6, 0x7c87, 0x4ce4, 0x5cc5, 0x2c22, 0x3c03, 0x0c60, 0x1c41,
        0xedae, 0xfd8f, 0xcdec, 0xddcd, 0xad2a, 0xbd0b, 0x8d68, 0x9d49,
        0x7e97, 0x6eb6, 0x5ed5, 0x4ef4, 0x3e13, 0x2e32, 0x1e51, 0x0e70,
        0xff9f, 0xefbe, 0xdfdd, 0xcffc, 0xbf1b, 0xaf3a, 0x9f59, 0x8f78,
        0x9188, 0x81a9, 0xb1ca, 0xa1eb, 0xd10c, 0xc12d, 0xf14e, 0xe16f,
        0x1080, 0x00a1, 0x30c2, 0x20e3, 0x5004, 0x4025, 0x7046, 0x6067,
        0x83b9, 0x9398, 0xa3fb, 0xb3da, 0xc33d, 0xd31c, 0xe37f, 0xf35e,
        0x02b1, 0x1290, 0x22f3, 0x32d2, 0x4235, 0x5214, 0x6277, 0x7256,
        0xb5ea, 0xa5cb, 0x95a8, 0x8589, 0xf56e, 0xe54f, 0xd52c, 0xc50d,
        0x34e2, 0x24c3, 0x14a0, 0x0481, 0x7466, 0x6447, 0x5424, 0x4405,
        0xa7db, 0xb7fa, 0x8799, 0x97b8, 0xe75f, 0xf77e, 0xc71d, 0xd73c,
        0x26d3, 0x36f2, 0x0691, 0x16b0, 0x6657, 0x7676, 0x4615, 0x5634,
        0xd94c, 0xc96d, 0xf90e, 0xe92f, 0x99c8, 0x89e9, 0xb98a, 0xa9ab,
        0x5844, 0x4865, 0x7806, 0x6827, 0x18c0, 0x08e1, 0x3882, 0x28a3,
        0xcb7d, 0xdb5c, 0xeb3f, 0xfb1e, 0x8bf9, 0x9bd8, 0xabbb, 0xbb9a,
        0x4a75, 0x5a54, 0x6a37, 0x7a16, 0x0af1, 0x1ad0, 0x2ab3, 0x3a92,
        0xfd2e, 0xed0f, 0xdd6c, 0xcd4d, 0xbdaa, 0xad8b, 0x9de8, 0x8dc9,
        0x7c26, 0x6c07, 0x5c64, 0x4c45, 0x3ca2, 0x2c83, 0x1ce0, 0x0cc1,
        0xef1f, 0xff3e, 0xcf5d, 0xdf7c, 0xaf9b, 0xbfba, 0x8fd9, 0x9ff8,
        0x6e17, 0x7e36, 0x4e55, 0x5e74, 0x2e93, 0x3eb2, 0x0ed1, 0x1ef0
    };
}
