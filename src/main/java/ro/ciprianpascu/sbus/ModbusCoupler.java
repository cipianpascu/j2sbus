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

import ro.ciprianpascu.sbus.procimg.DefaultProcessImageFactory;
import ro.ciprianpascu.sbus.procimg.ProcessImage;
import ro.ciprianpascu.sbus.procimg.ProcessImageFactory;

/**
 * Class implemented following a Singleton pattern,
 * to couple the slave side with a master side or
 * with a device.
* 
 * At the moment it only provides a reference to the
 * OO model of the process image.
 *
 * @author Dieter Wimberger
 * @version %I% (%G%)
 */
public class ModbusCoupler {

    // class attributes
    private static ModbusCoupler c_Self; // Singleton reference

    // instance attributes
    private ProcessImage m_ProcessImage;
    private int m_SubnetID = Modbus.DEFAULT_SUBNET_ID;
    private int m_UnitID = Modbus.DEFAULT_UNIT_ID;
    private boolean m_Master = true;
    private ProcessImageFactory m_PIFactory;

    static {
        c_Self = new ModbusCoupler();
    } // initializer

    private ModbusCoupler() {
        m_PIFactory = new DefaultProcessImageFactory();
    }// constructor

    /**
     * Private constructor to prevent multiple
     * instantiation.
     * 
     *
     * @param procimg a {@link ProcessImage}.
     */
    private ModbusCoupler(ProcessImage procimg) {
        setProcessImage(procimg);
        c_Self = this;
    }// contructor(ProcessImage)

    /**
     * Returns the actual {@link ProcessImageFactory} instance.
     *
     * @return a {@link ProcessImageFactory} instance.
     */
    public ProcessImageFactory getProcessImageFactory() {
        return m_PIFactory;
    }// getProcessImageFactory

    /**
     * Sets the {@link ProcessImageFactory} instance.
     *
     * @param factory the instance to be used for creating process
     *            image instances.
     */
    public void setProcessImageFactory(ProcessImageFactory factory) {
        m_PIFactory = factory;
    }// setProcessImageFactory

    /**
     * Returns a reference to the {@link ProcessImage}
     * of this {@link ModbusCoupler}.
     * 
     *
     * @return the {@link ProcessImage}.
     */
    public synchronized ProcessImage getProcessImage() {
        return m_ProcessImage;
    }// getProcessImage

    /**
     * Sets the reference to the {@link ProcessImage}
     * of this {@link ModbusCoupler}.
     * 
     *
     * @param procimg the {@link ProcessImage} to be set.
     */
    public synchronized void setProcessImage(ProcessImage procimg) {
        m_ProcessImage = procimg;
    }// setProcessImage

    /**
     * Returns the identifier of this unit.
     * This identifier is required to be set
     * for serial protocol slave implementations.
     *
     * @return the unit identifier as {@link int}.
     */
    public int getUnitID() {
        return m_UnitID;
    }// getUnitID

    /**
     * Sets the identifier of this unit, which is needed
     * to be determined in a serial network.
     *
     * @param id the new unit identifier as {@link int}.
     */
    public void setUnitID(int id) {
        m_UnitID = id;
    }// setUnitID

    /**
     * Returns the identifier of this subnet.
     * This identifier is required to be set
     * for serial protocol slave implementations.
     *
     * @return the unit identifier as {@link int}.
     */
    public int getSubnetID() {
        return m_SubnetID;
    }// getUnitID

    /**
     * Sets the identifier of this subnet, which is needed
     * to be determined in a serial network.
     *
     * @param id the new unit identifier as {@link int}.
     */
    public void setSubnetID(int id) {
        m_SubnetID = id;
    }// setUnitID
    /**
     * Tests if this instance is a master device.
     *
     * @return true if master, false otherwise.
     */
    public boolean isMaster() {
        return m_Master;
    }// isMaster

    /**
     * Tests if this instance is not a master device.
     *
     * @return true if slave, false otherwise.
     */
    public boolean isSlave() {
        return !m_Master;
    }// isSlave

    /**
     * Sets this instance to be or not to be
     * a master device.
     *
     * @param master true if master device, false otherwise.
     */
    public void setMaster(boolean master) {
        m_Master = master;
    }// setMaster

    /**
     * Returns a reference to the singleton instance.
     * 
     *
     * @return the {@link ModbusCoupler} instance reference.
     */
    public static final ModbusCoupler getReference() {
        return c_Self;
    }// getReference

}// class ModbusCoupler