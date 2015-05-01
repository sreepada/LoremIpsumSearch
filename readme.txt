
1) Linked based relevancy algorithm
-----------------------------------

untar the jar in the lucene/solr directory
mv to solr directory.

    a) setting classpath
    --------------------
    add solr/dist/*, solr/dist/solrj-lib/*, lucene/replicator/lib/*, solr/mylib/*,nutch/build/*,nutch/build/lib/*, solr/src/clavin/*
    into classpath
    
    b)download jars directly from our repo https://github.com/sreepada/LoremIpsumSearch/tree/master/lib and put it in a folder named lib/. These jars couldn’t be included because it’s huge

    c) download clavin index from https://github.com/sreepada/LoremIpsumSearch/IndexDirectory and put it in solr/IndexDirectory 

    d) compile
    ---------- 
    cd src/clavin/
    javac *.java
    cd ../../
    javac *.java

    e) Install python-networkx
    —-------------------------
    Using pip: pip install python-networkx
    Manual: download networkx from https://pypi.python.org/pypi/networkx/
            run $python setup.py install

    f) java SolrjPopulator <segment_path> <linkdb_data_path> <content_dump_dir>
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
    
4) [Extra Credit] ScoringFilter plugin for Nutch
------------------------------------------------
cp scoring-pagerank nutch/src/plugins
ant runtime
add "scoring-pagerank" in plugins.include property in nutch-site.xml



