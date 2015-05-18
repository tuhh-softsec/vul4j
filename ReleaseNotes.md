Plexus Archiver and Plexus-IO combined release notes
========================================================================

Since archiver depends on a given version of IO this list is cumulative,
any version includes *all* changes below.

Plexus Components - Version plexus-archiver-2.10.1
-----

MASSEMBLY-768 fix.

Plexus Components - Version plexus-archiver-2.10
-----

* Symlink support in DirectoryArchiver
* Multithreaded ZIP support
* Fixed resource leak on ZIP files included in ZIP files.
* Added encoding supporting overload: addArchivedFileSet( final ArchivedFileSet fileSet, Charset charset )
* Fixed NPE with missing folder in TAR
* Moved all "zip" support to archiver (from io).


Plexus Components - Version plexus-io-2.5
-----
* Proper support for closeable on zip archives.
* Removed zip supporting PlexusIoZipFileResourceCollection; which now exists in plexus-archiver. (Drop in replacement,
just change/add jar file).


Plexus Components - Version plexus-archiver-2.9.1
-----

Wrap-up release with plexus-io-2.4.1

Plexus Components - Version plexus-io-2.4.1
-----
** Bug

    * [PLXCOMP-279] - PlexusIoProxyResourceCollection does not provide Closeable iterator
    * [PLXCOMP-280] - SimpleResourceAttributes has incorrect value for default file mode

Plexus Components - Version plexus-archiver-2.9
-----
** Bug

    * [PLXCOMP-277] - Archiver unable to determine file equailty

** Improvement

    * [PLXCOMP-276] - Reduce number of ways to create a PlexusIoResource
Plexus Components - Version plexus-io-2.4
-----
** Improvement

    * [PLXCOMP-274] - Simplify use of proxies
    * [PLXCOMP-275] - Avoid leaky abstractions
    * [PLXCOMP-276] - Reduce number of ways to create a PlexusIoResource
Plexus Components - Version plexus-archiver-2.8.4
-----
** Bug

    * [PLXCOMP-273] - Normalize file separators for duplicate check
Plexus Components - Version plexus-archiver-2.8.3
-----
** Bug

    * [PLXCOMP-271] - Implicit created directories do not obey proper dirMode
    * [PLXCOMP-272] - overriding dirmode/filemode breaks symlinks
Plexus Components - Version plexus-io-2.3.5
-----
** Bug

    * [PLXCOMP-278] - Symlink attribute was not preserved through merged/overridden attributes
Plexus Components - Version plexus-archiver-2.8.2
-----
** Bug

    * [PLXCOMP-266] - In-place filtering of streams give incorrect content length for tar files
Plexus Components - Version plexus-io-2.3.4
-----
** Bug

    * [PLXCOMP-270] - Escaping algoritghm leaks through to system classloader
    * [PLXCOMP-272] - overriding dirmode/filemode breaks symlinks
Plexus Components - Version plexus-io-2.3.3
-----
** Bug

    * [PLXCOMP-267] - StreamTransformers are consistently applied to all collections
Plexus Components - Version plexus-archiver-2.8.1
-----
** Improvement

    * [PLXCOMP-268] - Add diagnostic archivers
Plexus Components - Version plexus-io-2.3.2
-----
** Bug

    * [PLXCOMP-265] - Locale in shell influences "ls" parsing for screenscraper
Plexus Components - Version plexus-io-2.3.1
-----
** Bug

    * [PLXCOMP-264] - Thread safety issue in streamconsumer
Plexus Components - Version plexus-archiver-2.8
-----
** Bug

    * [PLXCOMP-262] - Directory symlinks in zip files are incorrect

** Improvement

    * [PLXCOMP-255] - Removed dependency plexus-container-default:1.0-alpha-9-stable-1

** New Feature

    * [PLXCOMP-263] - Support on-the fly stream filtering
Plexus Components - Version plexus-io-2.3
-----
** Improvement

    * [PLXCOMP-260] - Make plexus io collections iterable
** New Feature

    * [PLXCOMP-261] - Make plexus io collections support on-the-fly filtering
Plexus Components - Version plexus-archiver-2.7.1
-----
** Bug

    * [PLXCOMP-256] - Several archivers leaks file handles

** Improvement

    * [PLXCOMP-257] - Inconsistent buffering
Plexus Components - Version plexus-io-2.2
-----
** Bug

   * [PLXCOMP-251] - Date parsing in "ls" screenscraping has locale dependencies
   * [PLXCOMP-254] - Fix File.separatorChar normalization when prefixes are used
Plexus Components - Version plexus-archiver-2.7
-----
** Bug

    * [PLXCOMP-252] - Tar archivers cannot roundtrip own archives on windows, UTF8 bug


** Improvement

    * [PLXCOMP-253] - Switch default encoding to UTF-8
Plexus Components - Version plexus-archiver-2.6.4
-----
** Bug

    * [PLXCOMP-45] - ignoreWebXML flag use is opposite of what the name implies.
    * [PLXCOMP-107] - Fail to unzip archive, which contains file with name  'How_can_I_annotate_a_part_in_the_AAM%3F.Help' .
    * [PLXCOMP-234] - plexus archiver TarOptions setDirMode and setMode do not do anything unless TarArchiver.setOptions is called
Plexus Components - Version plexus-io-2.1.4
-----
** Bug

    * [PLXCOMP-107] - Fail to unzip archive, which contains file with name  'How_can_I_annotate_a_part_in_the_AAM%3F.Help' .

** Improvement

    * [PLXCOMP-250] - Upgrade maven-enforcer-plugin to 1.3.1
Plexus Components - Version plexus-archiver-2.6.3
-----
** Bug

    * [PLXCOMP-233] - plexus archiver can create tarfiles with empty uid and gid bytes
    * [PLXCOMP-247] - Bug with windows AND java5
Plexus Components - Version plexus-io-2.1.3
-----
** Bug

    * [PLXCOMP-247] - Bug with windows AND java5
Plexus Components - Version plexus-archiver-2.6.2
-----
** Bug

    * [PLXCOMP-238] - CRC Failure if compress=false and file size <= 4 bytes
    * [PLXCOMP-245] - Archives created on windows get zero permissions, creates malformed permissions on linux
Plexus Components - Version plexus-io-2.1.2
-----
** Bug

    * [PLXCOMP-244] - Don't try to set attributes of symbolic links
    * [PLXCOMP-245] - Archives created on windows get zero permissions, creates malformed permissions on linux
Plexus Components - Version plexus-archiver-2.6.1
-----
** Bug

    * [PLXCOMP-243] - Restore JDK1.5 compatibility

Plexus Components - Version plexus-io-2.1.1
-----
** Bug

    * [PLXCOMP-243] - Restore JDK1.5 compatibility
Plexus Components - Version plexus-archiver-2.6
-----
** Bug

    * [PLXCOMP-113] - zip unarchiver doesn't support symlinks (and trivial to fix)
** Improvement

    * [PLXCOMP-64] - add symlink support to tar unarchiver
    * [PLXCOMP-117] - add symbolic links managment
Plexus Components - Version plexus-io-2.1
-----
** Bug

    * [PLXCOMP-113] - zip unarchiver doesn't support symlinks (and trivial to fix)
    * [PLXCOMP-241] - ResourcesTest.compare test failure
    * [PLXCOMP-248] - Use java7 setAttributes and ignore useJvmChmod flag when applicable

** Improvement

    * [PLXCOMP-64] - add symlink support to tar unarchiver
    * [PLXCOMP-117] - add symbolic links managment



Plexus Components - Version plexus-archiver-2.5 (plexus-io 2.0.12)
-----
** Bug

    * [PLXCOMP-13] - Plexus Archiver fails on certain Jars
    * [PLXCOMP-205] - Tar unarchiver does not respect includes/excludes flags
    * [PLXCOMP-216] - Unarchiver extracts files into wrong directory
    * [PLXCOMP-232] - Failures to unpack .tar.gz files
    * [PLXCOMP-236] - ZipUnArchiver fails to extract large (>4GB) ZIP files

** Improvement

    * [PLXCOMP-153] - TarUnArchiver does not support includes/excludes
    * [PLXCOMP-240] - Convert everything to commons-compress


Plexus Components - Version plexus-io-2.0.12
-----

** Bug

    * [PLXCOMP-249] - Add support for java7 chmod

Plexus Components - Version plexus-archiver-2.4.4 (plexus-io 2.0.10)
-----

** Bug
    * [PLXCOMP-178] - last modification time is not preserved
    * [PLXCOMP-222] - ZipOutputStream does not set Language encoding flag (EFS) when using UTF-8 encoding
    * [PLXCOMP-226] - Bug in org.codehaus.plexus.archiver.zip.ZipOutputStream.closeEntry(ZipOutputStream.java:352)



Older history in JIRA at http://jira.codehaus.org/browse/PLXCOMP
-----
