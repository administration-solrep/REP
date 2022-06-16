/*
 * (C) Copyright 2016 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Gabriel Barata <gbarata@nuxeo.com>
 */
package org.nuxeo.ecm.platform.search.core;

import java.io.IOException;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * @since 8.3
 */
public interface SavedSearchService {

    SavedSearch createSavedSearch(CoreSession session, String title, String queryParams,
            Map<String, String> namedParams, String query, String queryLanguage, String pageProviderName,
            Long pageSize, Long currentPageIndex, Long maxResults, String sortBy, String sortOrder,
            String contentViewData) throws InvalidSearchParameterException, IOException;

    SavedSearch getSavedSearch(CoreSession session, String id);

    SavedSearch saveSavedSearch(CoreSession session, SavedSearch search) throws InvalidSearchParameterException,
            IOException;

    void deleteSavedSearch(CoreSession session, SavedSearch search);
}
