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

/***
 * Copied with style from
 * Lea, Doug: "Concurrent Programming in Java: Design Principles and Patterns",
 * Second Edition, Addison-Wesley, ISBN 0-201-31009-0, November 1999
 ***/
package ro.ciprianpascu.sbus.util;

/**
 * Class defining a linked node element.
 *
 * This class represents a node in a linked list data structure.
 * Each node contains an object as its data and a reference to the next node in the list.
 *
 * @author Doug Lea, Ciprian Pascu
 * @version %I% (%G%)
 */
public class LinkedNode {

    /**
     * The data stored in the node.
     */
    protected Object m_Node;

    /**
     * The next node in the list.
     */
    protected LinkedNode m_NextNode = null;

    /**
     * Constructs a new LinkedNode object with the given data.
     *
     * @param node The data to be stored in the node.
     */
    public LinkedNode(Object node) {
        m_Node = node;
    }

    /**
     * Constructs a new LinkedNode object with the given data and reference to the next node.
     *
     * @param node The data to be stored in the node.
     * @param linkednode The next node in the list.
     */
    public LinkedNode(Object node, LinkedNode linkednode) {
        m_Node = node;
        m_NextNode = linkednode;
    }

}