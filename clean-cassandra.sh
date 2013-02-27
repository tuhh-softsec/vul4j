#! /usr/bin/expect
set timeout 5
spawn ~/apache-cassandra-1.1.4/bin/cassandra-cli
expect "\[default\@unknown\]"
send "drop onos;\r"
expect "\[default@unknown\]"
send "quit;\r"
