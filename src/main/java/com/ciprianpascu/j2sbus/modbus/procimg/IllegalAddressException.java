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
 * Class implementing an <code>IllegalAddressException</code>. This exception is
 * thrown when a non-existant spot in the process image was addressed.
 *
 * Note that this is a runtime exception, as it is similar to the
 * <code>IndexOutOfBoundsException</code>
 *
 * @author Dieter Wimberger
 * @author Steve O'Hara (4NG)
 * @version 2.0 (March 2016)
 */
public class IllegalAddressException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new <code>IllegalAddressException</code>.
     */
    public IllegalAddressException() {
    }

    /**
     * Constructs a new <code>IllegalAddressException</code> with the given message.
     *
     * @param message a message as <code>String</code>.
     */
    public IllegalAddressException(String message) {
        super(message);
    }
}
