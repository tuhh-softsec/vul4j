from setuptools import setup, find_packages

setup(
    name='vul4j',
    version=1.0,
    description='A benchmark of Java vulnerabilities to enable controlled research studies for testing and debugging.',
    author='Quang-Cuong Bui',
    author_email='cuong.bui@tuhh.de',
    url='https://github.com/bqcuong/vul4j',
    license='MIT',
    packages=['vul4j'],
    entry_points="""
            [console_scripts]
            vul4j = vul4j.main:main
        """,
)