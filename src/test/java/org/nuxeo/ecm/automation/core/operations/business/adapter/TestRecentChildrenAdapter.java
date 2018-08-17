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

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.CoreFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.ecm.automation.core.operations.business.adapter.RecentChildren;
import org.nuxeo.ecm.automation.core.scripting.DateWrapper;

@RunWith(FeaturesRunner.class)
@Features(CoreFeature.class)
@Deploy({"org.nuxeo.ecm.automation.core.operations.business.adapter.nuxeo-custom-business-objects"})
public class TestRecentChildrenAdapter {
  @Inject
  CoreSession session;

  @Test
  public void shouldCallTheAdapter() {
    String doctype = "Folder";
    String testTitle = "My Adapter Title";

    System.out.println("*** " + new DateWrapper().days(-30).toString());
    DocumentModel doc = session.createDocumentModel("/", "test-adapter", doctype);
    RecentChildren adapter = doc.getAdapter(RecentChildren.class);
    adapter.create();
    // session.save() is only needed in the context of unit tests
    session.save();

    Assert.assertNotNull("The adapter can't be used on the " + doctype + " document type", adapter);
    Assert.assertEquals("Document title does not match when using the adapter", testTitle, adapter.getTitle());
  }
}
