Lada-Server
===========
Die Software bietet Funktionalität zur Erfassung und Bearbeitung
von Messdaten. Sowie der Planung der Messungen.

Weitere Informationen finden sich auf der Projektwebseite unter
der Adresse: https://wald.intevation.org/projects/lada/

Die Software entstand im Rahmen einer Software Entwicklung durch die
Intevation GmbH im Auftrag des Bundesamt für Strahlenschutz in den Jahren 2013
bis 2015.

Kontakt
-------
Bundesamt für Strahlenschutz
SW2 Notfallschutz, Zentralstelle des Bundes (ZdB)
Willy-Brandt-Strasse 5
38226 Salzgitter
info@bfs.de

Lizenz
------
Die Software ist unter der GNU GPL v>=3 Lizenz verfügbar.
Details siehe die Datei `COPYING`.

Quelltext
---------
Die Quelldateien lassen sich wie folgt auschecken:
```
hg clone https://scm.wald.intevation.org/hg/lada/lada-server
```

Entwicklung
-----------
Für die Entwicklung wird ein JDK7 und maven3 oder höher benötigt. Sämtliche
Abhängigkeiten werden von dem maven build System aufgelöst.

Installation
------------
Die Installation des Lada-Servers erfolgt in einem Wildfly-Application-Server
(http://wildfly.org). Dazu müssen folgende Schritte unternommen werden:

 $ mvn clean compile package
 $ mv target/lada-server-$VERSION.war $JBOSS_HOME/standalone/deployments
 $ touch $JBOSS_HOME/standalone/deployments/lada-server-$VERSION.war.dodeploy

$JBOSS_HOME ist hierbei durch den Pfad zur Wildfly-Installation zu ersetzen,
$VERSION durch die aktuelle Versionsbezeichnung (entsprechend der Angabe in
pom.xml).

Zum Aktualisieren der Anwendung genügt es, das WAR-Archiv zu aktualisieren.

Die Anwendung ist dann unter dem Pfad "/lada-server-$VERSION" erreichbar.

Um zu garantieren, dass die von den REST-Schnittstellen ausgelieferten
Zeitstempel sich korrekt auf UTC beziehen, muss die entsprechende System-
Property `user.timezone=UTC` vor dem Start des Application-Servers gesetzt
werden (siehe `wildfly/standalone.conf`).

Das PostgreSQL-Datenbank-Backend des Lada-Servers kann als Nutzer `postgres`
(bzw. als PostgreSQL-Superuser) mit dem Skript `db_schema/setup-db.sh`
eingerichtet werden.

Details zur Installation können den Dateien `Dockerfile` und
`db_schema/Dockerfile` entnommen werden.

Docker
------
Um schnell und automatisiert ein Entwicklungs-Setup für LADA aufsetzen zu
können, werden Dockerfiles mitgeliefert. Voraussetzung für die Anwendung ist
eine Docker-Installation. Folgendes Vorgehen führt zu einem
Vollständigen Setup inklusive LADA-Client, in dem jeweils der auf dem Host
vorhandene Quellcode in die Container gemounted wird, so dass auf dem Host
durchgeführte Änderungen leicht innerhalb der Container getestet werden können.

Bauen der Images:
 $ cd ./db_schema
 $ docker build -t koala/lada_db .
 $ cd ..
 $ docker build -t koala/lada_wildfly .
 $ cd your/repo/of/lada-client
 $ docker build -t koala/lada_client .

Aufbau eines Netzwerks für die LADA-Komponenten:
 $ docker network create lada_network

Starten der Container:
 $ cd db_schema
 $ docker run --name your_lada_db --net=lada_network -v $PWD:/opt/lada_sql/ \
          -d koala/lada_db:latest
 $ cd ..
 $ docker run --name lada_wildfly --net=lada_network \
          --link your_lada_db:lada_db -v $PWD:/usr/src/lada-server \
          -d koala/lada_wildfly
 $ cd your/repo/of/lada-client
 $ docker run --name lada_client --net=lada_network \
              -v $PWD:/var/www/html/ \
              --link lada_wildfly:lada-server \
              -p 8180-8184:80-84 -d koala/lada_client

Die LADA-Anwendung kann dann unter den angegebenen Ports mit verschiedenen
Rollen im Browser ausgeführt werden.

Tests
-----
Die auf Arquillian basierenden Tests erfordern einen vollständig konfigurierten
und gestarteten Wildfly Application-Server, da für die Schnittstellentest eine
Clientanwendung simuliert wird und HTTP-Requests ausgeführt werden.

Das Ausführen der Tests erfolgt durch das Kommando

 $ mvn -Premote-test clean test

und benötigt eine leere Datenbank, die z.B. mit

 $ ./setup-db.sh -cn

angelegt werden kann.

Dokumenation
------------
Die Entwicklerdokumentation (Javadoc) kann mit dem folgenden Befehl im
Verzeichnis der Serveranwendung erzeugt werden:

 $ mvn javadoc:javadoc

Der Ordner 'target' enthält dann die Dokumentation im HTML-Format in dem
Verzeichnis 'site/apidocs'.

Erstellen von Queries
---------------------
Queries können als SQL-Statement in der Tabelle stammdaten.queries definiert
werden. Eine Filterung kann über Variablen erfolgen, die in stammdaten.filter
definiert werden müssen und mittels SQL-Interpolation im SQL-Statement
verwendet werden können.
Um neue Queries für die Suche von Proben, Messungen und Messprogrammen zu
erstellen sind die folgenden Schritte erforderlich:

1. In der Tabelle 'stammdaten.query' einen neuen Eintrag erzeugen.
   * id: Primary-Key (wird generiert)
   * name: Der Name der Query
   * type: Der Datentyp der gefiltert werden soll.
     (mögliche Werte siehe Datenbank-Schema-Definition)
   * sql: Das auszuführende SQL-Statement (siehe #Regeln für die Syntax)
   * description: Ein beschreibender Text

2. In der Tabelle 'stammdaten.result' für die anzuzeigenden Felder je einen
   Eintrag erzeugen:
   * id: Primary-Key (wird generiert)
   * query_id: ID der zugehörigen und in Schritt 1. erzeugten Query
   * data_index: Name des Feldes zur Übertragung an den Client (in CamelCase)
   * header: Der Titel der Spalte für diesen Eintrag
   * width: Die Spaltenbreite (in Pixel)
   * flex: Dynamische Spaltenbreite (true/false)
   * index: Der Datenindex

3. In der Tabelle 'stammdaten.filter' für jeden Parameter in der 'WHERE'-Clause
   der Query einen Eintrag erzeugen:
   * id: Primary-Key (wird generiert)
   * query_id: ID der zugehörigen und in Schritt 1. erzeugten Query
   * data_index: Der Name der Variablen, die in dem 'WHERE'-Statement ersetzt
     werden soll
   * type: Datenbasis, die im Client als Eingabe genutzt werden soll
     (mögliche Werte siehe Datenbank-Schema-Definition)
   * label: Der angezeigte Name des Filters
   * multiselect: Mehrfachangabe von Werten für diesen Filter (true/false)

### Regeln

* Bei Queries vom Typ `probe` muss das erste selektierte Feld `probe.id` sein.
  Dieses wird in der Oberfläche nicht angezeigt.
* Bei Queries vom Typ `messung` muss das erste selektierte Feld `messung.id`
  und das zweite `probe.id` sein. Diese werden in der Oberfläche nicht
  angezeigt. Für `probe.id` muss in stammdaten.result ein Eintrag mit
  `data_index = 'probeId'` angelegt werden (obwohl diese Spalte nicht angezeigt
  wird). Um im Client die Funktionalität zu erhalten, sollten Messungsfilter
  die beiden Felder `probe.hauptproben_nr` und `messung.nebenproben_nr`
  enthalten.
* Bei Queries vom Typ `messprogramm` muss das erste selektierte Feld
  `messprogramm.id` sein. Dieses wird in der Oberfläche nicht angezeigt.
* Werden bei einem JOIN Spalten gleichen Namens aus verschiedenen Tabellen
  in der SELECT-Clause verwendet, so müssen diese mit einem expliziten Alias
  versehen werden, um eine
  org.hibernate.loader.custom.NonUniqueDiscoveredSqlAliasException zu
  vermeiden.
* Im `WHERE`-Statement genutzte Variablen müssen in der Form `:variablenName`
  angegeben werden und dem Feld `data_index` im zugehörigen Filter entsprechen.
* Wenn ein Filter mit `multiselect = true` angegeben wird, so wird in der
  `WHERE`-Clause ein `SIMILAR TO` erwartet.
* Das Feld `index` in der Tabelle `stammdaten.result` dient zur Zuordnung des
  selektierten Datenfeldes zu dem Entsprechenden Eintrag in der Tabelle
  `stammdaten.result`. Beispiel:
```
    'SELECT probe.id, probe.mst_id AS mstId, probe.hauptproben_nr AS hpNr, ...'
                      |----- index 1 -----|  |--------- index 2 --------|
    Wird in der Tabelle 'stammdaten.result' zu:
    Result 1:
    ...
        data_index: mstId
        header: Messstelle
        width: 100
        flex: false
        index: 1
    ...
    Result 2:
    ...
        data_index: hpNr
        header: Hauptproben Nr
        width: 150
        flex: false
        index: 2
    ....
```
* Queries für Stammdaten werden gesondert behandelt und beinhalten keine
  SQL-Statements. Dementsprechend können auch keine Einträge für Ergebnisse in
  der Tabelle `stammdaten.result` gemacht werden. Filter können allerdings,
  unter der Bedingung, dass `data_index` auf einen in dem Datentyp vorhandenes
  und in CamelCase geschriebenes Datenfeld zeigt, angelegt werden.
  Momentan sind Queries für die folgenden Stammdaten möglich:
   * Orte
   * Probennehmer
   * Datensatzerzeuger
   * Messprogrammkategorien
