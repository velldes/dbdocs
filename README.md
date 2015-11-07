## Introduction

Creates javadoc-style HTML documentation for database schema objects or/and user defined objects.

**DbDoc** web application.
**DbDocCmd** command line application (see, example).

## Usage

Create properties file similar to oracle_sample.properties and start application using properties file name.
If owner differs form login user, oracle_all.sql file should be used and owner specified in properties file.

## Implemented databases
<ul>
    <li>Oracle</li>
</ul>
Note: to implement other databases, I hope, it is only needed to create SQL file similar to oracle.sql

## Implemented following schema objects
<ul>
    <li>function</li>
    <li>package</li>
    <li>procedure</li>
    <li>queue</li>
    <li>scheduler</li>
    <li>sequence</li>
    <li>table</li>
    <li>trigger</li>
    <li>type</li>
</ul>
## Rules to document packages
<ul>
  <li>
    Default mode
    <ul>
      <li>
        Comments between slash and two asterisks ( /** ) and an asterisk and a slash ( */ ) treated as documentation. 
      </li>
    </ul>
  </li>
  <li>
    AllCommentsAsDoc? mode
    <ul>
      <li>
        All comments treated as documentation ( Comments between slash and an asterisk and comments that begin with -- (two hyphens) ).
      </li>
      <li>
        Empty line commented with two hyphens separates documentation blocks. 
      </li>
    </ul>
  </li>
</ul>
Project Table API Generator for Oracle has example of documented package.

## User defined objects

User defined objects (UDO) are the tables, data from which should be documented. It can be application constants, settings, etc. To create UDO it is needed to change SQL file and add new object to properties file (see example below) and screenshots.

SQL file
```
-- OBJECTS add AND (...) to filter rows
OBJECTS==SELECT object_name FROM (
SELECT object_name
FROM user_objects
WHERE ( object_name NOT LIKE '%$%' AND object_name NOT LIKE '%#%' )
  AND object_type = :type
  #NAME#
UNION
SELECT 'APUSERS' object_name FROM DUAL
)
ORDER BY object_name
-- APUSERS
APUSERS==SELECT *
FROM APUSERS
ORDER BY id
...
```

Properties file
```
Objects          = FUNCTION,PACKAGE,PROCEDURE,QUEUE,SCHEDULER,SEQUENCE,TABLE,TRIGGER,TYPE,APUSERS
```
