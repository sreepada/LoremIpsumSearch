# LoremIpsumSearch
Contains search algorithms to be used with lucene and solr


bbox query: http://localhost:8983/solr/collection1/browse?q={!field%20f=bbox}Contains%28ENVELOPE%28-180.0,%20180.0,%2090.0,%2066.56%29%29


to delete solr indexes completely:

curl  http://localhost:8983/solr/update -H "Content-Type: text/xml" --data-binary '<delete><query>*:*</query></delete>'
curl  http://localhost:8983/solr/update?commit=true -d  '<commit />'
curl http://localhost:8983/solr/update?commit=true -d  '<optimize />'
