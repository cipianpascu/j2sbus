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
 * Class implementing an {@link IllegalAddressException}.
 * This exception is thrown when a non-existant spot in
 * the process image was addressed.
* 
 * Note that this is a runtime exception, as it is similar
 * to the {@link IndexOutOfBoundsException}
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class IllegalAddressException extends RuntimeException {

    /**
     * Constructs a new {@link IllegalAddressException}.
     */
    public IllegalAddressException() {
    }// constructor()

    /**
     * Constructs a new {@link IllegalAddressException}
     * with the given message.
     *
     * @param message a message as {@link String}.
     */
    public IllegalAddressException(String message) {
        super(message);
    }// constructor(String)

}// class IllegalAddressException
