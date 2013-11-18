#!/usr/bin/env python

import sys
from os import path
from xml.dom import minidom

# Location of the .classpath file relative to this script
classpath_filename = "../.classpath"
m2_repository = "~/.m2/repository"

def parse_classpath_xml(classpath_file, abs_m2_repository):
    xmldoc = minidom.parse(classpath_file)
    classpathentries = xmldoc.getElementsByTagName('classpathentry')
    classpath = ""
    for entry in classpathentries:
        kind = entry.attributes['kind'].value
        if kind == "output" or kind == "var":
            cp_entry = entry.attributes['path'].value + ":"
            
            classpath += cp_entry.replace("M2_REPO", abs_m2_repository)

    print classpath[0:-1]

if __name__ == "__main__":    
    abs_m2_repository = path.expanduser("~/.m2/repository")

    classpath_file = path.abspath(path.join(path.dirname(path.realpath(sys.argv[0])), classpath_filename))

    try:
        with open(classpath_file) as f:
            parse_classpath_xml(f, abs_m2_repository)
    except IOError:
        print "Error reading classpath file from %s" % classpath_file
        print "- Please check path is correct then run 'mvn eclipse:eclipse' to generate file"
