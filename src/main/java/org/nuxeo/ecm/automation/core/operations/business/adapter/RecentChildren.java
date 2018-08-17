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
 *     vdutat
 *
 */
package org.nuxeo.ecm.automation.core.operations.business.adapter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.core.query.sql.model.Operator;

/**
 *
 */
public class RecentChildren extends BusinessAdapter {

    protected String titleXpath = "dc:title";
    protected String descriptionXpath = "dc:description";

    private static final Log LOG = LogFactory.getLog(RecentChildren.class);

    /**
     * Default constructor is needed for jackson mapping
     */
    public RecentChildren() {
        super();
    }

    public RecentChildren(DocumentModel doc) {
        super(doc);
    }

    // Basic methods
    //
    // Note that we voluntarily expose only a subset of the DocumentModel API in this adapter.
    // You may wish to complete it without exposing everything!
    // For instance to avoid letting people change the document state using your adapter,
    // because this would be handled through workflows / buttons / events in your application.
    //
    public void create() {
        CoreSession session = doc.getCoreSession();
        session.createDocument(doc);
    }

    public void save() {
        save(doc.getCoreSession());
    }

    public DocumentRef getParentRef() {
        return doc.getParentRef();
    }

    // Technical properties retrieval

    public String getName() {
        return doc.getName();
    }

    public String getPath() {
        return doc.getPathAsString();
    }

    public String getState() {
        return doc.getCurrentLifeCycleState();
    }

    // Metadata get / set
    public String getTitle() {
        return doc.getTitle();
    }

    public void setTitle(String value) {
        doc.setPropertyValue(titleXpath, value);
    }

    public String getDescription() {
        return (String) doc.getPropertyValue(descriptionXpath);
    }

    public void setDescription(String value) {
        doc.setPropertyValue(descriptionXpath, value);
    }

    @JsonIgnore // FIXME does not work, getting org.codehaus.jackson.map.JsonMappingException: Direct self-reference leading to cycle (through reference chain: org.nuxeo.ecm.automation.core.operations.business.adapter.RecentChildren["children"]->org.nuxeo.ecm.core.api.impl.DocumentModelListImpl[0]->org.nuxeo.ecm.core.api.impl.DocumentModelImpl["coreSession"]->org.nuxeo.ecm.core.api.local.LocalSession["session"]->org.nuxeo.ecm.core.storage.sql.coremodel.SQLSession["rootDocument"]->org.nuxeo.ecm.core.storage.sql.coremodel.SQLDocumentLive["workingCopy"])
    public DocumentModelList getChildren() {
        StringBuilder sb = new StringBuilder("SELECT * FROM Document WHERE ");
        sb.append(NXQL.ECM_PARENTID).append(Operator.EQ.toString()).append(NXQL.escapeString(getId()))
        .append(" " + Operator.AND.toString() + " ").append(NXQL.ECM_LIFECYCLESTATE).append(Operator.NOTEQ.toString()).append(NXQL.escapeString(LifeCycleConstants.DELETED_STATE))
        .append(" " + Operator.AND.toString() + " ").append(NXQL.ECM_ISCHECKEDIN).append(Operator.EQ.toString() + "0");
        DocumentModelList docs = doc.getCoreSession().query(sb.toString());
        return docs;
    }

}
