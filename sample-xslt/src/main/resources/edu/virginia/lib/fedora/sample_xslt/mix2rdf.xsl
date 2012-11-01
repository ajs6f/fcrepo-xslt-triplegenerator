<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">
    
    <xsl:param name="pid"/>
    
    <xsl:template match="/">
        <rdf:RDF xmlns:fedora-model="info:fedora/fedora-system:def/model#"
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:mix="http://www.loc.gov/mix/v20#">
            <rdf:Description rdf:about="info:fedora/{$pid}">
                <mix:predicate>info:dummy/MIX-SUCCESS</mix:predicate>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>
    
</xsl:stylesheet>
