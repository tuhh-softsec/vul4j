#!/bin/sh

# Copying bootstrap.sh to the installer (and replacing the @lines@ variable)
lines=`wc -l < bootstrap.sh`
lines=`expr $lines + 1`
sed -e "s/@LINES@/${lines}/" bootstrap.sh > ../${finalName}

# Packing the data and shell scripts
tar czf ${tmpArchive} root/* sh/*

# Adding the temp archive to the installer
cat ${tmpArchive} >> ../${finalName}

# Modifying permission on the installer so it is executable
chmod 750 ../${finalName}

# Cleaning
rm ${tmpArchive}