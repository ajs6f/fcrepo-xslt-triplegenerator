<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:template match="/">
        <rdf:RDF xmlns:fedora-model="info:fedora/fedora-system:def/model#"
            xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
            xmlns:uva="http://fedora.lib.virginia.edu/relationships#">
            <rdf:Description rdf:about="info:fedora/uva-lib:1038847">
                <uva:hasCatalogRecordIn rdf:resource="info:fedora/uva-lib:744806"/>
                <uva:hasPreceedingPage rdf:resource="info:fedora/uva-lib:1038846"/>
                <uva:isFollowingPageOf rdf:resource="info:fedora/uva-lib:1038846"/>
                <uva:hasFollowingPage rdf:resource="info:fedora/uva-lib:1038848"/>
                <uva:isPreceedingPageOf rdf:resource="info:fedora/uva-lib:1038848"/>
                <fedora-model:hasModel rdf:resource="info:fedora/djatoka:jp2CModel"/>
            </rdf:Description>
        </rdf:RDF>
    </xsl:template>
    
</xsl:stylesheet>