fcrepo-xslt-triplegenerator
===========================

An experimental Fedora Resource Index triple extractor that uses
user-supplied XSLT to convert an XML datastream into an RDF/XML 
document that is parsed and added to the triple store.

Includes:

* fcrepo-xslt-triplegenerator (the actual component)
* sample-xslt (sample XSLT for use with this component)
* integration-tests (integration tests, using fedora-webapp-fedora below)
* fedora-webapp-fedora (a Fedora webapp with this component pre-installed)
* pubby-demo-webapp (an example showing how to use [Pubby](http://wifo5-03.informatik.uni-mannheim.de/pubby/) to integrate Fedora into the Web of Data)

-ajs