/*
 *
 * Copyright (c) 2018, 4ng and/or its affiliates. All rights reserved.
 * 4ENERGY PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 */
package com.ciprianpascu.j2sbus.modbus.cmd;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ciprianpascu.j2sbus.modbus.net.SerialConnection;

/**
 *
 */
public class GetCommsPorts {

    private static final Logger logger = LoggerFactory.getLogger(GetCommsPorts.class);

    public static void main(String[] args) {

        for (String commPort : new SerialConnection().getCommPorts()) {
            logger.info(commPort);
        }
    }
}
