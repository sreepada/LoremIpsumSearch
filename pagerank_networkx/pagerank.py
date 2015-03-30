import networkx as nx


D = nx.DiGraph()

ltpl = list()
with open('../temp.txt','r') as fp:
	for line in fp:
		frm,to,w = line.split(' ',3)
		tpl = (frm,to,float(w))
		ltpl.append(tpl)

	D.add_weighted_edges_from(ltpl)

pr = nx.pagerank(D)

for x in pr:
	print x + ' ' + str(pr[x] * 1000)
