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
git clone https://github.com/OpenBfS/lada-server.git
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

### Transformation von Ortskoordinaten

Die Transformation von Koordinaten aus dem CRS `EPSG:3146[6,7,8,9]` in das für intern
genutzte Geometrien CRS `EPSG:4326` kann optional mit einem ShiftGrid erfolgen.
Dies erhöht die Genauigkeit der resultierenden Koordinaten.
Das ShiftGrid ist dazu vor dem Compilieren (s.o.) folgendermaßen zu einzufügen:

 $ curl -O http://crs.bkg.bund.de/crseu/crs/descrtrans/BeTA/BETA2007.gsb
 $ mkdir -p src/main/resources/org/geotools/referencing/factory/gridshift
 $ mv BETA2007.gsb src/main/resources/org/geotools/referencing/factory/gridshift

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
              -v $PWD:/usr/local/apache2/htdocs \
              --link lada_wildfly:lada-server \
              -p 8180-8184:80-84 -d koala/lada_client

Innerhalb des Client-Containers muss dann noch folgendes ausgeführt werden,
wenn zum ersten mal your/repo/of/lada-client als Volume in einen Container
eingebunden wurde:

 $ ./install-sencha2opt.sh
 $ ./install-dependencies.sh
 $ ln -s $PWD/ext-6.2.0 ext
 $ sencha app install --framework=ext
 $ sencha app build development

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

Basequeries enthalten die grundlegenden Definitionen für Abfragen. Diese werden
fest in der Datenbank vorgegeben und sind in der Tabelle stamm.base_query definiert.
Die SQL-Abfrage in der Tabelle muss zumindest das SELECT- und FROM-Statement enthalten.
Den Ergebnisspalten der Abfrage sollte zudem mithilfe des AS-Ausdrucks ein Alias zugewiesen werden.
Der Spaltenname 'extjs_id' wird intern vom Client genutzt und sollte nicht vergeben werden.

Der Basequery zugeordnete Spalten werden zusätzlich in der Tabelle
stamm.grid_column festgelegt, wobei der gegebene DataIndex einem Alias der
zugeordneten Basequery entsprechen sollte. Der Datentyp data_type bestimmt
das Verhalten des Clients und den dort angezeigten Filterwidgets mit (siehe
unten). Die Position gibt die Stellung innerhalb der Basequery an, name ist die
im Ergebnisgrid anzuzeigende Spaltenbeschriftung.

Die Spalte filter innerhalb einer stamm.grid_column verweist auf einen Eintrag in der Tabelle stamm.filter.
Diese enthält Filter-Typ, das entsprechende SQL-Statement und den Namen des Parameters.
Neben einfachen Text-, Zahlen- oder boolschen- Filtern existieren auch Filter-Typen für von-bis-Datums-Filter, Multiselect-Filter und generische Text-Filter. Multiselect- und Datums-Filter akzeptieren dabei einen String mit Komma-separierten Werten.
Für die Definition der Filter mit SQL-Statement und Paramter gilt:
  * Datums-Filter: 2 Parameter. Beispielsweise:
    * SQL: probe.probeentnahme_beginn BETWEEN :fromTime AND :toTime
    * Spalte "Parameter": fromTime,toTime
  * Generischer Filter: 1 Parameter. Beispielsweise:
    * SQL: :genTextParam LIKE :genTextValue
    * Spalte "Parameter": genText
  * Sonst: 1 Parameter. Beispielsweise:
    * SQL: probe.id_alt LIKE :idAlt
    * Spalte "Parameter": idAlt

Einzelne Nutzer können aus bereits bestehenden Queries Kopien erstellen.
Hierfür gibt es zwei Speicherorte: In query_user werden die grundsätzlichen
Parameter festgelegt, wie etwa eine eigene Beschreibung oder ein eigener Namen der kopierten Query.
In grid_column_values werden die Definitionen der
einzelnen Spalten (z.B. Sichtbarkeit, derzeitig gespeicherter Filter) persistiert.

### Datentypen

Den einzelnen Spalten können verschiedene Datentypen zugeordnet werden. Dies
dient im Client zum einen der Darstellung von passenden Filtern und Spalten.
Einige Datentypen bieten zusätzliche Funktionalität.

Der Gesamttype eines Ergebnisgrids leitet sich ebenfalls aus Datentypen in der
Basequery ab, welche Datenbankeinträge eindeutig identifizieren.
Hierbei existiert eine Hierarchie: weiter oben stehende Elemente ersetzen
das weiter unten stehende.

Datentypen mit ID-Funktionalität, in absteigender Hierarchie:
  1. 'messungId' - Zeile enthält eine Messung
  2. 'probeId' - Zeile enthält eine Probe
  3. 'mpId' - Zeile enthält ein Messprogramm
  4. 'ortId' - Zeile enthält einen (Stammdaten-)Ort
  5. 'pnehmer' - Zeile enthält einen Probenehmer
  6. 'dsatzerz'- Zeile enthält einen Datensatzerzeuger
  7. 'mprkat'- Zeile enthält eine Messprogrammkategorie

Diese Datentypen sollten jeweils eine Datenbank-ID enthalten. Mehrere IDs von
verschiedenen Typen sind zulässig, und sind dann im Grid direkt auswähl- und
gegebenenfalls in eigenen Dialogen bearbeitbar.

Das Verhalten der 'Hinzufügen/Löschen' - Buttons und des Doppelklicks auf eine
Zeile richtet sich jeweils nach dem Datentyp mit der höchsten Hierarchiestufe.
(Beispiel: In einer Abfrage mit messungId, pnehmer und probeId können -bei
Berechtigung- alle drei Elemente bearbeitet werden. Die höchste Hierarchieebene
ist hier Messung, weshalb ein "Löschen" für die entsprechende Messung einer
Zeile gilt. Der 'Hinzufügen'- Button ist nicht verfügbar, da eine Messung nur
aus dem Kontext einer Probe hinzugefügt werden kann, und Proben in diesem Grid
nicht eindeutig sind)

Um neue Queries für die Suche von Proben, Messungen und Messprogrammen zu
erstellen sind die folgenden Schritte erforderlich:

### Sonderfälle in Datentypen

Für einige in stamm.column definerten möglichen Datentypen erwartet der
Client spezielle Angaben:

* Resultate mit Geometrien (Typ 'geom') werden als GeoJSON erwartet. Hierfür
  kann die postgis- Funktion 'st_asgeojson' genutzt werden:
```
  'SELECT ST_ASGEOJSON(geom) AS geometrie FROM stamm.ort;'
```

* Resultate für Zahlen können in E-Notation erzwungen werden, wenn im Tabelle
  stamm.result_type das Format auf 'e' gesetzt wird.


Erstellen von Importerkonfigurationen
-------------------------------------

Konfigurationen für den Importer enthalten drei Typen von Aktionen, die auf die
zu importierenden Daten angewendet werden, bevor die Daten in die Datenbank
geschrieben werden:
1. "default": Standardwerte, die leere oder fehlende Angaben ergänzen
2. "convert": Datenumwandlungen, die einen Ersatz von vorhandenen Daten
   darstellen
3. "transform": Zeichenumwandlung, die einzelne Zeichen eines Wertes ändern

Eine Konfiguration wird in der Datenbanktabelle 'importer_config' im Schema
"stammdaten" angelegt und hat die folgenden Felder:

* id (serial): Primary Key
* name (character varying(30)): Name der Datenbank-Tabelle,
  z.B. bei einer Probe "probe". Die Zeitbasis hat den Namen "zeitbasis".
* attribute (character varying(30)): Name des Attributes das bearbeitet werden
  soll in CamelCase-Schreibweise. (Zeitbasis hat hier einen "dummy"-Eintrag)
  Tabellenspalten, die als Foreign-Key auf andere Tabellen verweisen, werden mit
  dem Tabellennamen referenziert und können so im Falle der Aktion 'convert' mit
  den sprechenden Bezeichnung genutzt werden.
* mst_id (Foreign-Key auf mess_stelle): Enthält die Messstelle, für die diese
  Konfiguration gültig ist.
* from_value (character varying(100)): Für "default" bleibt diese Spalte leer,
  für "convert" und "transform" enthält diese Spalte den Ursprungswert.
* to_value (character varying(100)): Enthält den Zielwert der Konfiguration
* action (character varying(20)): Enthält eine der drei Aktionen als Text:
  "default", "convert" oder "transform"

Die Transformation im speziellen enthält in "from_value" und "to_value" die
hexadezimale Darstellung eines Zeichen in Unicode. Also z.B. für "+" den
Wert "2b", für "#" den Wert "23".
