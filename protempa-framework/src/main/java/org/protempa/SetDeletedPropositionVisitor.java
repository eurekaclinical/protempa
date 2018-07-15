package org.protempa;

/*-
 * #%L
 * Protempa Framework
 * %%
 * Copyright (C) 2012 - 2018 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Date;
import org.protempa.proposition.AbstractParameter;
import org.protempa.proposition.Context;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;

/**
 * Creates a copy of any context or abstract parameter, setting the delete date
 * to the date when the visitor instance was created. This implementation
 * assumes that the only types of propositions that might need their delete
 * date set in this way are {@link Context} and {@link AbstractParameter}
 * propositions.
 * 
 * @author Andrew Post
 */
final class SetDeletedPropositionVisitor extends AbstractPropositionVisitor {
    private final Date now;
    private TemporalProposition result;

    SetDeletedPropositionVisitor() {
        this.now = new Date();
    }
    
    @Override
    public void visit(Context context) {
        Context copy = new Context(context.getId(), context.getUniqueId());
        setCommonFields(context, copy);
        this.result = copy;
    }

    @Override
    public void visit(AbstractParameter abstractParameter) {
        AbstractParameter copy = new AbstractParameter(abstractParameter.getId(), abstractParameter.getUniqueId());
        setCommonFields(abstractParameter, copy);
        copy.setValue(abstractParameter.getValue());
        this.result = copy;
    }
    
    TemporalProposition getDeletedCopy() {
        return this.result;
    }
    
    private void setCommonFields(TemporalProposition original, TemporalProposition copy) {
        copy.setCreateDate(original.getCreateDate());
        copy.setUpdateDate(original.getUpdateDate());
        copy.setDeleteDate(this.now);
        copy.setDownloadDate(original.getDownloadDate());
        copy.setInterval(original.getInterval());
        copy.setSourceSystem(original.getSourceSystem());
        for (String refName : original.getReferenceNames()) {
            copy.setReferences(refName, original.getReferences(refName));
        }
        for (String propName : original.getPropertyNames()) {
            copy.setProperty(propName, original.getProperty(propName));
        }
    }
    
}
