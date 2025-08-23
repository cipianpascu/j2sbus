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

package ro.ciprianpascu.sbus.net;

import ro.ciprianpascu.sbus.msg.SbusResponse;

/**
 * Interface for listening to unsolicited SBus messages.
 * This listener will be notified when messages arrive that are not
 * part of a synchronous request/response transaction.
 *
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public interface SbusMessageListener {
    
    /**
     * Called when an unsolicited message is received.
     * This method will be called from a background thread, so implementations
     * should be thread-safe and avoid blocking operations.
     *
     * @param response the received SBus response message
     */
    void onMessageReceived(SbusResponse response);
    
    /**
     * Called when an error occurs while processing a message.
     * This allows the listener to handle parsing errors or other issues.
     *
     * @param error the exception that occurred
     * @param rawMessage the raw message bytes that caused the error (may be null)
     */
    default void onError(Exception error, byte[] rawMessage) {
        // Default implementation does nothing
        // Implementations can override to handle errors
    }
}
