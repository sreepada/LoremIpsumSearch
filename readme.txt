
1) Linked based relevancy algorithm
-----------------------------------

untar the jar in the lucene/solr directory
mv to solr directory.

    a) setting classpath
    --------------------
    add solr/dist/*, solr/dist/solrj-lib/*, lucene/replicator/lib/*, solr/mylib/*,nutch/build/*,nutch/build/lib/*, solr/src/clavin/*
    into classpath

    b) compile
    ---------- 
    cd src/clavin/
    javac *.java
    cd ../../
    javac *.java

    c) java SolrjPopulator <segment_path> <linkdb_data_path> <content_dump_dir>
       Note that segment_path : path till data file
                 similarly for linkdb
                 content_dump_dir : directory containing all the files using bin/nutch dump

2) Content based relevancy
--------------------------
    a) compile
    ----------
    javac DocumentInfo.java TFIDF.java

    b) running script
    ----------------- 
    java TFIDF <directory containing files> <query_in_quotes>
    for example, java TFIDF nutch/dump/ "oil extraction"


3) Running queries
------------------
One can run queries using SolrjSearcher.java 

    a) compile
    ----------
    javac SolrjSearcher.java

    b) running
    ----------
    java SolrjSearcher "<query>"
    for example java SolrjSearcher "text:arctic and ((text:"oil exploration"~10) or (text:"iron") or (text:oil))"
    <query> - should be a valid solr query
   
    Note: We have not implemented adding fields, restricting rows and other features in this java implementation

