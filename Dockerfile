FROM ubuntu:20.04

ENV DEBIAN_FRONTEND=noninteractive
ENV LANG=C.UTF-8

# install required softwares
RUN apt update \
    && apt install -y wget curl git python3 python3-pip vim zsh unzip bzip2 xz-utils \
    openjdk-8-jdk maven \
    openssh-client patch software-properties-common time build-essential \
    && rm -rf /var/lib/apt/lists/*

RUN wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh || true

COPY ./ /vul4j/

WORKDIR /vul4j

RUN pip3 install -r requirements.txt
RUN pip3 install .

# jdk7 downloaded from https://www.oracle.com/java/technologies/javase/javase7-archive-downloads.html
COPY jdk-7u80-linux-x64.tar.gz /tmp/jdk-7u80-linux-x64.tar.gz
RUN tar xvzf /tmp/jdk-7u80-linux-x64.tar.gz -C /tmp/
RUN mv /tmp/jdk1.7.0_80 /usr/lib/jvm/jdk1.7.0_80/
RUN rm jdk-7u80-linux-x64.tar.gz

# set env
ENV JAVA7_HOME /usr/lib/jvm/jdk1.7.0_80
ENV JAVA8_HOME /usr/lib/jvm/java-8-openjdk-amd64
ENV BENCHMARK_PATH /vul4j/benchmark_repo
ENV DATASET_PATH /vul4j/dataset/vul4j_dataset.csv
ENV GZOLTAR_RUNNER_PATH /vul4j/gzoltar_runner
ENV REPRODUCTION_DIR /vul4j/reproduction
ENV PROJECT_REPOS_ROOT_PATH /vul4j/project_repos

