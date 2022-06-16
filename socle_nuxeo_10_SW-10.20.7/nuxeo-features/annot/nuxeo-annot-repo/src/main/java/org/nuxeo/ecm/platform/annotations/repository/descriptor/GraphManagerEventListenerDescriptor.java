/*
 * (C) Copyright 2006-2008 Nuxeo SA (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Alexandre Russel
 *
 * $Id$
 */

package org.nuxeo.ecm.platform.annotations.repository.descriptor;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.platform.annotations.repository.service.GraphManagerEventListener;

/**
 * @author <a href="mailto:arussel@nuxeo.com">Alexandre Russel</a>
 */
@XObject("listener")
public class GraphManagerEventListenerDescriptor {
    @XNode("@class")
    private Class<? extends GraphManagerEventListener> klass;

    public Class<? extends GraphManagerEventListener> getKlass() {
        return klass;
    }

    public void setKlass(Class<? extends GraphManagerEventListener> klass) {
        this.klass = klass;
    }

}
