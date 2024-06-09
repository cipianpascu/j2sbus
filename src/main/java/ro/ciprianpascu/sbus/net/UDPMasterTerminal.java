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

import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class implementing a {@link UDPMasterTerminal}.
 *
 * @author Dieter Wimberger
 * @author Ciprian Pascu

 * @version %I% (%G%)
 */
class UDPMasterTerminal extends UDPSlaveTerminal implements UDPTerminal {
    private static final Logger logger = LoggerFactory.getLogger(UDPMasterTerminal.class);



    public UDPMasterTerminal() {
    	super(false);
    }// constructor

    protected UDPMasterTerminal(InetAddress addr) {
    	super(addr,false);
    }// constructor



    @Override
    public void sendMessage(byte[] msg) throws Exception {
    	
        super.sendMessage(msg);
    }// sendPackage

    @Override
    public byte[] receiveMessage() throws Exception {

    	return super.receiveMessage();
    }// receiveMessage
    
    @Override
    public void setTimeout(int timeout) {
    	super.setTimeout(timeout);
    }


    
}// class UDPMasterTerminal
