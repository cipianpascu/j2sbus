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

package ro.ciprianpascu.sbus.procimg;

/**
 * Interface defining a process image
 * in an object oriented manner.
* 
 * The process image is understood as a shared
 * memory area used form communication between
 * slave and master or device side.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public interface ProcessImage {

    /**
     * Returns a range of {@link DigitalOut} instances.
* 
     *
     * @param offset the start offset.
     * @param count the amount of {@link DigitalOut} from the offset.
     *
     * @return an array of {@link DigitalOut} instances.
     *
     * @throws IllegalAddressException if the range from offset
     *             to offset+count is non existant.
     */
    public DigitalOut[] getDigitalOutRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the {@link DigitalOut} instance at the given
     * reference.
* 
     *
     * @param ref the reference.
     *
     * @return the {@link DigitalOut} instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    public DigitalOut getDigitalOut(int ref) throws IllegalAddressException;

    /**
     * Returns the number of {@link DigitalOut} instances
     * in this {@link ProcessImage}.
     *
     * @return the number of digital outs as {@link int}.
     */
    public int getDigitalOutCount();

    /**
     * Returns a range of {@link DigitalIn} instances.
* 
     *
     * @param offset the start offset.
     * @param count the amount of {@link DigitalIn} from the offset.
     *
     * @return an array of {@link DigitalIn} instances.
     *
     * @throws IllegalAddressException if the range from offset
     *             to offset+count is non existant.
     */
    public DigitalIn[] getDigitalInRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the {@link DigitalIn} instance at the given
     * reference.
* 
     *
     * @param ref the reference.
     *
     * @return the {@link DigitalIn} instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    public DigitalIn getDigitalIn(int ref) throws IllegalAddressException;

    /**
     * Returns the number of {@link DigitalIn} instances
     * in this {@link ProcessImage}.
     *
     * @return the number of digital ins as {@link int}.
     */
    public int getDigitalInCount();

    /**
     * Returns a range of {@link InputRegister} instances.
* 
     *
     * @param offset the start offset.
     * @param count the amount of {@link InputRegister}
     *            from the offset.
     *
     * @return an array of {@link InputRegister} instances.
     *
     * @throws IllegalAddressException if the range from offset
     *             to offset+count is non existant.
     */
    public InputRegister[] getInputRegisterRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the {@link InputRegister} instance at the given
     * reference.
* 
     *
     * @param ref the reference.
     *
     * @return the {@link InputRegister} instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    public InputRegister getInputRegister(int ref) throws IllegalAddressException;

    /**
     * Returns the number of {@link InputRegister} instances
     * in this {@link ProcessImage}.
     *
     * @return the number of input registers as {@link int}.
     */
    public int getInputRegisterCount();

    /**
     * Returns a range of {@link Register} instances.
* 
     *
     * @param offset the start offset.
     * @param count the amount of {@link Register} from the offset.
     *
     * @return an array of {@link Register} instances.
     *
     * @throws IllegalAddressException if the range from offset
     *             to offset+count is non existant.
     */
    public Register[] getRegisterRange(int offset, int count) throws IllegalAddressException;

    /**
     * Returns the {@link Register} instance at the given
     * reference.
* 
     *
     * @param ref the reference.
     *
     * @return the {@link Register} instance at the given address.
     *
     * @throws IllegalAddressException if the reference is invalid.
     */
    public Register getRegister(int ref) throws IllegalAddressException;

    /**
     * Returns the number of {@link Register} instances
     * in this {@link ProcessImage}.
     *
     * @return the number of registers as {@link int}.
     */
    public int getRegisterCount();

}// interface ProcessImage
