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
 *     Funsho David
 */

package org.nuxeo.ecm.core.bulk;

import org.nuxeo.common.xmap.annotation.XNode;
import org.nuxeo.common.xmap.annotation.XObject;
import org.nuxeo.ecm.core.api.NuxeoException;
import org.nuxeo.runtime.model.Descriptor;

/**
 * @since 10.2
 */
@XObject("action")
public class BulkActionDescriptor implements Descriptor {

    public static final Integer DEFAULT_BUCKET_SIZE = 100;

    public static final Integer DEFAULT_BATCH_SIZE = 25;

    // @since 11.1
    @XNode("@enabled")
    protected boolean isEnabled = true;

    @XNode("@name")
    public String name;

    @XNode("@bucketSize")
    public Integer bucketSize = DEFAULT_BUCKET_SIZE;

    @XNode("@batchSize")
    public Integer batchSize = DEFAULT_BATCH_SIZE;

    @XNode("@httpEnabled")
    public Boolean httpEnabled = Boolean.FALSE;

    @XNode("@sequentialCommands")
    public Boolean sequentialCommands = Boolean.FALSE;

    @XNode("@validationClass")
    public Class<? extends BulkActionValidation> validationClass;

    @Override
    public String getId() {
        return name;
    }

    public Integer getBucketSize() {
        return bucketSize;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    /**
     * @since 10.10
     */
    public BulkActionValidation newValidationInstance() {
        try {
            return validationClass.getDeclaredConstructor().newInstance();
        } catch (ReflectiveOperationException e) {
            throw new NuxeoException("Cannot create validation class of type " + validationClass.getName(), e);
        }
    }

    // @since 11.1
    public boolean isEnabled() {
        return isEnabled;
    }

    // @since 11.1
    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

}
