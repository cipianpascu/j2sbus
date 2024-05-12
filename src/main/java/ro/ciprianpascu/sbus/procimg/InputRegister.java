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
 * Interface defining an input register.
* 
 * This register is read only from the slave side.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public interface InputRegister {

    /**
     * Returns the value of this {@link InputRegister}.
     * The value is stored as {@link int} but should be
     * treated like a 16-bit word.
     *
     * @return the value as {@link int}.
     */
    public int getValue();

    /**
     * Returns the content of this {@link Register} as
     * unsigned 16-bit value (unsigned short).
     *
     * @return the content as unsigned short ({@link int}).
     */
    public int toUnsignedShort();

    /**
     * Returns the content of this {@link Register} as
     * signed 16-bit value (short).
     *
     * @return the content as {@link short}.
     */
    public short toShort();

    /**
     * Returns the content of this {@link Register}
     * as bytes.
     *
     * @return a {@link byte[]} with length 2.
     */
    public byte[] toBytes();

}// interface InputRegister
