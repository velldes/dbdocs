## Introduction ##
Creates javadoc-style HTML documentation for database schema objects or/and user defined objects.

**DbDoc** web application.<br>
<b>DbDocCmd</b> command line application (see, <a href='http://dbdocs.googlecode.com/svn/trunk/DbDocCmd/dbdoccmd_doc_example.zip'>example</a>).<br>
<br>
<a href='https://www.paypal.com/cgi-bin/webscr?cmd=_donations&business=T8PSGE58F34MC&lc=CZ&item_number=dbdocs&currency_code=EUR&bn=PP%2dDonationsBF%3abtn_donateCC_LG%2egif%3aNonHosted'><img src='https://www.paypalobjects.com/en_US/i/btn/btn_donateCC_LG.gif' /></a>

<h2>Usage</h2>
Create properties file similar to <a href='http://code.google.com/p/dbdocs/source/browse/trunk/DbDoc/res/props/oracle_sample.properties'>oracle_sample.properties</a> and start application using properties file name.<br>
<br>
If owner differs form login user, <a href='http://code.google.com/p/dbdocs/source/browse/trunk/DbDoc/res/sql/oracle_all.sql'>oracle_all.sql</a> file should be used and owner specified in properties file.<br>
<br>
<h2>Implemented databases</h2>
<ul><li>Oracle<br>
Note: to implement other databases, I hope, it is only needed to create SQL file similar to<br>
<a href='http://code.google.com/p/dbdocs/source/browse/trunk/DbDoc/res/sql/oracle.sql'>oracle.sql</a></li></ul>

<h2>Implemented following schema objects</h2>
<ul><li>function<br>
</li><li>package<br>
</li><li>procedure<br>
</li><li>queue<br>
</li><li>scheduler<br>
</li><li>sequence<br>
</li><li>table<br>
</li><li>trigger<br>
</li><li>type</li></ul>

<h2>Rules to document packages</h2>
<ul><li>Default mode<br>
<ol><li>Comments between slash and two asterisks ( /<code>**</code> ) and an asterisk and a slash ( <code>*</code>/ ) treated as documentation.<br>
</li></ol></li><li>AllCommentsAsDoc mode<br>
<ol><li>All comments treated as documentation ( Comments between slash and an asterisk and comments that begin with -- (two hyphens) ).<br>
</li><li>Empty line commented with two hyphens separates documentation blocks.</li></ol></li></ul>

Project <a href='http://code.google.com/p/tapig/'>Table API Generator for Oracle</a> has <a href='http://code.google.com/p/tapig/source/browse/trunk/examples/generated_tapi_spec.sql'>example</a> of documented package.<br>
<br>
<h2>User defined objects</h2>
User defined objects (UDO) are the tables, data from which should be documented. It can be application constants, settings, etc.<br>
To create UDO it is needed to change SQL file and add new object to properties file (see example below) and <a href='DbDoc.md'>screenshots</a>.<br>
<br>
SQL file<br>
<pre><code>-- OBJECTS add AND (...) to filter rows<br>
OBJECTS==SELECT object_name FROM (<br>
SELECT object_name<br>
FROM user_objects<br>
WHERE ( object_name NOT LIKE '%$%' AND object_name NOT LIKE '%#%' )<br>
  AND object_type = :type<br>
  #NAME#<br>
UNION<br>
SELECT 'APUSERS' object_name FROM DUAL<br>
)<br>
ORDER BY object_name<br>
-- APUSERS<br>
APUSERS==SELECT *<br>
FROM APUSERS<br>
ORDER BY id<br>
...<br>
</code></pre>

Properties file<br>
<pre><code>Objects          = FUNCTION,PACKAGE,PROCEDURE,QUEUE,SCHEDULER,SEQUENCE,TABLE,TRIGGER,TYPE,APUSERS<br>
</code></pre>