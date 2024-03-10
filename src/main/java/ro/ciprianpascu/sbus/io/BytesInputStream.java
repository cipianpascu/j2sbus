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

package ro.ciprianpascu.sbus.io;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * Class implementing a byte array input stream with
 * a DataInput interface.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class BytesInputStream extends FastByteArrayInputStream implements DataInput {

    DataInputStream m_Din;

    /**
     * Constructs a new {@link BytesInputStream} instance,
     * with an empty buffer of a given size.
     *
     * @param size the size of the input buffer.
     */
    public BytesInputStream(int size) {
        super(new byte[size]);
        m_Din = new DataInputStream(this);
    }// BytesInputStream

    /**
     * Constructs a new {@link BytesInputStream} instance,
     * that will read from the given data.
     *
     * @param data a byte array containing data to be read.
     */
    public BytesInputStream(byte[] data) {
        super(data);
        m_Din = new DataInputStream(this);
    }// BytesInputStream

    /**
     * Resets this {@link BytesInputStream} using the given
     * byte[] as new input buffer.
     *
     * @param data a byte array with data to be read.
     */
    public void reset(byte[] data) {
        // System.out.println("reset(byte[])::count=" + count + " pos=" + pos);
        pos = 0;
        mark = 0;
        buf = data;
        count = data.length;
    }// reset

    /**
     * Resets this {@link BytesInputStream} using the given
     * byte[] as new input buffer and a given length.
     *
     * @param data a byte array with data to be read.
     * @param length the length of the buffer to be considered.
     */
    public void reset(byte[] data, int length) {
        pos = 0;
        mark = 0;
        count = length;
        buf = data;
        readlimit = -1;
        // System.out.println("reset(byte[],int)::count=" + count + " pos=" + pos);
    }// reset

    /**
     * Resets this {@link BytesInputStream} assigning the input buffer
     * a new length.
     *
     * @param length the length of the buffer to be considered.
     */
    public void reset(int length) {
        // System.out.println("reset(int)::count=" + count + " pos=" + pos);
        pos = 0;
        count = length;
    }// reset

    /**
     * Skips the given number of bytes or all bytes till the end
     * of the assigned input buffer length.
     *
     * @param n the number of bytes to be skipped as {@link int}.
     * @return the number of bytes skipped.
     */
    public int skip(int n) {
        mark(pos);
        pos += n;
        return n;
    }// skip

    /**
     * Returns the reference to the input buffer.
     *
     * @return the reference to the {@link byte[]} input buffer.
     */
    @Override
    public byte[] getBuffer() {
        return buf;
    }// getBuffer

    /**
     * Returns the length of the input buffer.
     * @return Returns the length of the input buffer.
     */
    public int getBufferLength() {
        return buf.length;
    }// getBufferLength

    /**
     * Reads the given number of bytes from the input buffer.
     * @param b the byte array to be filled.
     */
    @Override
    public void readFully(byte b[]) throws IOException {
        m_Din.readFully(b);
    }// readFully

    /**
     * Reads bytes into an array from the input buffer.
     *
     * @param b   The buffer into which the data is read.
     * @param off The starting offset in the array.
     * @param len The number of bytes to read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void readFully(byte b[], int off, int len) throws IOException {
        m_Din.readFully(b, off, len);
    }// readFully

    /**
     * Skips over and discards n bytes of data from the input buffer.
     *
     * @param n The number of bytes to be skipped as an `int`.
     * @return The actual number of bytes skipped.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int skipBytes(int n) throws IOException {
        return m_Din.skipBytes(n);
    }// skipBytes

    /**
     * Reads a boolean value from the input buffer.
     *
     * @return The boolean value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public boolean readBoolean() throws IOException {
        return m_Din.readBoolean();
    }// readBoolean

    /**
     * Reads a signed 8-bit value from the input buffer.
     *
     * @return The byte value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public byte readByte() throws IOException {
        return m_Din.readByte();
    }

    /**
     * Reads an unsigned 8-bit value from the input buffer.
     *
     * @return The unsigned byte value read as an `int`.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int readUnsignedByte() throws IOException {
        return m_Din.readUnsignedByte();
    }// readUnsignedByte

    /**
     * Reads a signed 16-bit value from the input buffer.
     *
     * @return The short value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public short readShort() throws IOException {
        return m_Din.readShort();
    }// readShort

    /**
     * Reads an unsigned 16-bit value from the input buffer.
     *
     * @return The unsigned short value read as an `int`.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int readUnsignedShort() throws IOException {
        return m_Din.readUnsignedShort();
    }// readUnsignedShort

    /**
     * Reads a 16-bit Unicode character from the input buffer.
     *
     * @return The character value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public char readChar() throws IOException {
        return m_Din.readChar();
    }// readChar

    /**
     * Reads a signed 32-bit integer from the input buffer.
     *
     * @return The integer value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public int readInt() throws IOException {
        return m_Din.readInt();
    }// readInt

    /**
     * Reads a signed 64-bit long value from the input buffer.
     *
     * @return The long value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public long readLong() throws IOException {
        return m_Din.readLong();
    }// readLong

    // @commentstart@
    /**
     * Reads a 32-bit floating point value from the input buffer.
     *
     * @return The float value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public float readFloat() throws IOException {
        return m_Din.readFloat();
    }// readFloat

    /**
     * Reads a 64-bit floating point value from the input buffer.
     *
     * @return The double value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public double readDouble() throws IOException {
        return m_Din.readDouble();
    }// readDouble
     // @commentend@

    /**
     * Reads a line of text from the input buffer. This method is not supported and will always throw an IOException.
     *
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public String readLine() throws IOException {
        throw new IOException("Not supported.");
    }// readLine

    /**
     * Reads a UTF-8 encoded string from the input buffer.
     *
     * @return The string value read.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public String readUTF() throws IOException {
        return m_Din.readUTF();
    }// readUTF

}// class BytesInputStream
