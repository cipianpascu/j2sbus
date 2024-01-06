/**
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

package ro.ciprianpascu.sbus.io;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Interface defining a transportable class.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public interface Transportable {

    /**
     * Returns the number of bytes that will
     * be written by {@link #writeTo(DataOutput)}.
     *
     * @return the number of bytes that will be written as {@link int}.
     */
    public int getOutputLength();

    /**
     * Writes this {@link Transportable} to the
     * given {@link DataOutput}.
     *
     * @param dout the {@link DataOutput} to write to.
     * @throws java.io.IOException if an I/O error occurs.
     */
    public void writeTo(DataOutput dout) throws IOException;

    /**
     * Reads this {@link Transportable} from the given
     * {@link DataInput}.
     *
     * @param din the {@link DataInput} to read from.
     * @throws java.io.IOException if an I/O error occurs or the data
     *             is invalid.
     */
    public void readFrom(DataInput din) throws IOException;

}// interface Transportable
