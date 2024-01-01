/*
 * Copyright 2002-2016 jamod & j2mod development teams
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
 */
package com.ciprianpascu.j2sbus.modbus.io;

import java.io.IOException;

import com.ciprianpascu.j2sbus.modbus.Modbus;
import com.ciprianpascu.j2sbus.modbus.ModbusIOException;
import com.ciprianpascu.j2sbus.modbus.msg.ModbusRequest;
import com.ciprianpascu.j2sbus.modbus.msg.ModbusResponse;
import com.ciprianpascu.j2sbus.modbus.net.AbstractModbusListener;

/**
 * Interface defining the I/O mechanisms for
 * <code>ModbusMessage</code> instances.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public abstract class AbstractModbusTransport {

    protected int timeout = Modbus.DEFAULT_TIMEOUT;

    /**
     * Set the socket timeout
     *
     * @param time Timeout in milliseconds
     */
    public void setTimeout(int time) {
        timeout = time;
    }

    /**
     * Closes the raw input and output streams of
     * this <code>ModbusTransport</code>.
     *
     * @throws IOException if a stream
     *                     cannot be closed properly.
     */
    public abstract void close() throws IOException;

    /**
     * Creates a Modbus transaction for the underlying transport.
     *
     * @return the new transaction
     */
    public abstract ModbusTransaction createTransaction();

    /**
     * Writes a <code>ModbusMessage</code> to the
     * output stream of this <code>ModbusTransport</code>.
     *
     * @param msg a <code>ModbusMessage</code>.
     *
     * @throws ModbusIOException data cannot be
     *                           written properly to the raw output stream of
     *                           this <code>ModbusTransport</code>.
     */
    public abstract void writeRequest(ModbusRequest msg) throws ModbusIOException;

    /**
     * Writes a <code>ModbusResponseMessage</code> to the
     * output stream of this <code>ModbusTransport</code>.
     *
     * @param msg a <code>ModbusMessage</code>.
     *
     * @throws ModbusIOException data cannot be
     *                           written properly to the raw output stream of
     *                           this <code>ModbusTransport</code>.
     */
    public abstract void writeResponse(ModbusResponse msg) throws ModbusIOException;

    /**
     * Reads a <code>ModbusRequest</code> from the
     * input stream of this <code>ModbusTransport</code>.
     *
     *
     * @param listener Listener the request was received by
     *
     * @return req the <code>ModbusRequest</code> read from the underlying stream.
     *
     * @throws ModbusIOException data cannot be
     *                           read properly from the raw input stream of
     *                           this <code>ModbusTransport</code>.
     */
    public abstract ModbusRequest readRequest(AbstractModbusListener listener) throws ModbusIOException;

    /**
     * Reads a <code>ModbusResponse</code> from the
     * input stream of this <code>ModbusTransport</code>.
     *
     *
     * @return res the <code>ModbusResponse</code> read from the underlying stream.
     *
     * @throws ModbusIOException data cannot be
     *                           read properly from the raw input stream of
     *                           this <code>ModbusTransport</code>.
     */
    public abstract ModbusResponse readResponse() throws ModbusIOException;

}