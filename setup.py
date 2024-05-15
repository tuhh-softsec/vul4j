import os
import shutil
from setuptools import setup
from setuptools.command.install import install


class CustomInstall(install):
    def run(self):
        shutil.copy(os.path.join("vul4j", "vul4j.ini"), os.path.expanduser("~"))
        install.run(self)


setup(
    name='vul4j',
    version='2.0',
    description='Vul4J: A Dataset of Reproducible Java Vulnerabilities.',
    author='Quang-Cuong Bui',
    author_email='cuong.bui@tuhh.de',
    url='https://github.com/bqcuong/vul4j',
    license='MIT',
    packages=['vul4j'],
    install_requires=['unidiff', 'loguru', 'GitPython'],
    cmdclass={"install": CustomInstall},
    entry_points="""
            [console_scripts]
            vul4j = vul4j.main:main
        """,
)
