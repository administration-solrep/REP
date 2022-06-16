/*
 * (C) Copyright 2012 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     matic
 */
package org.nuxeo.ecm.core.test;

import java.util.HashMap;
import java.util.Map;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;

/**
 * @author matic
 */
public class CoreScope implements Scope {

    protected final ThreadLocal<Map<Key<?>, Object>> values = new ThreadLocal<Map<Key<?>, Object>>() {
        protected java.util.Map<Key<?>, Object> initialValue() {
            return new HashMap<Key<?>, Object>();
        };
    };

    public final static CoreScope INSTANCE = new CoreScope();

    protected CoreScope() {

    }

    public void enter() {
        values.get();
    }

    public void exit() {
        values.remove();
    }

    @Override
    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {

            @Override
            public T get() {
                Map<Key<?>, Object> scopedMap = getScopedObjectMap(key);
                T current = (T) scopedMap.get(key);
                if (current == null && !scopedMap.containsKey(key)) {
                    current = unscoped.get();
                    scopedMap.put(key, current);
                }
                return current;
            }

        };
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key + " outside of a scoping block");
        }
        return scopedObjects;
    }
}
