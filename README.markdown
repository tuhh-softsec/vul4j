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
