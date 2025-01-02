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

import ro.ciprianpascu.sbus.io.Transportable;

/**
 * Interface defining a Sbus Message.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public interface SbusMessage extends Transportable {


    /**
     * Returns the length of the data appended
     * after the protocol header.
     * 
     *
     * @return the data length as {@link int}.
     */
    public int getDataLength();

    /**
     * Returns the source subnet identifier of this
     * {@link SbusMessage} as {@link int}.<br>
     * The identifier is a 1-byte non negative
     * integer value valid in the range of 0-255.
     * 
     *
     * @return the unit identifier as {@link int}.
     */
    public int getSourceSubnetID();
    
    /**
     * Returns the source unit identifier of this
     * {@link SbusMessage} as {@link int}.<br>
     * The identifier is a 1-byte non negative
     * integer value valid in the range of 0-255.
     * 
     *
     * @return the unit identifier as {@link int}.
     */
    public int getSourceUnitID();

    /**
     * Returns the source device type of this
     * {@link SbusMessage} as {@link int}.<br>
     * The identifier is a 2-byte non negative
     * integer value valid in the range of 0-255.
     * 
     *
     * @return the unit identifier as {@link int}.
     */
    public int getSourceDeviceType();

    /**
     * Returns the subnet identifier of this
     * {@link SbusMessage} as {@link int}.<br>
     * The identifier is a 1-byte non negative
     * integer value valid in the range of 0-255.
     * 
     *
     * @return the unit identifier as {@link int}.
     */
    public int getSubnetID();
    
    /**
     * Returns the unit identifier of this
     * {@link SbusMessage} as {@link int}.<br>
     * The identifier is a 1-byte non negative
     * integer value valid in the range of 0-255.
     * 
     *
     * @return the unit identifier as {@link int}.
     */
    public int getUnitID();

    /**
     * Returns the function code of this
     * {@link SbusMessage} as {@link int}.<br>
     * The function code is a 1-byte non negative
     * integer value valid in the range of 0-127.<br>
     * Function codes are ordered in conformance
     * classes their values are specified in
     * {@link ro.ciprianpascu.sbus.Sbus}.
     * 
     *
     * @return the function code as {@link int}.
     *
     * @see ro.ciprianpascu.sbus.Sbus
     */
    public int getFunctionCode();

    /**
     * Returns the <i>raw</i> message as {@link String}
     * containing a hexadecimal series of bytes.
     * <br>
     * This method is specially for debugging purposes,
     * allowing to log the communication in a manner used
     * in the specification document.
     * 
     *
     * @return the <i>raw</i> message as {@link String}
     *         containing a hexadecimal series of bytes.
     *
     */
    public String getHexMessage();

}// interface SbusMessage
