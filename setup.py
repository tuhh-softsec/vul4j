from setuptools import setup

setup(
    name='vul4j',
    version=1.0,
    description='Vul4J: A Dataset of Reproducible Java Vulnerabilities.',
    author='Quang-Cuong Bui',
    author_email='cuong.bui@tuhh.de',
    url='https://github.com/bqcuong/vul4j',
    license='MIT',
    packages=['vul4j'],
    install_requires=['unidiff'],
    entry_points="""
            [console_scripts]
            vul4j = vul4j.main:main
        """,
)
