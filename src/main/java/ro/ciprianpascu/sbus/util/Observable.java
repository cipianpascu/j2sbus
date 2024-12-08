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

package ro.ciprianpascu.sbus.util;

import java.util.Vector;

/**
 * A clean-room implementation of the Observable pattern.
 * This class provides a thread-safe implementation of the Observer pattern,
 * allowing objects to register as observers and receive notifications when
 * the observable object changes state. All operations on the observer list
 * are synchronized to ensure thread safety.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class Observable {

    /** Vector storing all registered observers */
    private Vector m_Observers;

    /**
     * Constructs a new Observable instance.
     * Initializes with a default capacity of 10 observers.
     */
    public Observable() {
        m_Observers = new Vector(10);
    }

    /**
     * Returns the current number of registered observers.
     * This method is synchronized to ensure thread-safe access
     * to the observers list.
     *
     * @return the number of registered observers
     */
    public int getObserverCount() {
        synchronized (m_Observers) {
            return m_Observers.size();
        }
    }

    /**
     * Adds an observer instance if it is not already in the
     * set of observers for this Observable.
     *
     * @param o an observer instance to be added
     */
    public void addObserver(Observer o) {
        synchronized (m_Observers) {
            if (!m_Observers.contains(o)) {
                m_Observers.addElement(o);
            }
        }
    }

    /**
     * Removes an observer instance from the set of observers
     * of this Observable.
     *
     * @param o an observer instance to be removed
     */
    public void removeObserver(Observer o) {
        synchronized (m_Observers) {
            m_Observers.removeElement(o);
        }
    }

    /**
     * Removes all observer instances from the set of observers
     * of this Observable.
     */
    public void removeObservers() {
        synchronized (m_Observers) {
            m_Observers.removeAllElements();
        }
    }

    /**
     * Notifies all observer instances in the set of observers
     * of this Observable. Each observer's update method is called
     * with this Observable instance and the provided argument.
     *
     * @param arg an arbitrary argument to be passed to all observers
     */
    public void notifyObservers(Object arg) {
        synchronized (m_Observers) {
            for (int i = 0; i < m_Observers.size(); i++) {
                ((Observer) m_Observers.elementAt(i)).update(this, arg);
            }
        }
    }
}
