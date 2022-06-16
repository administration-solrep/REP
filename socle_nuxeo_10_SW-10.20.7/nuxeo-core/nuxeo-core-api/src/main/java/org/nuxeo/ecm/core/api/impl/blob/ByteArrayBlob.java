/*
 * (C) Copyright 2006-2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Bogdan Stefanescu
 *     Florent Guillaume
 */
package org.nuxeo.ecm.core.api.impl.blob;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Blob based on a byte array.
 */
public class ByteArrayBlob extends AbstractBlob implements Serializable {

    private static final long serialVersionUID = 1L;

    protected final byte[] bytes;

    public ByteArrayBlob(byte[] bytes) {
        this(bytes, null, null);
    }

    public ByteArrayBlob(byte[] bytes, String mimeType) {
        this(bytes, mimeType, null);
    }

    public ByteArrayBlob(byte[] bytes, String mimeType, String encoding) {
        if (bytes == null) {
            throw new NullPointerException("null bytes");
        }
        this.bytes = bytes;
        this.mimeType = mimeType;
        this.encoding = encoding;
    }

    @Override
    public long getLength() {
        return bytes.length;
    }

    @Override
    public InputStream getStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public byte[] getByteArray() {
        return bytes;
    }

    @Override
    public String getString() throws IOException {
        return new String(bytes, getEncoding() == null ? UTF_8 : getEncoding());
    }

}
