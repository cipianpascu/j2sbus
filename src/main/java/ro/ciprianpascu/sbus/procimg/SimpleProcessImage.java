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

import java.util.Vector;

/**
 * Class implementing a simple process image for the SBus protocol.
 * This implementation provides basic functionality for handling digital inputs/outputs
 * and registers, making it suitable for unit tests and simple use cases.
 * The process image can be locked to prevent modifications during operation.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu
 * @version %I% (%G%)
 */
public class SimpleProcessImage implements ProcessImageImplementation {

    /** Vector storing all digital input points */
    protected Vector m_DigitalInputs;

    /** Vector storing all digital output points */
    protected Vector m_DigitalOutputs;

    /** Vector storing all input registers (read-only registers) */
    protected Vector m_InputRegisters;

    /** Vector storing all read-write registers */
    protected Vector m_Registers;

    /** Flag indicating if the process image is locked for modifications */
    protected boolean m_Locked = false;

    /**
     * Constructs a new SimpleProcessImage instance.
     * Initializes empty vectors for all types of I/O points and registers.
     */
    public SimpleProcessImage() {
        m_DigitalInputs = new Vector();
        m_DigitalOutputs = new Vector();
        m_InputRegisters = new Vector();
        m_Registers = new Vector();
    }

    /**
     * Checks if this process image is locked for modifications.
     *
     * @return true if locked, false if modifications are allowed
     */
    public boolean isLocked() {
        return m_Locked;
    }

    /**
     * Sets the locked state of this process image.
     * When locked, no modifications to the I/O points or registers are allowed.
     *
     * @param locked true to lock the process image, false to allow modifications
     */
    public void setLocked(boolean locked) {
        m_Locked = locked;
    }

    @Override
    public void addDigitalIn(DigitalIn di) {
        if (!isLocked()) {
            m_DigitalInputs.addElement(di);
        }
    }

    @Override
    public void removeDigitalIn(DigitalIn di) {
        if (!isLocked()) {
            m_DigitalInputs.removeElement(di);
        }
    }

    @Override
    public void setDigitalIn(int ref, DigitalIn di) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                m_DigitalInputs.setElementAt(di, ref);
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    @Override
    public DigitalIn getDigitalIn(int ref) throws IllegalAddressException {
        try {
            return (DigitalIn) m_DigitalInputs.elementAt(ref);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    @Override
    public int getDigitalInCount() {
        return m_DigitalInputs.size();
    }

    @Override
    public DigitalIn[] getDigitalInRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_DigitalInputs.size()) {
            throw new IllegalAddressException();
        } else {
            DigitalIn[] dins = new DigitalIn[count];
            for (int i = 0; i < dins.length; i++) {
                dins[i] = getDigitalIn(ref + i);
            }
            return dins;
        }
    }

    @Override
    public void addDigitalOut(DigitalOut _do) {
        if (!isLocked()) {
            m_DigitalOutputs.addElement(_do);
        }
    }

    @Override
    public void removeDigitalOut(DigitalOut _do) {
        if (!isLocked()) {
            m_DigitalOutputs.removeElement(_do);
        }
    }

    @Override
    public void setDigitalOut(int ref, DigitalOut _do) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                m_DigitalOutputs.setElementAt(_do, ref);
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    @Override
    public DigitalOut getDigitalOut(int ref) throws IllegalAddressException {
        try {
            return (DigitalOut) m_DigitalOutputs.elementAt(ref);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    @Override
    public int getDigitalOutCount() {
        return m_DigitalOutputs.size();
    }

    @Override
    public DigitalOut[] getDigitalOutRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_DigitalOutputs.size()) {
            throw new IllegalAddressException();
        } else {
            DigitalOut[] douts = new DigitalOut[count];
            for (int i = 0; i < douts.length; i++) {
                douts[i] = getDigitalOut(ref + i);
            }
            return douts;
        }
    }

    @Override
    public void addInputRegister(InputRegister reg) {
        if (!isLocked()) {
            m_InputRegisters.addElement(reg);
        }
    }

    @Override
    public void removeInputRegister(InputRegister reg) {
        if (!isLocked()) {
            m_InputRegisters.removeElement(reg);
        }
    }

    @Override
    public void setInputRegister(int ref, InputRegister reg) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                m_InputRegisters.setElementAt(reg, ref);
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    @Override
    public InputRegister getInputRegister(int ref) throws IllegalAddressException {
        try {
            return (InputRegister) m_InputRegisters.elementAt(ref);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    @Override
    public int getInputRegisterCount() {
        return m_InputRegisters.size();
    }

    @Override
    public InputRegister[] getInputRegisterRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_InputRegisters.size()) {
            throw new IllegalAddressException();
        } else {
            InputRegister[] iregs = new InputRegister[count];
            for (int i = 0; i < iregs.length; i++) {
                iregs[i] = getInputRegister(ref + i);
            }
            return iregs;
        }
    }

    @Override
    public void addRegister(Register reg) {
        if (!isLocked()) {
            m_Registers.addElement(reg);
        }
    }

    @Override
    public void removeRegister(Register reg) {
        if (!isLocked()) {
            m_Registers.removeElement(reg);
        }
    }

    @Override
    public void setRegister(int ref, Register reg) throws IllegalAddressException {
        if (!isLocked()) {
            try {
                m_Registers.setElementAt(reg, ref);
            } catch (IndexOutOfBoundsException ex) {
                throw new IllegalAddressException();
            }
        }
    }

    @Override
    public Register getRegister(int ref) throws IllegalAddressException {
        try {
            return (Register) m_Registers.elementAt(ref);
        } catch (IndexOutOfBoundsException ex) {
            throw new IllegalAddressException();
        }
    }

    @Override
    public int getRegisterCount() {
        return m_Registers.size();
    }

    @Override
    public Register[] getRegisterRange(int ref, int count) {
        // ensure valid reference range
        if (ref < 0 || ref + count > m_Registers.size()) {
            throw new IllegalAddressException();
        } else {
            Register[] iregs = new Register[count];
            for (int i = 0; i < iregs.length; i++) {
                iregs[i] = getRegister(ref + i);
            }
            return iregs;
        }
    }
}
