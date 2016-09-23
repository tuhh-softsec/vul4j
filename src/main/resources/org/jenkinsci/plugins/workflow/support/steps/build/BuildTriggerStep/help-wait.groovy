p(raw('''
    You may ask that this Pipeline build wait for completion of the downstream build.
    In that case the return value of the step is an object on which you can obtain the following read-only properties:
    so you can inspect its <code>.result</code> and so on.
'''))
raw(org.jenkinsci.plugins.workflow.support.steps.build.RunWrapper.class.getResource("RunWrapper/help.html").text)
p('''
    If you do not wait, this step succeeds so long as the downstream build can be added to the queue (it will not even have been started).
    In that case there is currently no return value.
''')
