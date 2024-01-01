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
package com.ciprianpascu.j2sbus.modbus.procimg;

/**
 * Interface defining implementation specific details of the
 * <code>ProcessImage</code>, adding mechanisms for creating and modifying the
 * actual "process image".
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public interface ProcessImageImplementation extends ProcessImage {

    /**
     * Defines the set state (i.e. <b>true</b>) of a digital input or output.
     */
    byte DIG_TRUE = 1;
    /**
     * Defines the unset state (i.e. <b>false</b>) of a digital input or output.
     */
    byte DIG_FALSE = 0;
    /**
     * Defines the invalid (unset, neither true nor false) state of a digital
     * input or output.
     */
    byte DIG_INVALID = -1;

    /**
     * Sets a new <code>DigitalOut</code> instance at the given reference.
     *
     * @param ref the reference as <code>int</code>.
     * @param out the new <code>DigitalOut</code> instance to be set.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    void setDigitalOut(int ref, DigitalOut out) throws IllegalAddressException;

    /**
     * Adds a new <code>DigitalOut</code> instance.
     *
     * @param out the <code>DigitalOut</code> instance to be added.
     */
    void addDigitalOut(DigitalOut out);

    /**
     * Adds a new <code>DigitalOut</code> instance at the given reference.
     *
     * @param ref - the reference for the instance.
     * @param out - the <code>DigitalOut</code> instance to be added.
     */
    void addDigitalOut(int ref, DigitalOut out);

    /**
     * Removes a given <code>DigitalOut</code> instance.
     *
     * @param out the <code>DigitalOut</code> instance to be removed.
     */
    void removeDigitalOut(DigitalOut out);

    /**
     * Sets a new <code>DigitalIn</code> instance at the given reference.
     *
     * @param ref the reference as <code>int</code>.
     * @param di  the new <code>DigitalIn</code> instance to be set.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    void setDigitalIn(int ref, DigitalIn di) throws IllegalAddressException;

    /**
     * Adds a new <code>DigitalIn</code> instance.
     *
     * @param di the <code>DigitalIn</code> instance to be added.
     */
    void addDigitalIn(DigitalIn di);

    /**
     * Adds a new <code>DigitalIn</code> instance at the given reference, possibly
     * creating a hole between the last existing reference and the new object.
     *
     * @param ref - the reference for the new instance.
     * @param di  the <code>DigitalIn</code> instance to be added.
     */
    void addDigitalIn(int ref, DigitalIn di);

    /**
     * Removes a given <code>DigitalIn</code> instance.
     *
     * @param di the <code>DigitalIn</code> instance to be removed.
     */
    void removeDigitalIn(DigitalIn di);

    /**
     * Sets a new <code>InputRegister</code> instance at the given reference.
     *
     * @param ref the reference as <code>int</code>.
     * @param reg the new <code>InputRegister</code> instance to be set.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    void setInputRegister(int ref, InputRegister reg) throws IllegalAddressException;

    /**
     * Adds a new <code>InputRegister</code> instance.
     *
     * @param reg the <code>InputRegister</code> instance to be added.
     */
    void addInputRegister(InputRegister reg);

    /**
     * Adds a new <code>InputRegister</code> instance, possibly
     * creating a hole between the last existing reference and the new object.
     *
     * @param ref - The reference for the new instance.
     * @param reg the <code>InputRegister</code> instance to be added.
     */
    void addInputRegister(int ref, InputRegister reg);

    /**
     * Removes a given <code>InputRegister</code> instance.
     *
     * @param reg the <code>InputRegister</code> instance to be removed.
     */
    void removeInputRegister(InputRegister reg);

    /**
     * Sets a new <code>Register</code> instance at the given reference.
     *
     * @param ref the reference as <code>int</code>.
     * @param reg the new <code>Register</code> instance to be set.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    void setRegister(int ref, Register reg) throws IllegalAddressException;

    /**
     * Adds a new <code>Register</code> instance.
     *
     * @param reg the <code>Register</code> instance to be added.
     */
    void addRegister(Register reg);

    /**
     * Adds a new <code>Register</code> instance, possibly
     * creating a hole between the last existing reference and the new object.
     *
     * @param ref - the reference for the new instance.
     * @param reg the <code>Register</code> instance to be added.
     */
    void addRegister(int ref, Register reg);

    /**
     * Removes a given <code>Register</code> instance.
     *
     * @param reg the <code>Register</code> instance to be removed.
     */
    void removeRegister(Register reg);

    /**
     * Sets a new <code>File</code> instance at the given reference.
     *
     * @param ref the reference as <code>int</code>.
     * @param reg the new <code>File</code> instance to be set.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    void setFile(int ref, File reg) throws IllegalAddressException;

    /**
     * Adds a new <code>File</code> instance.
     *
     * @param reg the <code>File</code> instance to be added.
     */
    void addFile(File reg);

    /**
     * Adds a new <code>File</code> instance, possibly
     * creating a hole between the last existing reference and the new object.
     *
     * @param ref - the reference for the new isntance.
     * @param reg the <code>File</code> instance to be added.
     */
    void addFile(int ref, File reg);

    /**
     * Removes a given <code>File</code> instance.
     *
     * @param reg the <code>File</code> instance to be removed.
     */
    void removeFile(File reg);

    /**
     * Sets a new <code>FIFO</code> instance at the given reference.
     *
     * @param ref the reference as <code>int</code>.
     * @param reg the new <code>FIFO</code> instance to be set.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    void setFIFO(int ref, FIFO reg) throws IllegalAddressException;

    /**
     * Adds a new <code>FIFO</code> instance.
     *
     * @param reg the <code>FIFO</code> instance to be added.
     */
    void addFIFO(FIFO reg);

    /**
     * Adds a new <code>FIFO</code> instance, possibly
     * creating a hole between the last existing reference and the new object.
     *
     * @param ref - the reference for the new instance.
     * @param reg the <code>FIFO</code> instance to be added.
     */
    void addFIFO(int ref, FIFO reg);

    /**
     * Removes a given <code>FIFO</code> instance.
     *
     * @param reg the <code>FIFO</code> instance to be removed.
     */
    void removeFIFO(FIFO reg);
}
