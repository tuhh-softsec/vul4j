flaky-test-handler-plugin
=========================
This plugin is used to provide various support for handling flaky tests. It currently supports for Git and Maven.
It includes support for the latest version of the Maven surefire plug-in which produces additional data about test 
flakiness using the new "rerunFailingTestsCount" option. It also supports re-running  only failed tests for a 
failed build at the exact failed Git revision. 

Finally it aggregates statistics of tests (passes, fails and flakes) over Git revisions.
