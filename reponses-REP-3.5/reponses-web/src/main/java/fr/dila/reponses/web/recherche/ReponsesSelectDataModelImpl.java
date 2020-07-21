/*
 * (C) Copyright 2007 Nuxeo SAS (http://nuxeo.com/) and contributors.
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
 *     Nuxeo - initial API and implementation
 *
 * $Id: SelectDataModelImpl.java 28493 2008-01-04 19:51:30Z sfermigier $
 */

package fr.dila.reponses.web.recherche;

import java.util.List;

import org.nuxeo.ecm.platform.ui.web.model.SelectDataModel;
import org.nuxeo.ecm.platform.ui.web.model.impl.SelectDataModelImpl;

/**
 * Mon select data model pour corriger un problème sur la sélection des données.
 * @author jgomez
 */
public class ReponsesSelectDataModelImpl extends SelectDataModelImpl implements SelectDataModel {
    
    private Boolean selected;


    @SuppressWarnings("rawtypes")
    public ReponsesSelectDataModelImpl(String name, List data, List selectedData) {
        super(name, data,selectedData);
    }


    public void setSelected(Boolean selected) {
        this.selected = selected;
        this.selectedData = this.data;
        generateSelectRows();
    }


    public Boolean getSelected() {
        return selected;
    } 
    
    
}
