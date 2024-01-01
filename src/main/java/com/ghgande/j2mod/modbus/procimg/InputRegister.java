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
package com.ghgande.j2mod.modbus.procimg;

/**
 * Interface defining an input register.
 *
 * This register is read only from the slave side.
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public interface InputRegister {

    /**
     * Returns the value of this <code>InputRegister</code>. The value is stored as
     * <code>int</code> but should be treated like a 16-bit word.
     *
     * @return the value as <code>int</code>.
     */
    int getValue();

    /**
     * Returns the content of this <code>Register</code> as unsigned 16-bit value
     * (unsigned short).
     *
     * @return the content as unsigned short (<code>int</code>).
     */
    int toUnsignedShort();

    /**
     * Returns the content of this <code>Register</code> as signed 16-bit value
     * (short).
     *
     * @return the content as <code>short</code>.
     */
    short toShort();

    /**
     * Returns the content of this <code>Register</code> as bytes.
     *
     * @return a <code>byte[]</code> with length 2.
     */
    byte[] toBytes();

}