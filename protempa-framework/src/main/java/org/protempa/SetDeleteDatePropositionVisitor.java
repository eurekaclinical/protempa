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
import org.protempa.proposition.Constant;
import org.protempa.proposition.Context;
import org.protempa.proposition.CopyPropositionVisitor;
import org.protempa.proposition.Event;
import org.protempa.proposition.PrimitiveParameter;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.visitor.AbstractPropositionVisitor;

/**
 * For all types of propositions, if the delete date is <code>null</code>, it
 * creates an exact copy of the proposition except with the delete date set to
 * the date when this visitor instance was created. To use this class, pass an
 * instance of this class into 
 * {@link Proposition#accept(org.protempa.proposition.visitor.PropositionVisitor) }
 * and call {@link #getDeleted() }. If the delete date of the original
 * proposition is not <code>null</code>, the original proposition is not copied;
 * the same instance is returned from {@link #getDeleted() }.
 *
 * @author Andrew Post
 */
final class SetDeleteDatePropositionVisitor extends AbstractPropositionVisitor {

    private Proposition result;
    private final CopyPropositionVisitor copyPropositionVisitor;

    SetDeleteDatePropositionVisitor() {
        this.copyPropositionVisitor = new CopyPropositionVisitor();
        this.copyPropositionVisitor.setDeleteDate(new Date());
    }

    @Override
    public void visit(Context context) {
        if (context.getDeleteDate() == null) {
            context.accept(this.copyPropositionVisitor);
            this.result = this.copyPropositionVisitor.getCopy();
        } else {
            this.result = context;
        }
    }

    @Override
    public void visit(AbstractParameter abstractParameter) {
        if (abstractParameter.getDeleteDate() == null) {
            abstractParameter.accept(this.copyPropositionVisitor);
            this.result = this.copyPropositionVisitor.getCopy();
        } else {
            this.result = abstractParameter;
        }
    }

    @Override
    public void visit(Constant constant) {
        if (constant.getDeleteDate() == null) {
            constant.accept(this.copyPropositionVisitor);
            this.result = this.copyPropositionVisitor.getCopy();
        } else {
            this.result = constant;
        }
    }

    @Override
    public void visit(PrimitiveParameter primitiveParameter) {
        if (primitiveParameter.getDeleteDate() == null) {
            primitiveParameter.accept(this.copyPropositionVisitor);
            this.result = this.copyPropositionVisitor.getCopy();
        } else {
            this.result = primitiveParameter;
        }
    }

    @Override
    public void visit(Event event) {
        this.result = event;
    }

    Proposition getDeleted() {
        return this.result;
    }

}
