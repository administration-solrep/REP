/*
 * (C) Copyright 2006-2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * Contributors:
 *     Thierry Delprat
 * *
 */

package org.nuxeo.ecm.platform.computedgroups;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XNodeList;
import org.nuxeo.common.xmap.annotation.XObject;

/**
*
* @author Thierry Delprat
*
*/

@XObject("groupComputerChain")
public class GroupComputerChainDescriptor implements Serializable {

    private static final long serialVersionUID = 1L;

    @XNodeList(value = "computers/computer", type = ArrayList.class, componentType = String.class)
    private List<String> computerNames;

    @XNode("@append")
    private boolean append = false;

    public boolean isAppend() {
        return append;
    }

    public List<String> getComputerNames() {
        if (computerNames != null) {
            return computerNames;
        } else {
            return new ArrayList<String>();
        }
    }

}
