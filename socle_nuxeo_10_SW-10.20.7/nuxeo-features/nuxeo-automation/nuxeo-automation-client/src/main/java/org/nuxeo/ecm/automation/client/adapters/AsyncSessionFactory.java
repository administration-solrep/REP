/*
 * (C) Copyright 2018 Nuxeo (http://nuxeo.com/) and others.
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
 *     Nelson Silva <nsilva@nuxeo.com>
 */

package org.nuxeo.ecm.automation.client.adapters;

import org.nuxeo.ecm.automation.client.AdapterFactory;
import org.nuxeo.ecm.automation.client.Session;
import org.nuxeo.ecm.automation.client.jaxrs.spi.DefaultSession;

/**
 * @since 10.3
 */
public class AsyncSessionFactory implements AdapterFactory<AsyncSession> {

    @Override
    public AsyncSession getAdapter(Session session, Class<AsyncSession> clazz) {
        if (session instanceof DefaultSession) {
            return new AsyncSession((DefaultSession) session);
        }
        throw new IllegalArgumentException("Cannot instantiate async session adapter");
    }

}
