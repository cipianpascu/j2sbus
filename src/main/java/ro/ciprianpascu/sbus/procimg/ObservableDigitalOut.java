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

import ro.ciprianpascu.sbus.util.Observable;

/**
 * Class implementing an observable digital output.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
public class ObservableDigitalOut extends Observable implements DigitalOut {

    /**
     * Constructs a new ObservableDigitalOut instance.
     * The digital output is initially set to false (off).
     */
    public ObservableDigitalOut() {
        m_Set = false;
    }

    /**
     * Constructs a new ObservableDigitalOut instance with the given initial state.
     *
     * @param state the initial state of this digital output
     */
    public ObservableDigitalOut(boolean state) {
        m_Set = state;
    }

    /**
     * A boolean holding the state of this digital out.
     */
    protected boolean m_Set;

    @Override
    public boolean isSet() {
        return m_Set;
    }// isSet

    @Override
    public void set(boolean b) {
        m_Set = b;
        notifyObservers("value");
    }// set

}// class ObservableDigitalIn
