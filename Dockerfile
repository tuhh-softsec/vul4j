FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8

# install required softwares
RUN apt update \
    && apt install -y wget curl git python3 python3-pip vim zsh unzip bzip2 xz-utils \
    openjdk-8-jdk openjdk-11-jdk openjdk-13-jdk openjdk-16-jdk \
    openssh-client patch software-properties-common time build-essential \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh || true

RUN wget -P /opt/ https://dlcdn.apache.org/maven/maven-3/3.8.8/binaries/apache-maven-3.8.8-bin.zip; \
    unzip /opt/apache-maven-3.8.8-bin.zip -d /opt/; \
    ln -s /opt/apache-maven-3.8.8/bin/mvn /usr/local/bin/; \
    rm /opt/apache-maven-3.8.8-bin.zip

COPY ./maven_conf/settings.xml /root/.m2/settings.xml

COPY ./ /vul4j/

WORKDIR /vul4j

RUN python3 setup.py install

RUN vul4j get-spotbugs

# jdk7 downloaded from https://www.oracle.com/java/technologies/javase/javase7-archive-downloads.html
RUN tar xvzf jdk-7u80-linux-x64.tar.gz -C /tmp/
RUN mv /tmp/jdk1.7.0_80 /usr/lib/jvm/jdk1.7.0_80/
RUN rm jdk-7u80-linux-x64.tar.gz

# set env
ENV JAVA7_HOME /usr/lib/jvm/jdk1.7.0_80
ENV JAVA8_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV JAVA11_HOME /usr/lib/jvm/java-11-openjdk-amd64
ENV JAVA13_HOME /usr/lib/jvm/java-13-openjdk-amd64
ENV JAVA16_HOME /usr/lib/jvm/java-16-openjdk-amd64

WORKDIR /
