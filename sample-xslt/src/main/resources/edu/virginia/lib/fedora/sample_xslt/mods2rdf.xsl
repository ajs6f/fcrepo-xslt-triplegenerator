<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="xs" version="2.0">

    <xsl:param name="pid"/>

    <xsl:template match="/">
        <rdf:RDF xmlns:fedora-model="info:fedora/fedora-system:def/model#"
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:mods="http://www.loc.gov/mods/v3#">
            <!-- we avoid using a blank node -->
            <xsl:variable name="blanknode1" select="concat('info:fedora/',$pid,'/blank/1')"/>
            <rdf:Description rdf:about="info:fedora/{$pid}">
                <mods:predicate>info:dummy/MODS-SUCCESS</mods:predicate>
                <mods:somePredicate rdf:resource="{$blanknode1}"/>
            </rdf:Description>
            <rdf:Description rdf:about="{$blanknode1}">
                <mods:someOtherPredicate rdf:resource="http://example.com/x"/>
                <mods:aThirdPredicate rdf:resource="http://example.com/y"/>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>

</xsl:stylesheet>