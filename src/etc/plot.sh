#!/bin/bash

SCRIPTDIR=$(dirname ${0})
TARGETDIR="${SCRIPTDIR}/../../target"

export GDFONTPATH=/usr/share/fonts/corefonts

gnuplot << EOF

set term png size 800,400
set key left top
set xlabel "Number of XML start elements"
set ylabel "Time [s]"
set style data linespoints

set output "${TARGETDIR}/signature-times-inbound.png"
set title "Time needed for signature verification"
plot "${TARGETDIR}/signatureInTimeSamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/signatureInTimeSamples.txt" using 1:3 title 'DOM'

set output "${TARGETDIR}/signature-times-outbound.png"
set title "Time needed for signature creation"
plot "${TARGETDIR}/signatureOutTimeSamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/signatureOutTimeSamples.txt" using 1:3 title 'DOM'

set output "${TARGETDIR}/encryption-times-inbound.png"
set title "Time needed for decryption"
plot "${TARGETDIR}/encryptionInTimeSamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/encryptionInTimeSamples.txt" using 1:3 title 'DOM'

set output "${TARGETDIR}/encryption-times-outbound.png"
set title "Time needed for encryption"
plot "${TARGETDIR}/encryptionOutTimeSamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/encryptionOutTimeSamples.txt" using 1:3 title 'DOM'


set ylabel "Memory [MB]"

set output "${TARGETDIR}/signature-memory-inbound.png"
set title "HEAP memory consumption during signature verification"
plot "${TARGETDIR}/signatureInMemorySamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/signatureInMemorySamples.txt" using 1:3 title 'DOM'

set output "${TARGETDIR}/signature-memory-outbound.png"
set title "HEAP memory consumption during signature creation"
plot "${TARGETDIR}/signatureOutMemorySamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/signatureOutMemorySamples.txt" using 1:3 title 'DOM'

set output "${TARGETDIR}/encryption-memory-inbound.png"
set title "HEAP memory consumption during decryption"
plot "${TARGETDIR}/encryptionInMemorySamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/encryptionInMemorySamples.txt" using 1:3 title 'DOM'

set output "${TARGETDIR}/encryption-memory-outbound.png"
set title "HEAP memory consumption during encryption"
plot "${TARGETDIR}/encryptionOutMemorySamples.txt" using 1:2 title 'StAX', \
     "${TARGETDIR}/encryptionOutMemorySamples.txt" using 1:3 title 'DOM'

EOF
