#!/usr/bin/env python
# -*- coding: utf-8 -*-

import os
from waflib import Task, TaskGen, Utils, Node
from waflib.TaskGen import feature, before, after
from waflib.Configure import conf


JUNIT_RUNNER = 'org.junit.runner.JUnitCore'


@feature('junit')
@after('apply_java', 'use_javac_files')
def compile_junit_tests_and_run_them(self):
    if not hasattr(self, 'srcdir_test'):
        return
    if not hasattr(self, 'outdir_test'):
        return
    if not hasattr(self, 'sourcepath_test'):
        return
    if not hasattr(self, 'classpath_test'):
        return

    srcdir_test = []
    for x in Utils.to_list(getattr(self, 'srcdir_test', '')):
        if isinstance(x, Node.Node):
            y = x
        else:
            y = self.path.find_dir(x)
            if not y:
                self.bld.fatal('Could not find the folder %s from %s' % (x, self.path))
        srcdir_test.append(y)
    self.srcdir_test = srcdir_test

    outdir_test = getattr(self, 'outdir_test')
    if not isinstance(outdir_test, Node.Node):
        outdir_test = self.path.get_bld().make_node(self.outdir_test)
    outdir_test.mkdir()
    self.outdir_test = outdir_test.abspath()

    nodes = [isinstance(x, Node.Node) and x or self.path.find_dir(x) for x in self.to_list(self.sourcepath_test)]
    sourcepath_test = os.pathsep.join([x.srcpath() for x in nodes])
    self.sourcepath_test = sourcepath_test

    classpath_test = os.pathsep.join(self.to_list(self.classpath_test)) + os.pathsep
    self.classpath_test = classpath_test

    compile_junit_test_task = self.create_task('compile_junit_test')
    run_junit_test_task = self.create_task('run_junit_test')
    try:
        compile_junit_test_task.set_run_after(self.javac_task)
        run_junit_test_task.set_run_after(compile_junit_test_task)
    except:
        pass


class compile_junit_test(Task.Task):
    color = 'BLUE'

    def runnable_status(self):
        for t in self.run_after:
            if not t.hasrun:
                return Task.ASK_LATER

        self.srcdir_test = self.generator.srcdir_test
        self.outdir_test = self.generator.outdir_test
        self.sourcepath_test = self.generator.sourcepath_test
        self.classpath_test = self.generator.classpath_test

        n = self.generator.path.find_dir(self.sourcepath_test)
        if not n:
            self.generator.bld.fatal('Could not find the sourcepath_test dir %r' % self.generator.sourcepath_test)
        self.base = n
        self.inputs = n.ant_glob('**/*.java')

        return super(compile_junit_test, self).runnable_status()

    def run(self):
        cmd = []
        cmd.extend(self.env.JAVAC)
        cmd.append('-classpath')
        cmd.append(
                self.generator.javac_task.env.CLASSPATH
                + os.pathsep
                + self.generator.javac_task.env.OUTDIR
                + os.pathsep
                + self.classpath_test
        )
        cmd.extend(['-d'])
        cmd.append(self.outdir_test)
        cmd.extend(['-sourcepath'])
        cmd.append(self.sourcepath_test)
        cmd.extend([x.path_from(self.generator.bld.bldnode) for x in self.inputs])
        return self.exec_command(cmd)


class run_junit_test(Task.Task):
    color = 'YELLOW'

    def runnable_status(self):
        for t in self.run_after:
            if not t.hasrun:
                return Task.ASK_LATER

        n = self.generator.path.find_dir(self.generator.sourcepath_test)
        if not n:
            self.generator.bld.fatal('Could not find the sourcepath_test dir %r' % self.generator.sourcepath_test)
        self.base = n
        self.inputs = n.ant_glob('**/*.java')

        return super(run_junit_test, self).runnable_status()

    def run(self):
        cmd = []
        cmd.extend(self.env.JAVA)
        cmd.append('-classpath')
        cmd.append(
                self.generator.javac_task.env.CLASSPATH
                + os.pathsep
                + self.generator.javac_task.env.OUTDIR
                + os.pathsep
                + self.generator.classpath_test
                + os.pathsep
                + self.generator.outdir_test
        )
        cmd.append(JUNIT_RUNNER)
        cmd.extend([x.path_from(self.base).replace('.java', '').replace(os.sep, '.') for x in self.inputs])
        return self.exec_command(cmd)
