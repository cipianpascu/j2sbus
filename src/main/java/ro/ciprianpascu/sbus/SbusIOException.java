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

package ro.ciprianpascu.sbus;

/**
 * Class that implements a {@link SbusIOException}.
 * Instances of this exception are thrown when
 * errors in the I/O occur.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class SbusIOException extends SbusException {

    private boolean m_EOF = false;

    /**
     * Constructs a new {@link SbusIOException}
     * instance.
     */
    public SbusIOException() {
    }// constructor

    /**
     * Constructs a new {@link SbusIOException}
     * instance with the given message.
* 
     *
     * @param message the message describing this
     *            {@link SbusIOException}.
     */
    public SbusIOException(String message) {
        super(message);
    }// constructor(String)

    /**
     * Constructs a new {@link SbusIOException}
     * instance.
     *
     * @param b true if caused by end of stream, false otherwise.
     */
    public SbusIOException(boolean b) {
        m_EOF = b;
    }// constructor

    /**
     * Constructs a new {@link SbusIOException}
     * instance with the given message.
* 
     *
     * @param message the message describing this
     *            {@link SbusIOException}.
     * @param b true if caused by end of stream, false otherwise.
     */
    public SbusIOException(String message, boolean b) {
        super(message);
        m_EOF = b;
    }// constructor(String)

    /**
     * Tests if this {@link SbusIOException}
     * is caused by an end of the stream.
* 
     *
     * @return true if stream ended, false otherwise.
     */
    public boolean isEOF() {
        return m_EOF;
    }// isEOF

    /**
     * Sets the flag that determines whether this
     * {@link SbusIOException} was caused by
     * an end of the stream.
* 
     *
     * @param b true if stream ended, false otherwise.
     */
    public void setEOF(boolean b) {
        m_EOF = b;
    }// setEOF

}// SbusIOException
