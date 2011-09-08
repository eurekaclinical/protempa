package org.protempa.query.handler.table;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.protempa.KnowledgeSource;
import org.protempa.KnowledgeSourceReadException;
import org.protempa.ProtempaUtil;
import org.protempa.proposition.Proposition;
import org.protempa.proposition.PropositionUtil;
import org.protempa.proposition.TemporalProposition;
import org.protempa.proposition.UniqueId;
import org.protempa.proposition.comparator.AllPropositionIntervalComparator;
import org.protempa.proposition.value.Unit;

/**
 *
 * @author Andrew Post
 */
public final class DistanceBetweenColumnSpec extends AbstractTableColumnSpec {
    
    private static final Comparator<Proposition> comp =
            new AllPropositionIntervalComparator();
    
    private final Link[] links;
    private final String columnNamePrefixOverride;
    private final Unit units;
    
    public DistanceBetweenColumnSpec(Link[] links) {
        this(null, links);
    }
    
    public DistanceBetweenColumnSpec(String columnNamePrefixOverride, 
            Link[] links) {
        this(columnNamePrefixOverride, links, null);
    }
    
    public DistanceBetweenColumnSpec(String columnNamePrefixOverride, 
            Link[] links, Unit units) {
        if (links == null) {
            this.links = Util.EMPTY_LINK_ARRAY;
        } else {
            ProtempaUtil.checkArrayForNullElement(links, "links");
            this.links = links.clone();
        }
        this.columnNamePrefixOverride = columnNamePrefixOverride;
        this.units = units;
    }

    @Override
    public String[] columnNames(KnowledgeSource knowledgeSource) 
            throws KnowledgeSourceReadException {
        String headerString = this.columnNamePrefixOverride != null 
                ? this.columnNamePrefixOverride
                : generateLinksHeaderString(this.links) + "_lengthBetween";
        return new String[] { headerString };
    }

    @Override
    public String[] columnValues(String key, Proposition proposition, 
            Map<Proposition, List<Proposition>> forwardDerivations, 
            Map<Proposition, List<Proposition>> backwardDerivations, 
            Map<UniqueId, Proposition> references, 
            KnowledgeSource knowledgeSource) 
            throws KnowledgeSourceReadException {
        Logger logger = Util.logger();
        List<Proposition> propositions = traverseLinks(this.links,
                proposition, forwardDerivations, backwardDerivations, 
                references, knowledgeSource);
        Collections.sort(propositions, comp);
        int size = propositions.size();
        if (size > 2) {
            logger.log(Level.WARNING, 
                "There should be two or less propositions but there are {0}", 
                size);
        }
        if (size < 2) {
            return new String[]{null};
        }
        
        Iterator<Proposition> itr = propositions.iterator();
        TemporalProposition first = null;
        TemporalProposition second = null;
        
        Proposition tmpFirst = itr.next();
        if (tmpFirst instanceof TemporalProposition) {
            first = (TemporalProposition) tmpFirst;
        } else {
            logger.log(Level.WARNING, 
                    "The first proposition is not temporal: ", 
                    first.getId());
        }
        Proposition tmpSecond = itr.next();
        if (tmpSecond instanceof TemporalProposition) {
            second = (TemporalProposition) tmpSecond;
        } else {
            logger.log(Level.WARNING, 
                    "The second proposition is not temporal: ", 
                    second.getId());
        }
        
        String distance = PropositionUtil
                            .distanceBetweenFormattedShort(first, second, 
                                this.units);
        return new String[] {distance};
    }
    
    @Override
    public void validate(KnowledgeSource knowledgeSource) throws 
            TableColumnSpecValidationFailedException, 
            KnowledgeSourceReadException {
        int i = 1;
        for (Link link : this.links) {
            try {
                link.validate(knowledgeSource);
            } catch (LinkValidationFailedException ex) {
                throw new TableColumnSpecValidationFailedException(
                        "Validation of link " + i + " failed", ex);
            }
            i++;
        }
    }
    
}
