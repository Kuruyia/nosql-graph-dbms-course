# NoSQL Graph DBMS - Day 2 activities

## Activity #5.1: Working with airport data (Neo4j "Graph Data Science" sandbox)

Here's the schema visualization of this database:

![Airport database schema visualization](./assets/activity5_schema1.png)

### Create the GDS project

Query:

```cypher
CALL gds.graph.project(
    'routes',
    'Airport',
    'HAS_ROUTE'
)
YIELD graphName, nodeProjection, nodeCount, relationshipProjection, relationshipCount;
```

Result:

```
╒═════════╤═════════════════════════════════════════════╤═════════╤══════════════════════════════════════════════════════════════════════╤═════════════════╕
│graphName│nodeProjection                               │nodeCount│relationshipProjection                                                │relationshipCount│
╞═════════╪═════════════════════════════════════════════╪═════════╪══════════════════════════════════════════════════════════════════════╪═════════════════╡
│"routes" │{Airport: {label: "Airport", properties: {}}}│3503     │{HAS_ROUTE: {aggregation: "DEFAULT", orientation: "NATURAL", indexInve│46389            │
│         │                                             │         │rse: false, properties: {}, type: "HAS_ROUTE"}}                       │                 │
└─────────┴─────────────────────────────────────────────┴─────────┴──────────────────────────────────────────────────────────────────────┴─────────────────┘
```

### Get all routes

Query:

```cypher
MATCH (a:Airport)-[r:HAS_ROUTE]->(b:Airport) 
RETURN COUNT(r);
```

Result:

```
╒════════╕
│COUNT(r)│
╞════════╡
│46389   │
└────────┘
```

### Centrality

Query:

```cypher
CALL gds.pageRank.stream('routes')
YIELD nodeId, score
WITH gds.util.asNode(nodeId) AS n, score AS pageRank
RETURN n.iata AS iata, n.descr AS description, pageRank
ORDER BY pageRank DESC, iata ASC;
```

Result:

```
╒═════╤══════════════════════════════════════════════════════════════════════╤══════════════════╕
│iata │description                                                           │pageRank          │
╞═════╪══════════════════════════════════════════════════════════════════════╪══════════════════╡
│"DFW"│"Dallas/Fort Worth International Airport"                             │11.979782606703342│
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"ORD"│"Chicago O'Hare International Airport"                                │11.16298817892027 │
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"DEN"│"Denver International Airport"                                        │10.997299338126384│
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"ATL"│"Hartsfield - Jackson Atlanta International Airport"                  │10.389948350302957│
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"IST"│"Istanbul International Airport"                                      │8.425801217705782 │
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"CDG"│"Paris Charles de Gaulle"                                             │8.401469085296542 │
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
...
```

### Write `pageRank` property

Query:

```cypher
CALL gds.pageRank.write('routes', { writeProperty: 'pageRank' })
YIELD nodePropertiesWritten, ranIterations;
```

Result:

```
╒═════════════════════╤═════════════╕
│nodePropertiesWritten│ranIterations│
╞═════════════════════╪═════════════╡
│3503                 │20           │
└─────────────────────┴─────────────┘
```

### Show the airports with the `pageRank` property

Query:

```cypher
MATCH (a:Airport)
RETURN a.iata AS iata, a.descr AS description, a.pageRank AS pageRank
ORDER BY a.pageRank DESC, a.iata ASC;
```

Result:

```
╒═════╤══════════════════════════════════════════════════════════════════════╤══════════════════╕
│iata │description                                                           │pageRank          │
╞═════╪══════════════════════════════════════════════════════════════════════╪══════════════════╡
│"DFW"│"Dallas/Fort Worth International Airport"                             │11.979782606703338│
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"ORD"│"Chicago O'Hare International Airport"                                │11.16298817892027 │
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"DEN"│"Denver International Airport"                                        │10.997299338126384│
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"ATL"│"Hartsfield - Jackson Atlanta International Airport"                  │10.389948350302957│
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"IST"│"Istanbul International Airport"                                      │8.425801217705784 │
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
│"CDG"│"Paris Charles de Gaulle"                                             │8.401469085296544 │
├─────┼──────────────────────────────────────────────────────────────────────┼──────────────────┤
...
```

### Discover distinct clusters

Query:

```cypher
CALL gds.louvain.stream('routes')
YIELD nodeId, communityId
WITH gds.util.asNode(nodeId) AS n, communityId
RETURN
    communityId,
    SIZE(COLLECT(n)) AS numberOfAirports,
    COLLECT(DISTINCT n.city) AS cities
ORDER BY numberOfAirports DESC, communityId;
```

Result:

```
╒═══════════╤════════════════╤══════════════════════════════════════════════════════════════════════╕
│communityId│numberOfAirports│cities                                                                │
╞═══════════╪════════════════╪══════════════════════════════════════════════════════════════════════╡
│3018       │668             │["Atlanta", "Anchorage", "Austin", "Nashville", "Boston", "Baltimore",│
│           │                │ "Washington D.C.", "Dallas", "Fort Lauderdale", "Houston", "New York"│
│           │                │, "Los Angeles", "Orlando", "Miami", "Minneapolis", "Chicago", "West P│
│           │                │alm Beach", "Phoenix", "Raleigh", "Seattle", "San Francisco", "San Jos│
│           │                │e", "Tampa", "San Diego", "Long Beach", "Santa Ana", "Salt Lake City",│
│           │                │ "Las Vegas", "Denver", "White Plains", "San Antonio", "New Orleans", │
│           │                │"Newark", "Cedar Rapids", "Honolulu", "El Paso", "San Juan", "Clevelan│
│           │                │d", "Oakland", "Tucson", "Santa Fe", "Philadelphia", "Detroit", "Toron│
│           │                │to", "Vancouver", "Ottawa", "Fort Myers", "Montreal", "Edmonton", "Cal│
│           │                │gary", "St. John's", "Mexico City", "Kingston", "Tallahassee", "Pittsb│
│           │                │urgh", "Portland", "Oaklahoma City", "Ontario", "Rochester", "Halifax"│
│           │                │, "Winnipeg", "Charlotte", "Cancun", "Palm Springs", "Memphis", "Cinci│
│           │                │nnati", "Indianapolis", "Kansas City", "St Louis", "Albuquerque", "Mil│
│           │                │waukee", "Harrison", "Salina", "Omaha", "Tulsa", "Puerto Vallarta", "K│
│           │                │ahului", "Nassau", "Freeport", "George Town", "Key West", "Bridgetown"│
│           │                │, "St. George", "Charlotte Amalie", "Hamilton", "Scarborough", "Port o│
│           │                │f Spain", "Montego Bay", "Little Rock", "Curacao", "Kralendijk", "Oran│
│           │                │jestad", "Norfolk", "Jacksonville", "Providence", "Punta Cana", "Harri│
│           │                │sburg", "Sacramento", "Roatan Island", "Tegucigalpa", "Colorado Spring│
│           │                │s", "Huntsville", "Birmingham", "Quebec City", "Rapid City", "Louisvil│
│           │                │le", "Buffalo", "Shreveport", "Boise", "Lihue", "Lubbock", "Panama Cit│
│           │                │y Beach", "Harlingen", "Reno", "Columbus", "Idaho Falls", "Albany", "W│
│           │                │ichita", "Midland", "Saskatoon", "Hartford", "Billings", "Sint Martin"│
│           │                │, "Springfield", "Richmond", "Texarkana", "Peoria", "Hilo", "Lexington│
│           │                │", "Guatemala City", "Islip", "Niagara Falls", "Newburgh", "South Bimi│
│           │                │ni", "Havana", "Corpus Christi", "Abilene", "Waco", "College Station",│
│           │                │ "Bloomington/Normal", "Beaumont/Port Arthur", "Des Moines", "Myrtle B│
│           │                │each", "Alexandria", "Cozumel", "Aguascalientes", "Monterrey", "Amaril│
│           │                │lo", "Silao", "Brownsville", "Baton Rouge", "Belize City", "Columbia",│
│           │                │ "Chattanooga", "Charleston", "Champaign/Urbana", "Dayton", "Chihuahua│
│           │                │", "Durango", "Evansville", "Fargo", "Fresno", "Sioux Falls", "Fort Sm│
│           │                │ith", "Fort Wayne", "Garden City", "Guadalajara", "Longview", "Grand J│
│           │                │unction", "Gulfport", "Grand Island", "Fort Hood/Killeen", "Grand Rapi│
│           │                │ds", "Greensboro", "Greenville", "Jackson", "Joplin", "Lawton", "Lake │
│           │                │Charles", "Lafayette", "Liberia", "Laredo", "Mc Allen", "Montgomery", │
│           │                │"Manhattan", "Moline", "Morelia", "Monroe", "Mobile", "Madison", "Maza│
│           │                │tlán", "Puebla", "Providenciales Island", "Pensacola", "Panama City", │
│           │                │"Querétaro", "Roswell", "Santa Clara", "Savannah", "San José del Cabo"│
│           │                │, "San Angelo", "San Luis Potosí", "Wichita Falls", "Torreón", "Tyler"│
│           │                │, "Knoxville", "Valparaiso", "Fayetteville/Springdale/", "Zacatecas", │
│           │                │"Vieux Fort", "Akron", "Burlington", "Manchester", "Syracuse", "Regina│
│           │                │", "Florence", "Asheville", "Eagle", "Hayden", "Sarasota/Bradenton", "│
│           │                │Topeka", "Lansing", "Roanoke", "Marquette", "Green Bay", "Augusta", "B│
│           │                │angor", "Fayetteville", "Hilton Head Island", "Wilmington", "Puerto Pl│
│           │                │ata", "Holguin", "Varadero", "Zandery", "Aguadilla", "Naples", "Gaines│
│           │                │ville", "La Romana", "Santo Domingo", "Santiago", "La Mesa", "Mérida",│
│           │                │ "Managua", "Port-au-Prince", "Cayman Brac", "Georgetown", "Marsh Harb│
│           │                │our", "North Eleuthera", "Cartagena", "Maracaibo", "Fort-de-France", "│
│           │                │Pointe-à-Pitre Le Raizet", "Saint George's", "Christiansted", "Bassete│
│           │                │rre", "Burbank", "Samana", "Ponce", "Atlantic City", "Allentown", "App│
│           │                │leton", "Wilkes-Barre/Scranton", "Kalamazoo", "Brunswick", "Charlottes│
│           │                │ville", "Daytona Beach", "Dothan", "New Bern", "Flint", "Columbus/W Po│
│           │                │int/Starkville", "Lewisburg", "Saginaw", "Macon", "Meridian", "Melbour│
│           │                │ne", "Muscle Shoals", "Newport News", "Hattiesburg/Laurel", "South Ben│
│           │                │d", "Bristol/Johnson/Kingsport", "Trenton", "Tupelo", "Valdosta", "Lan│
│           │                │ai City", "Kailua/Kona", "Pago Pago", "Lahaina", "Kaunakakai", "London│
│           │                │", "Waterloo", "Sioux City", "Kitchener", "Cayo Coco", "Victoria", "Ar│
│           │                │cata/Eureka", "Bakersfield", "Crescent City", "Chico", "Eugene", "Klam│
│           │                │ath Falls", "Medford", "Modesto", "Monterey", "North Bend", "Pasco", "│
│           │                │Redding", "Redmond", "Santa Barbara", "San Luis Obispo", "Kelowna", "A│
│           │                │spen", "Bellingham", "Carlsbad", "Spokane", "Kingman", "Merced", "Mamm│
│           │                │oth Lakes", "Yuma", "Prescott", "Santa Maria", "Santa Rosa", "Visalia"│
│           │                │, "Hermosillo", "Loreto", "Uruapan", "Ixtapa", "Manzanillo", "Provo", │
│           │                │"Butte", "Bozeman", "Cedar City", "Moab", "Cody", "Casper", "Elko", "G│
│           │                │illette", "Kalispell", "Great Falls", "Helena", "Lewiston", "Missoula"│
│           │                │, "Pocatello", "Rock Springs", "St George", "Twin Falls", "Vernal", "B│
│           │                │ranson", "Fort McMurray", "Alliance", "Alamosa", "Scottsbluff", "Bisma│
│           │                │rck", "Cortez", "Cheyenne", "Dodge City", "Dickinson", "Kearney", "Far│
│           │                │mington", "Gunnison", "Williston", "Laramie", "North Platte", "Liberal│
│           │                │", "Lincoln", "Mc Cook", "Minot", "Montrose", "Page", "Pierre", "Puebl│
│           │                │o", "Riverton", "Sheridan", "Watertown", "Hancock", "Mosinee", "Dubuqu│
│           │                │e", "Decatur", "Duluth", "Eau Claire", "Elmira/Corning", "La Crosse", │
│           │                │"Muskegon", "Paducah", "St Cloud", "Toledo", "Traverse City", "State C│
│           │                │ollege", "Toluca", "Latrobe", "Worcester", "Plattsburgh", "Treasure Ca│
│           │                │y", "Governor's Harbour", "San Salvador", "Moncton", "Altoona", "Bingh│
│           │                │amton", "Beckley", "Hagerstown", "Johnstown", "Lancaster", "Morgantown│
│           │                │", "Staunton/Waynesboro/Harrisonburg", "Ithaca", "Grand Forks", "Chica│
│           │                │go/Rockford", "Aberdeen", "Alpena", "Bemidji", "Brainerd", "Hibbing", │
│           │                │"Iron Mountain / Kingsford", "International Falls", "Rhinelander", "Na│
│           │                │ntucket", "Bar Harbor", "Hyannis", "Lebanon", "Martha's Vineyard", "Pr│
│           │                │esque Isle", "Provincetown", "Rockland", "Rutland", "Saranac Lake", "C│
│           │                │old Bay", "Cordova", "Adak Island", "Kenai", "Homer", "Iliamna", "Sand│
│           │                │ Point", "St Paul Island", "Valdez", "Athens", "Hobbs", "Acapulco", "H│
│           │                │uatulco", "Ciudad del Carmen", "Saltillo", "Oaxaca", "Tampico", "Villa│
│           │                │hermosa", "Veracruz", "Flagstaff", "Show Low", "Silver City", "Sault S│
│           │                │te Marie", "Deer Lake", "Fredericton", "Windsor", "Thunder Bay", "Sydn│
│           │                │ey", "Sudbury", "Saint John", "Timmins", "North Bay", "Charlottetown",│
│           │                │ "Sarnia", "Camaguey", "Erie", "New Haven", "Williamsport", "Salisbury│
│           │                │", "Escanaba", "Pellston", "Bradford", "Dubois", "Franklin", "Jamestow│
│           │                │n", "Parkersburg", "Imperial", "Trail", "Bella Coola", "Campbell River│
│           │                │", "Nanaimo", "Castlegar", "Dawson Creek", "Kamloops", "Prince Rupert"│
│           │                │, "Powell River", "Comox", "Quesnel", "Williams Lake", "Cranbrook", "F│
│           │                │ort St.John", "Prince George", "Terrace", "Whitehorse", "Smithers", "P│
│           │                │enticton", "Sandspit", "Port Hardy", "Masset", "Walla Walla", "Wenatch│
│           │                │ee", "St Petersburg-Clearwater", "Pullman/Moscow", "Yakima", "Marigot"│
│           │                │, "Gustavia", "Culebra Island", "Mayaguez", "Vieques Island", "Charles│
│           │                │town", "The Valley", "Road Town", "Spanish Town", "Stockton", "Punta G│
│           │                │orda", "High Level", "Rainbow Lake", "Grande Prairie", "Abbotsford", "│
│           │                │Saint-Pierre", "Tijuana", "Cayo Largo del Sur", "Fort Nelson", "Culiac│
│           │                │án", "Chetumal", "Ciudad Obregón", "Campeche", "Ciudad Juárez", "Ciuda│
│           │                │d Victoria", "Tepic", "Colima", "Xalapa", "Lázaro Cárdenas", "Los Moch│
│           │                │is", "La Paz", "Matamoros", "Mexicali", "Minatitlán", "Nuevo Laredo", │
│           │                │"Poza Rica", "Piedras Negras", "Palenque", "Puerto Escondido", "Reynos│
│           │                │a", "Tuxtla Gutiérrez", "Tapachula", "Brandon", "Lloydminster", "Red D│
│           │                │eer", "Lethbridge", "Medicine Hat", "Chadron", "Cayenne / Rochambeau",│
│           │                │ "Anahim Lake", "Bella Bella", "Stephenville", "Moosonee", "Gander", "│
│           │                │Kapuskasing", "Belleville", "Cape Girardeau", "Clarksburg", "El Dorado│
│           │                │", "New Bedford", "Grand Canyon", "Glendive", "Glasgow", "Huron", "Hot│
│           │                │ Springs", "Huntington", "Havre", "Kirksville", "Jonesboro", "Los Alam│
│           │                │os", "Lynchburg", "Manistee", "Morristown", "Massena", "Marion", "Ogde│
│           │                │nsburg", "Wolf Point", "Owensboro", "Pendleton", "Portsmouth", "Sidney│
│           │                │", "Fort Leonard Wood", "Teterboro", "Thief River Falls", "Quincy", "W│
│           │                │orland", "Youngstown/Warren", "Cockburn Town", "South Caicos Island", │
│           │                │"La Isabela", "San Benito", "La Ceiba", "Puerto Lempira", "Isla Colón"│
│           │                │, "David", "La Fortuna/San Carlos", "Roxana", "Puntarenas", "Golfito",│
│           │                │ "Nicoya", "Puerto Jimenez", "Palmar Sur", "Quepos", "Santa Cruz", "Ca│
│           │                │p Haitien", "Guantánamo", "Little Cayman", "Spring Point", "Arthur's T│
│           │                │own", "Colonel Hill", "Rock Sound", "Matthew Town", "Deadman's Cay", "│
│           │                │Stella Maris", "Mayaguana", "Port Nelson", "False Pass", "King Cove", │
│           │                │"Yakutat", "Isla Margarita", "Valencia", "Castries", "Kingstown", "St │
│           │                │Augustine", "Fort Albany", "Salt Cay", "Albrook", "Nelson Lagoon", "Co│
│           │                │ncord", "Hailey", "Everett", "Ceiba"]                                 │
├───────────┼────────────────┼──────────────────────────────────────────────────────────────────────┤
...
```

### Get node similarities

Query:

```cypher
CALL gds.nodeSimilarity.stream('routes')
YIELD node1, node2, similarity
WITH gds.util.asNode(node1) AS n1, gds.util.asNode(node2) AS n2, similarity
RETURN
    n1.iata AS iata,
    n1.city AS city,
    COLLECT({iata:n2.iata, city:n2.city, similarityScore: similarity}) AS similarAirports
ORDER BY city LIMIT 20;
```

Result:

```
╒═════╤═════════════╤══════════════════════════════════════════════════════════════════════╕
│iata │city         │similarAirports                                                       │
╞═════╪═════════════╪══════════════════════════════════════════════════════════════════════╡
│"AAL"│"Aalborg"    │[{iata: "KRS", city: "Kjevik", similarityScore: 0.3333333333333333}, {│
│     │             │iata: "HAU", city: "Karmøy", similarityScore: 0.2727272727272727}, {ia│
│     │             │ta: "SZZ", city: "Goleniow", similarityScore: 0.2608695652173913}, {ia│
│     │             │ta: "AAR", city: "Aarhus", similarityScore: 0.25}, {iata: "TRF", city:│
│     │             │ "Torp", similarityScore: 0.24444444444444444}, {iata: "BLL", city: "B│
│     │             │illund", similarityScore: 0.23333333333333334}, {iata: "AES", city: "Å│
│     │             │lesund", similarityScore: 0.22727272727272727}, {iata: "GOT", city: "G│
│     │             │othenburg", similarityScore: 0.21875}, {iata: "SVG", city: "Stavanger"│
│     │             │, similarityScore: 0.21568627450980393}, {iata: "KYA", city: "Konya", │
│     │             │similarityScore: 0.21052631578947367}]                                │
├─────┼─────────────┼──────────────────────────────────────────────────────────────────────┤
...
```

### Get node similarities (`topN` and `bottomN`)

Query:

```cypher
CALL gds.nodeSimilarity.stream(
    'routes',
    { topK: 1, topN: 10 }
)
YIELD node1, node2, similarity
WITH gds.util.asNode(node1) AS n1, gds.util.asNode(node2) AS n2, similarity AS similarityScore
RETURN
    n1.iata AS iata,
    n1.city AS city,
    { iata:n2.iata, city:n2.city } AS similarAirport,
    similarityScore
ORDER BY city;
```

Result:

```
╒═════╤══════════════════════╤════════════════════════════════════════╤═══════════════╕
│iata │city                  │similarAirport                          │similarityScore│
╞═════╪══════════════════════╪════════════════════════════════════════╪═══════════════╡
│"ABI"│"Abilene"             │{iata: "TXK", city: "Texarkana"}        │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"AEX"│"Alexandria"          │{iata: "GRK", city: "Fort Hood/Killeen"}│1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"BPT"│"Beaumont/Port Arthur"│{iata: "TXK", city: "Texarkana"}        │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"CLL"│"College Station"     │{iata: "LCH", city: "Lake Charles"}     │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"DRO"│"Durango"             │{iata: "SAF", city: "Santa Fe"}         │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"GCK"│"Garden City"         │{iata: "TXK", city: "Texarkana"}        │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"IAG"│"Niagara Falls"       │{iata: "PSM", city: "Portsmouth"}       │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"SAF"│"Santa Fe"            │{iata: "DRO", city: "Durango"}          │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"TXK"│"Texarkana"           │{iata: "ABI", city: "Abilene"}          │1.0            │
├─────┼──────────────────────┼────────────────────────────────────────┼───────────────┤
│"ACT"│"Waco"                │{iata: "TXK", city: "Texarkana"}        │1.0            │
└─────┴──────────────────────┴────────────────────────────────────────┴───────────────┘
```

### Calculate the shortest path between two airports

First, let's create the weighted graph projection:

```cypher
CALL gds.graph.project(
    'routes-weighted',
    'Airport',
    'HAS_ROUTE',
    { relationshipProperties: 'distance' }
) YIELD graphName, nodeProjection, nodeCount, relationshipProjection, relationshipCount;
```

Result:

```
╒═════════════════╤═════════════════════════════════════════════╤═════════╤══════════════════════════════════════════════════════════════════════╤═════════════════╕
│graphName        │nodeProjection                               │nodeCount│relationshipProjection                                                │relationshipCount│
╞═════════════════╪═════════════════════════════════════════════╪═════════╪══════════════════════════════════════════════════════════════════════╪═════════════════╡
│"routes-weighted"│{Airport: {label: "Airport", properties: {}}}│3503     │{HAS_ROUTE: {aggregation: "DEFAULT", orientation: "NATURAL", indexInve│46389            │
│                 │                                             │         │rse: false, properties: {distance: {aggregation: "DEFAULT", property: │                 │
│                 │                                             │         │"distance", defaultValue: null}}, type: "HAS_ROUTE"}}                 │                 │
└─────────────────┴─────────────────────────────────────────────┴─────────┴──────────────────────────────────────────────────────────────────────┴─────────────────┘
```

Then, calculate the shortest path using Dijkstra's algorithm:

```cypher
MATCH (source:Airport {iata: 'DEN'})
MATCH (target:Airport {iata: 'MLE'})
CALL gds.shortestPath.dijkstra.stream('routes-weighted', {
    sourceNode: source,
    targetNode: target,
    relationshipWeightProperty: 'distance'
})
YIELD index, sourceNode, targetNode, totalCost, nodeIds, costs, path
RETURN
    index,
    gds.util.asNode(sourceNode).iata AS sourceNodeName,
    gds.util.asNode(targetNode).iata AS targetNodeName,
    totalCost,
    [nodeId IN nodeIds | gds.util.asNode(nodeId).iata] AS nodeNames,
    costs,
    nodes(path) as path
ORDER BY index;
```

Result:

```
╒═════╤══════════════╤══════════════╤═════════╤═══════════════════════════════════╤═════════════════════════════════════╤══════════════════════════════════════════════════════════════════════╕
│index│sourceNodeName│targetNodeName│totalCost│nodeNames                          │costs                                │path                                                                  │
╞═════╪══════════════╪══════════════╪═════════╪═══════════════════════════════════╪═════════════════════════════════════╪══════════════════════════════════════════════════════════════════════╡
│0    │"DEN"         │"MLE"         │9704.0   │["DEN", "KEF", "HEL", "VKO", "MLE"]│[0.0, 3556.0, 5074.0, 5629.0, 9704.0]│[(:Airport {altitude: 5433,descr: "Denver International Airport",pageR│
│     │              │              │         │                                   │                                     │ank: 10.997299338126384,longest: 16000,iata: "DEN",city: "Denver",icao│
│     │              │              │         │                                   │                                     │: "KDEN",location: point({srid:4326, x:-104.672996520996, y:39.8616981│
│     │              │              │         │                                   │                                     │506348}),id: "31",pagerank: 10.997299338126387,runways: 6}), (:Airport│
│     │              │              │         │                                   │                                     │ {descr: "Reykjavik, Keflavik International Airport",altitude: 171,pag│
│     │              │              │         │                                   │                                     │eRank: 2.4095328113872316,longest: 10056,iata: "KEF",city: "Reykjavik"│
│     │              │              │         │                                   │                                     │,icao: "BIKF",location: point({srid:4326, x:-22.6056003570557, y:63.98│
│     │              │              │         │                                   │                                     │50006103516}),id: "217",pagerank: 2.4095328113872316,runways: 2}), (:A│
│     │              │              │         │                                   │                                     │irport {altitude: 179,descr: "Helsinki Ventaa",pageRank: 4.18583598716│
│     │              │              │         │                                   │                                     │3839,longest: 11286,iata: "HEL",city: "Helsinki",icao: "EFHK",location│
│     │              │              │         │                                   │                                     │: point({srid:4326, x:24.9633007049561, y:60.3171997070312}),id: "53",│
│     │              │              │         │                                   │                                     │pagerank: 4.185835987163839,runways: 3}), (:Airport {altitude: 685,des│
│     │              │              │         │                                   │                                     │cr: "Vnukovo International Airport",pageRank: 1.949832924823111,longes│
│     │              │              │         │                                   │                                     │t: 10039,iata: "VKO",city: "Moscow",icao: "UUWW",location: point({srid│
│     │              │              │         │                                   │                                     │:4326, x:37.2615013123, y:55.5914993286}),id: "318",pagerank: 1.949832│
│     │              │              │         │                                   │                                     │9248231108,runways: 2}), (:Airport {altitude: 6,descr: "Malé Internati│
│     │              │              │         │                                   │                                     │onal Airport",pageRank: 0.7543631236774755,longest: 10499,iata: "MLE",│
│     │              │              │         │                                   │                                     │city: "Malé",icao: "VRMM",location: point({srid:4326, x:73.52909851074│
│     │              │              │         │                                   │                                     │22, y:4.19183015823364}),id: "523",pagerank: 0.7543631236774754,runway│
│     │              │              │         │                                   │                                     │s: 1})]                                                               │
└─────┴──────────────┴──────────────┴─────────┴───────────────────────────────────┴─────────────────────────────────────┴──────────────────────────────────────────────────────────────────────┘
```

## Activity #5.2: Working with crime data (Neo4j "Crime Investigation" sandbox)

Here's the schema visualization of this database:

![Crime investigation database schema visualization](./assets/activity5_schema2.png)

### Give me the details of all the Crimes under investigation by Officer Larive (Badge Number 26-5234182)

Query:

```cypher
MATCH (c: Crime)-[i: INVESTIGATED_BY]->(o: Officer {badge_no: "26-5234182"}) 
RETURN c, i, o;
```

Result:

![Crimes investigated by Officer Larive](./assets/activity5_query1.png)

## Activity #6: Recommendation system

### Create the database

First, create the users:

```cypher
CREATE (u:User {name: "Alexandre" });
// ...
```

Then, create the movies:

```cypher
CREATE (m:Movie {title: "The Matrix", genre: "Action"});
CREATE (m:Movie {title: "Inception", genre: "Sci-Fi" });
CREATE (m:Movie {title: "The Godfather", genre: "Drama" });
CREATE (m:Movie {title: "Forest Gump", genre: "Drama"});
CREATE (m:Movie {title: "Intersaellar", genre: "Sci-FI" });
CREATE (m:Movie {title: "Mission Impossible", genre: "Action" });
CREATE (m:Movie {title: "La La Land", genre: "Musical" });
```

Finally, add the preferred movies:

```cypher
MATCH (u:User {name: "Alexandre" }), (m:Movie {title: "The Godfather", genre: "Drama"})
MERGE (u)-[:LIKES]->(m);
// ...
```

### Recommend movies based on what similar users liked

Query:

```cypher
MATCH (:User {name: "Alexandre"})-[:LIKES]->(m:Movie)<-[:LIKES]-(:User)-[:LIKES]->(recommendations:Movie)
WHERE m <> recommendations
RETURN DISTINCT recommendations;
```

This gets all users that liked the same movies as Alexandre liked, get the
other movies liked by those users, and return the ones that were not liked by
Alexandre.

Result:

```
╒════════════════════════════════════════════════════════════╕
│recommendations                                             │
╞════════════════════════════════════════════════════════════╡
│(:Movie {genre: "Sci-Fi",title: "Interstellar"})            │
├────────────────────────────────────────────────────────────┤
│(:Movie {genre: "Drama",title: "The Shawshank Redemption"}) │
├────────────────────────────────────────────────────────────┤
│(:Movie {genre: "Action",title: "The Matrix"})              │
├────────────────────────────────────────────────────────────┤
│(:Movie {genre: "Action",title: "The Dark Knight"})         │
├────────────────────────────────────────────────────────────┤
│(:Movie {genre: "Sci-Fi",title: "Inception"})               │
├────────────────────────────────────────────────────────────┤
│(:Movie {genre: "Comedy",title: "The Grand Budapest Hotel"})│
├────────────────────────────────────────────────────────────┤
│(:Movie {genre: "Romance",title: "Amélie Poulain"})         │
└────────────────────────────────────────────────────────────┘
```

### Recommend other movies in the same genre

Query:

```cypher
MATCH (recommendations: Movie {genre: "Sci-Fi"})
WHERE NOT (:User {name: "Alexandre"})-[:LIKES]->(recommendations)
RETURN recommendations;
```

This gets all movies of the "Sci-Fi" genre, and returns the ones that were not
liked by Alexandre.

Result:

```
╒════════════════════════════════════════════════╕
│recommendations                                 │
╞════════════════════════════════════════════════╡
│(:Movie {genre: "Sci-Fi",title: "Inception"})   │
├────────────────────────────────────────────────┤
│(:Movie {genre: "Sci-Fi",title: "Interstellar"})│
└────────────────────────────────────────────────┘
```

## Spark

Code related to Spark can be found in [`spark_scala/`](./spark_scala/).

### Getting the count of some words

For this activity, we want to count the number of occurrences of the words
"data" and "spark" in the README file of the Spark repository.

Code can be found [here](./spark_scala/src/main/scala/SimpleApp.scala).

Here's the result:

```
Lines with data: 2, Lines with spark: 19
```

### Reading and transforming data from a CSV file

For this activity, we want to get, per city, the names of people aged at least
25 that live there.

Code can be found [here](./spark_scala/src/main/scala/UserTest.scala).

Here's the result:

```
Users in San Francisco: P5
Users in New York: P1, P3
Users in Boston: P4
```
