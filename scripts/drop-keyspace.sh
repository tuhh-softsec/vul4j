#! /usr/bin/expect -f
set timeout 5
spawn ~/apache-cassandra-1.1.4/bin/cassandra-cli
expect "unknown\]\ "
send "drop keyspace onos;\n"
expect "unknown\]\ "
send "quit;\n"
