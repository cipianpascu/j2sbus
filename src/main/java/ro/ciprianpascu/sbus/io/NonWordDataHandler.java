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

package ro.ciprianpascu.sbus.io;

import java.io.DataInput;
import java.io.EOFException;
import java.io.IOException;

/**
 * Interface for handling non-word data in the SBus protocol.
 * This handler manages data that doesn't fit into the standard word (16-bit) format,
 * particularly for read/write multiple register operations. It provides methods
 * for reading, preparing, and committing data, along with conversion between
 * byte and word representations.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public interface NonWordDataHandler {

    /**
     * Returns the intermediate raw non-word data.
     *
     * @return the raw data as a byte array
     */
    public byte[] getData();

    /**
     * Reads the non-word raw data based on an arbitrary implemented structure.
     * This method handles reading data that doesn't conform to standard word boundaries.
     *
     * @param in the DataInput to read from
     * @param reference the starting offset for reading
     * @param count the number of bytes to read
     * @throws IOException if an I/O error occurs
     * @throws EOFException if the end of stream is reached before reading all data
     */
    public void readData(DataInput in, int reference, int count) throws IOException, EOFException;

    /**
     * Returns the word count of the data.
     * This should be the length of the byte array divided by two,
     * as each word consists of two bytes.
     *
     * @return the number of words the data consists of
     */
    public int getWordCount();

    /**
     * Commits the data if it has been read into an intermediate repository.
     * This method is called when a write operation is complete to finalize
     * the changes in the backing store.
     *
     * @return -1 if the commit was successful, or an appropriate SBus exception
     *         code if the commit failed
     */
    public int commitUpdate();

    /**
     * Prepares the raw data from a backing data store.
     * This method is called before a read operation to ensure the data
     * is ready for transmission.
     *
     * @param reference the starting offset for the data preparation
     * @param count the number of bytes to prepare
     */
    public void prepareData(int reference, int count);
}
