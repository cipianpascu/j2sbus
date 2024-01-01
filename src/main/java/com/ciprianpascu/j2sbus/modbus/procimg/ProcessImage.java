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
 * Interface defining a process image in an object oriented manner.
 *
 * The process image is understood as a shared memory area used form
 * communication between slave and master or device side.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public interface ProcessImage {

    /**
     * Returns a range of <code>DigitalOut</code> instances.
     *
     * @param offset the start offset.
     * @param count  the amount of <code>DigitalOut</code> from the offset.
     *
     * @return an array of <code>DigitalOut</code> instances.
     *
     * @throws IllegalAddressException if the range from offset to offset+count is non existant.
     */
    DigitalOut[] getDigitalOutRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the <code>DigitalOut</code> instance at the given reference.
     *
     * @param ref the reference.
     *
     * @return the <code>DigitalOut</code> instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    DigitalOut getDigitalOut(int ref) throws IllegalAddressException;

    /**
     * Returns the number of <code>DigitalOut</code> instances in this
     * <code>ProcessImage</code>.
     *
     * @return the number of digital outs as <code>int</code>.
     */
    int getDigitalOutCount();

    /**
     * Returns a range of <code>DigitalIn</code> instances.
     *
     * @param offset the start offset.
     * @param count  the amount of <code>DigitalIn</code> from the offset.
     *
     * @return an array of <code>DigitalIn</code> instances.
     *
     * @throws IllegalAddressException if the range from offset to offset+count is non existant.
     */
    DigitalIn[] getDigitalInRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the <code>DigitalIn</code> instance at the given reference.
     *
     * @param ref the reference.
     *
     * @return the <code>DigitalIn</code> instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    DigitalIn getDigitalIn(int ref) throws IllegalAddressException;

    /**
     * Returns the number of <code>DigitalIn</code> instances in this
     * <code>ProcessImage</code>.
     *
     * @return the number of digital ins as <code>int</code>.
     */
    int getDigitalInCount();

    /**
     * Returns a range of <code>InputRegister</code> instances.
     *
     * @param offset the start offset.
     * @param count  the amount of <code>InputRegister</code> from the offset.
     *
     * @return an array of <code>InputRegister</code> instances.
     *
     * @throws IllegalAddressException if the range from offset to offset+count is non existant.
     */
    InputRegister[] getInputRegisterRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the <code>InputRegister</code> instance at the given reference.
     *
     * @param ref the reference.
     *
     * @return the <code>InputRegister</code> instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    InputRegister getInputRegister(int ref) throws IllegalAddressException;

    /**
     * Returns the number of <code>InputRegister</code> instances in this
     * <code>ProcessImage</code>.
     *
     *
     * This is not the same as the value of the highest addressable register.
     *
     * @return the number of input registers as <code>int</code>.
     */
    int getInputRegisterCount();

    /**
     * Returns a range of <code>Register</code> instances.
     *
     * @param offset the start offset.
     * @param count  the amount of <code>Register</code> from the offset.
     *
     * @return an array of <code>Register</code> instances.
     *
     * @throws IllegalAddressException if the range from offset to offset+count is non existant.
     */
    Register[] getRegisterRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the <code>Register</code> instance at the given reference.
     *
     *
     * @param ref the reference.
     *
     * @return the <code>Register</code> instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    Register getRegister(int ref) throws IllegalAddressException;

    /**
     * Returns the number of <code>Register</code> instances in this
     * <code>ProcessImage</code>.
     *
     *
     * This is not the same as the value of the highest addressable register.
     *
     * @return the number of registers as <code>int</code>.
     */
    int getRegisterCount();

    /**
     * Returns the <code>File</code> instance at the given reference.
     *
     *
     * @param ref the reference.
     *
     * @return the <code>File</code> instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    File getFile(int ref) throws IllegalAddressException;

    /**
     * Returns the <code>File</code> instance having the specified file number.
     *
     * @param ref The file number for the File object to be returned.
     *
     * @return the <code>File</code> instance having the given number.
     *
     * @throws IllegalAddressException if a File with the given number does not exist.
     */
    File getFileByNumber(int ref) throws IllegalAddressException;

    /**
     * Returns the number of <code>File</code> instances in this
     * <code>ProcessImage</code>.
     *
     *
     * This is not the same as the value of the highest addressable register.
     *
     * @return the number of registers as <code>int</code>.
     */
    int getFileCount();

    /**
     * Returns the <code>FIFO</code> instance in the list of all FIFO objects
     * in this ProcessImage.
     *
     * @param ref the reference.
     *
     * @return the <code>File</code> instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    FIFO getFIFO(int ref) throws IllegalAddressException;

    /**
     * Returns the <code>FIFO</code> instance having the specified base address.
     *
     * @param ref The address for the FIFO object to be returned.
     *
     * @return the <code>FIFO</code> instance having the given base address
     *
     * @throws IllegalAddressException if a File with the given number does not exist.
     */
    FIFO getFIFOByAddress(int ref) throws IllegalAddressException;

    /**
     * Returns the number of <code>File</code> instances in this
     * <code>ProcessImage</code>.
     *
     *
     * This is not the same as the value of the highest addressable register.
     *
     * @return the number of registers as <code>int</code>.
     */
    int getFIFOCount();
}
