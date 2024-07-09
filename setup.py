import os
import shutil

from setuptools import setup
from setuptools.command.install import install


class Vul4JConfigure(install):
    user_options = install.user_options + [
        ("location=", None, "Specify location for vul4j data directory (default: ~/vul4j_data).")
    ]

    def initialize_options(self):
        install.initialize_options(self)
        self.location = None

    def finalize_options(self):
        install.finalize_options(self)
        if self.location is None:
            self.location = os.path.expanduser("~/vul4j_data")
        if os.path.exists(self.location):
            print(f"ERROR: Directory already exists: {self.location}")
            exit(1)
        os.environ["VUL4J_DATA"] = self.location

    def run(self):
        os.makedirs(self.location, exist_ok=True)
        shutil.copy(os.path.join("vul4j", "vul4j.ini"), self.location)
        print(f"Data directory and files setup at: {self.location}")

        install.run(self)


setup(
    name='vul4j',
    version='2.0',
    description='Vul4J: A Dataset of Reproducible Java Vulnerabilities',
    author='Quang-Cuong Bui, Bence Bogenfuerst',
    author_email='cuong.bui@tuhh.de, bogen@inf.u-szeged.hu',
    url='https://github.com/bqcuong/vul4j',
    license='MIT',
    packages=['vul4j'],
    install_requires=[
        'unidiff',
        'loguru',
        'GitPython'
    ],
    cmdclass={
        'install': Vul4JConfigure
    },
    entry_points={
        'console_scripts': ['vul4j = vul4j.main:main']
    },
)
