@ECHO OFF

ECHO Setting ignore list for project

svn propset svn:ignore -F svnignore.txt .

svn propget svn:ignore