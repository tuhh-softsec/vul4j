import csv
import requests as req

with open('vulas_db_msr2019_release.csv', 'r') as file:
    csv_reader = csv.reader(file, delimiter=',')
    next(csv_reader)  # skip header
    for row in csv_reader:
        commit_hash = row[2]
        project_slug = row[1].split('/')[-2] + '/' + row[1].split('/')[-1]
        pom_xml = f"https://raw.githubusercontent.com/{project_slug}/{commit_hash}/pom.xml"
        build_gradle = f"https://raw.githubusercontent.com/{project_slug}/{commit_hash}/build.gradle"

        build_system = "Unknown"
        try:
            resp = req.get(pom_xml)
            if resp.status_code == 200:
                build_system = "Maven"
            else:
                resp = req.get(build_gradle)
                if resp.status_code == 200:
                    build_system = "Gradle"
        except:
            pass

        print(build_system)
