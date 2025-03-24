# NoSQL Graph DBMS - Day 1 activities

## Activity #1: Create your graph data model and generate the Cypher and image of your model

See the graph data model
[online](https://arrows.app/#/import/json=eyJncmFwaCI6eyJzdHlsZSI6eyJmb250LWZhbWlseSI6Ik51bml0byBTYW5zIiwiYmFja2dyb3VuZC1jb2xvciI6IiNGMkYyRjIiLCJiYWNrZ3JvdW5kLWltYWdlIjoiIiwiYmFja2dyb3VuZC1zaXplIjoiMTAwJSIsIm5vZGUtY29sb3IiOiIjNEM4RURBIiwiYm9yZGVyLXdpZHRoIjowLCJib3JkZXItY29sb3IiOiIjMDAwMDAwIiwicmFkaXVzIjo3NSwibm9kZS1wYWRkaW5nIjo1LCJub2RlLW1hcmdpbiI6Miwib3V0c2lkZS1wb3NpdGlvbiI6ImF1dG8iLCJub2RlLWljb24taW1hZ2UiOiIiLCJub2RlLWJhY2tncm91bmQtaW1hZ2UiOiIiLCJpY29uLXBvc2l0aW9uIjoiaW5zaWRlIiwiaWNvbi1zaXplIjo2NCwiY2FwdGlvbi1wb3NpdGlvbiI6Imluc2lkZSIsImNhcHRpb24tbWF4LXdpZHRoIjoyMDAsImNhcHRpb24tY29sb3IiOiIjZmZmZmZmIiwiY2FwdGlvbi1mb250LXNpemUiOjIwLCJjYXB0aW9uLWZvbnQtd2VpZ2h0Ijoibm9ybWFsIiwibGFiZWwtcG9zaXRpb24iOiJpbnNpZGUiLCJsYWJlbC1kaXNwbGF5IjoiYmFyZSIsImxhYmVsLWNvbG9yIjoiI2ZmZmZmZiIsImxhYmVsLWJhY2tncm91bmQtY29sb3IiOiIjODQ4NDg0IiwibGFiZWwtYm9yZGVyLWNvbG9yIjoiIzg0ODQ4NCIsImxhYmVsLWJvcmRlci13aWR0aCI6MywibGFiZWwtZm9udC1zaXplIjoyMCwibGFiZWwtcGFkZGluZyI6NSwibGFiZWwtbWFyZ2luIjo0LCJkaXJlY3Rpb25hbGl0eSI6ImRpcmVjdGVkIiwiZGV0YWlsLXBvc2l0aW9uIjoiYWJvdmUiLCJkZXRhaWwtb3JpZW50YXRpb24iOiJwYXJhbGxlbCIsImFycm93LXdpZHRoIjozLCJhcnJvdy1jb2xvciI6IiM4NDg0ODQiLCJtYXJnaW4tc3RhcnQiOjUsIm1hcmdpbi1lbmQiOjUsIm1hcmdpbi1wZWVyIjoyMCwiYXR0YWNobWVudC1zdGFydCI6Im5vcm1hbCIsImF0dGFjaG1lbnQtZW5kIjoibm9ybWFsIiwicmVsYXRpb25zaGlwLWljb24taW1hZ2UiOiIiLCJ0eXBlLWNvbG9yIjoiIzg0ODQ4NCIsInR5cGUtYmFja2dyb3VuZC1jb2xvciI6IiNGMkYyRjIiLCJ0eXBlLWJvcmRlci1jb2xvciI6IiM4NDg0ODQiLCJ0eXBlLWJvcmRlci13aWR0aCI6MCwidHlwZS1mb250LXNpemUiOjIxLCJ0eXBlLXBhZGRpbmciOjUsInByb3BlcnR5LXBvc2l0aW9uIjoib3V0c2lkZSIsInByb3BlcnR5LWFsaWdubWVudCI6ImNvbG9uIiwicHJvcGVydHktY29sb3IiOiIjODQ4NDg0IiwicHJvcGVydHktZm9udC1zaXplIjoyMCwicHJvcGVydHktZm9udC13ZWlnaHQiOiJub3JtYWwifSwibm9kZXMiOlt7ImlkIjoibjAiLCJwb3NpdGlvbiI6eyJ4IjotMzksInkiOjE0MS41NTI3Nzc4NDg2MzU4M30sImNhcHRpb24iOiIiLCJsYWJlbHMiOlsiUGVyc29uIl0sInByb3BlcnRpZXMiOnsibmFtZSI6IkFsZXhhbmRyZSIsImxvY2F0aW9uIjoiTW9udHBlbGxpZXIifSwic3R5bGUiOnt9fSx7ImlkIjoibjIiLCJwb3NpdGlvbiI6eyJ4IjoxMTMuNzQ2NDc5MjIyNDE5MDgsInkiOjQ4MS44MDU4NjA3OTc2MjE4fSwiY2FwdGlvbiI6IiIsImxhYmVscyI6WyJVbml2ZXJzaXR5Il0sInByb3BlcnRpZXMiOnsibmFtZSI6IlBvbHl0ZWNoIE1vbnRwZWxsaWVyIiwiZGVwYXJ0bWVudCI6IkRPIn0sInN0eWxlIjp7Im5vZGUtY29sb3IiOiIjZmI5ZTAwIn19LHsiaWQiOiJuMyIsInBvc2l0aW9uIjp7IngiOjQwNy42NjQ5OTMyODYxMzI4LCJ5IjotOS40OTcxMjM1MTgzNTYyMzN9LCJjYXB0aW9uIjoiIiwibGFiZWxzIjpbIkdpdEh1YiBSZXBvc2l0b3J5Il0sInByb3BlcnRpZXMiOnsibmFtZSI6InBva2VwbGF0aW51bSIsIm9yZyI6InByZXQifSwic3R5bGUiOnsibm9kZS1jb2xvciI6IiM3YjY0ZmYifX0seyJpZCI6Im40IiwicG9zaXRpb24iOnsieCI6LTE2OS4yOTMwNTM3MDYzMDA5MiwieSI6LTIwNy45MTQ0ODQ2MDUyNjE4M30sImNhcHRpb24iOiIiLCJsYWJlbHMiOlsiR2FtZSJdLCJwcm9wZXJ0aWVzIjp7Im5hbWUiOiJPdXRlciBXaWxkcyIsImdlbnJlIjoiQWN0aW9uLWFkdmVudHVyZSwgUHV6emxlLCBJbmRpZSJ9LCJzdHlsZSI6eyJub2RlLWNvbG9yIjoiI2Y0NGUzYiJ9fSx7ImlkIjoibjUiLCJwb3NpdGlvbiI6eyJ4IjoyOTYuMzgxOTIyNzc4MjkwMjMsInkiOi0zNzIuOTI0NjYwNDI0OTU2OX0sImNhcHRpb24iOiIiLCJsYWJlbHMiOlsiQ29tcGFueSJdLCJwcm9wZXJ0aWVzIjp7Im5hbWUiOiJNb2JpdXMgRGlnaXRhbCIsImZvdW5kZWRfaW4iOiIyMDEzIn0sInN0eWxlIjp7Im5vZGUtY29sb3IiOiIjNjhiYzAwIn19LHsiaWQiOiJuNiIsInBvc2l0aW9uIjp7IngiOi02NDQuMzkyMzQwNzUyMjY1OSwieSI6NjYuMzg0MjE2NjYyNTI1NTd9LCJjYXB0aW9uIjoiIiwibGFiZWxzIjpbIkNvbXBhbnkiXSwicHJvcGVydGllcyI6eyJuYW1lIjoiQW5uYXB1cm5hIEludGVyYWN0aXZlIiwiZm91bmRlZF9pbiI6IjIwMTYifSwic3R5bGUiOnsibm9kZS1jb2xvciI6IiM2OGJjMDAifX1dLCJyZWxhdGlvbnNoaXBzIjpbeyJpZCI6Im4xIiwiZnJvbUlkIjoibjAiLCJ0b0lkIjoibjIiLCJ0eXBlIjoiU1RVRElFU19JTiIsInByb3BlcnRpZXMiOnsic2luY2UiOiIyMDIyIn0sInN0eWxlIjp7fX0seyJpZCI6Im4yIiwiZnJvbUlkIjoibjAiLCJ0b0lkIjoibjMiLCJ0eXBlIjoiQ09OVFJJQlVURVNfVE8iLCJwcm9wZXJ0aWVzIjp7InByX29wZW5lZCI6IjE3In0sInN0eWxlIjp7fX0seyJpZCI6Im4zIiwiZnJvbUlkIjoibjAiLCJ0b0lkIjoibjQiLCJ0eXBlIjoiUExBWUVEIiwicHJvcGVydGllcyI6eyJwbGF5dGltZSI6IjMwLjcifSwic3R5bGUiOnt9fSx7ImlkIjoibjQiLCJmcm9tSWQiOiJuNSIsInRvSWQiOiJuNCIsInR5cGUiOiJERVZFTE9QRURfQlkiLCJwcm9wZXJ0aWVzIjp7ImluIjoiMjAxMyJ9LCJzdHlsZSI6e319LHsiaWQiOiJuNSIsImZyb21JZCI6Im42IiwidG9JZCI6Im40IiwidHlwZSI6IlBVQkxJU0hFRF9CWSIsInByb3BlcnRpZXMiOnsiaW4iOiIyMDE1In0sInN0eWxlIjp7fX1dfSwiZGlhZ3JhbU5hbWUiOiJVbnRpdGxlZCBncmFwaCJ9).

Cypher script for creating the data model:

```cypher
CREATE (:`GitHub Repository` {name: "pokeplatinum", org: "pret"})<-[:CONTRIBUTES_TO {pr_opened: 17}]-(n0:Person {name: "Alexandre", location: "Montpellier"})-[:STUDIES_IN {since: 2022}]->(:University {name: "Polytech Montpellier", department: "DO"}),
(n0)-[:PLAYED {playtime: 30.7}]->(n4:Game {name: "Outer Wilds", genre: "Action-adventure, Puzzle, Indie"})<-[:DEVELOPED_BY {in: 2013}]-(:Company {name: "Mobius Digital", founded_in: 2013}),
(:Company {name: "Annapurna Interactive", founded_in: 2016})-[:PUBLISHED_BY {in: 2015}]->(n4);
```

Graph of the data model:

![Graph data model](./assets/activity1.png)

## Activity #2: Run these queries of your graph model in Neo4j

Run the following Cypher queries:

```cypher
// First query
MATCH (n:Person)
RETURN n;

// Second query
MATCH (a:Person)-[r:PLAYED]->(b:Game)
RETURN a, r, b;

// Third query
MATCH (a:Person)-[r:PLAYED]->(b:Game)
RETURN a.name, r.playtime, b.name;

// Fourth query
MATCH (a:Person)-[r:CONTRIBUTES_TO] ->(b:`GitHub Repository`)
WHERE b.org = 'google'
RETURN a, r, b;
```

### First query

![First query graph](./assets/activity2_query1.png)

```
╒═════════════════════════════════════════════════════╕
│n                                                    │
╞═════════════════════════════════════════════════════╡
│(:Person {name: "Alexandre",location: "Montpellier"})│
└─────────────────────────────────────────────────────┘
```

### Second query

![Second query graph](./assets/activity2_query2.png)

```
╒═════════════════════════════════════════════════════╤══════════════════════════╤══════════════════════════════════════════════════════════════════════╕
│a                                                    │r                         │b                                                                     │
╞═════════════════════════════════════════════════════╪══════════════════════════╪══════════════════════════════════════════════════════════════════════╡
│(:Person {name: "Alexandre",location: "Montpellier"})│[:PLAYED {playtime: 30.7}]│(:Game {name: "Outer Wilds",genre: "Action-adventure, Puzzle, Indie"})│
└─────────────────────────────────────────────────────┴──────────────────────────┴──────────────────────────────────────────────────────────────────────┘
```

### Third query

```
╒═══════════╤══════════╤═════════════╕
│a.name     │r.playtime│b.name       │
╞═══════════╪══════════╪═════════════╡
│"Alexandre"│30.7      │"Outer Wilds"│
└───────────┴──────────┴─────────────┘
```

### Fourth query

```
(no changes, no records)
```

The error here is expected as there are no `GitHub Repository` node with the
`org` property set to `google`.

## Activity #3: Load CVEs into Neo4j

Using the following data as `nvdcve-1.1-2024.json`:
https://nvd.nist.gov/feeds/json/cve/1.1/nvdcve-1.1-2024.json.zip

### Getting the types of the keys

Query:

```cypher
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND keys(value) AS key
RETURN key, apoc.meta.cypher.type(value[key]);
```

Result:

```
╒═══════════════════════╤═════════════════════════════════╕
│key                    │apoc.meta.cypher.type(value[key])│
╞═══════════════════════╪═════════════════════════════════╡
│"CVE_Items"            │"LIST OF MAP"                    │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_type"        │"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_format"      │"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_timestamp"   │"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_numberOfCVEs"│"STRING"                         │
├───────────────────────┼─────────────────────────────────┤
│"CVE_data_version"     │"STRING"                         │
└───────────────────────┴─────────────────────────────────┘
```

### Getting the number of CVEs

Query:

```cypher
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND value.CVE_Items as cve
RETURN count (cve);
```

Result:

```
╒═══════════╕
│count (cve)│
╞═══════════╡
│37388      │
└───────────┘
```

### Getting the number of CVEs per severity

Query:

```cypher
WITH "file:///nvdcve-1.1-2024.json" as url 
CALL apoc.load.json(url) YIELD value 
UNWIND value.CVE_Items as cve
WITH cve.impact.baseMetricV3.cvssV3.baseSeverity as severity, COUNT(*) as cnt
LIMIT 10
RETURN severity, cnt;
```

Result:

```
╒══════════╤═════╕
│severity  │cnt  │
╞══════════╪═════╡
│"CRITICAL"│2527 │
├──────────┼─────┤
│"HIGH"    │6702 │
├──────────┼─────┤
│null      │17693│
├──────────┼─────┤
│"MEDIUM"  │10134│
├──────────┼─────┤
│"LOW"     │332  │
└──────────┴─────┘
```

### Create the graph model

To make it a bit faster, we only load 100 CVEs.

Query:

```cypher
CALL apoc.load.jsonArray("file:///nvdcve-1.1-2024.json", "$.CVE_Items")
YIELD value
LIMIT 100
MERGE (cve: CVE {cve_id: value.cve.CVE_data_meta.ID})
WITH *
UNWIND value.cve.references.reference_data AS reference_value
MERGE (reference: Reference {name: reference_value.name, url: reference_value.url})
MERGE (cve)-[:REFERENCES]->(reference)
RETURN *;
```

Result:

![CVE graph](./assets/activity3_query1.png)

### Getting the CVEs

Query:

```cypher
MATCH (n: CVE)-[references: REFERENCES]->(:Reference)
RETURN n.cve_id, collect (references);
```

Result:

![CVE table](./assets/activity3_query2.png)

## Activity #4: Create a graph model from the stackoverflow API

Using the following data as `stackoverflow.json`:
https://api.stackexchange.com/2.2/questions?pagesize=100&order=desc&sort=creation&tagged=neo4j&site=stackoverflow&filter=!5-i6Zw8Y)4W7vpy91PMYsKM-k9yzEsSC1_Uxlf

### Loading the data

First, let's load the data from the JSON file:

```cypher
CALL apoc.load.jsonArray("file:///stackoverflow.json", "$.items")
YIELD value
MERGE (question_owner: User {name: value.owner.display_name})
MERGE (question: Question {title: value.title, body: value.body_markdown})
MERGE (question_owner)-[:ASKED]->(question)
WITH *
UNWIND value.answers AS answer_value
MERGE (answer_owner: User {name: answer_value.owner.display_name})
MERGE (answer: Answer {title: answer_value.title, body: answer_value.body_markdown})
MERGE (answer_owner)-[:PROVIDED]->(answer)
MERGE (answer)-[:ANSWERS]->(question)
WITH *
UNWIND value.tags AS tag_value
MERGE (tag: Tag {text: tag_value})
MERGE (question)-[:TAGGED]->(tag)
RETURN *;
```

Result:

![Query graph](./assets/activity4_query1.png)

### See the schema

Then, we can get the schema visualization as usual:

```cypher
CALL db.schema.visualization;
```

Result:

![Graph model](./assets/activity4_query2.png)
