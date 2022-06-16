/*
 * (C) Copyright 2011 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Anahide Tchertchian
 */
package org.nuxeo.ecm.automation.jsf.operations;

import org.nuxeo.ecm.automation.OperationContext;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Context;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.automation.jsf.OperationHelper;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Saves a document (equivalent to clicking on the 'save' button on a document edition form).
 *
 * @since 5.4.2
 */
@Operation(id = SaveDocumentInUI.ID, category = Constants.CAT_UI, requires = Constants.SEAM_CONTEXT, label = "Save Document in UI", description = "Saves a document in UI, "
        + "as if user was hitting the 'Save' button on a the document edition form. "
        + "It assumes that the contextual 'currentDocument' document from the Seam context has been updated "
        + "to hold the new properties. It will navigate to the edited document context, "
        + "set its view as outcome, and return it.")
public class SaveDocumentInUI {

    public static final String ID = "Seam.SaveDocumentInUI";

    @Context
    protected OperationContext ctx;

    @OperationMethod
    public DocumentModel run() {
        ctx.put(SeamOperation.OUTCOME, OperationHelper.getDocumentActions().updateCurrentDocument());
        return OperationHelper.getNavigationContext().getCurrentDocument();
    }

}
