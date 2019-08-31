echo off

set M2_REPO=C:/Users/Siobhan/.m2/repository

set CLASSPATH=.;%CLASSPATH%;%M2_REPO%/pacemaker/pacemaker-console-maven/1.0/pacemaker-console-maven-1.0.jar;%M2_REPO%/com/thoughtworks/xstream/xstream/1.4.10/xstream-1.4.10.jar;%M2_REPO%/com/google/guava/guava/23.0/guava-23.0.jar;%M2_REPO%/com/googlecode/clichemaven/cliche/110413/cliche-110413.jar;%M2_REPO%/java-ascii-table/java-ascii-table/1.0/java-ascii-table-1.0.jar;%M2_REPO%/joda-time/joda-time/1.0/joda-time-2.9.9.jar;

java controllers.Main