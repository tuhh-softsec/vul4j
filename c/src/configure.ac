# The Apache Software License, Version 1.1
#
#
# Copyright (c) 1999 The Apache Software Foundation.  All rights 
# reserved.
#
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions
# are met:
#
# 1. Redistributions of source code must retain the above copyright
#    notice, this list of conditions and the following disclaimer. 
#
# 2. Redistributions in binary form must reproduce the above copyright
#    notice, this list of conditions and the following disclaimer in
#    the documentation and/or other materials provided with the
#    distribution.
#
# 3. The end-user documentation included with the redistribution,
#    if any, must include the following acknowledgment:  
#       "This product includes software developed by the
#        Apache Software Foundation (http://www.apache.org/)."
#    Alternately, this acknowledgment may appear in the software itself,
#    if and wherever such third-party acknowledgments normally appear.
#
# 4. The names "<WebSig>" and "Apache Software Foundation" must
#    not be used to endorse or promote products derived from this
#    software without prior written permission. For written 
#    permission, please contact apache@apache.org.
#
# 5. Products derived from this software may not be called "Apache",
#    nor may "Apache" appear in their name, without prior written
#    permission of the Apache Software Foundation.
#
# THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
# WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
# OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
# DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
# ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
# SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
# LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
# USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
# ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
# OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
# OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
# SUCH DAMAGE.
# ====================================================================
#
# This software consists of voluntary contributions made by many
# individuals on behalf of the Apache Software Foundation and was
# originally based on software copyright (c) 2001, Institute for
# Data Communications Systems, <http://www.nue.et-inf.uni-siegen.de/>.
# The development of this software was partly funded by the European 
# Commission in the <WebSig> project in the ISIS Programme. 
# For more information on the Apache Software Foundation, please see
# <http://www.apache.org/>.

# Process this file with autoconf to produce a configure script

AC_INIT([[XML-Security-C]], [0.2.0], [berin@ozemail.com.au], [xml-security-c])

# framework is a unique directory in src/ - allows configure to check
# it is running in the correct directory

AC_CONFIG_SRCDIR(framework)

# Define the files we wish to generate

#AC_CONFIG_FILES([Makefile \
#	canon/Makefile \
#	transformers/Makefile \
#	dsig/Makefile \
#	enc/Makefile \
#	enc/OpenSSL/Makefile \
#	framework/Makefile \
#	utils/Makefile \
#	utils/unixutils/Makefile \
#	tools/Makefile \
#	samples/Makefile])
AC_CONFIG_FILES([Makefile canon/Makefile transformers/Makefile dsig/Makefile enc/Makefile enc/OpenSSL/Makefile framework/Makefile utils/Makefile utils/unixutils/Makefile tools/Makefile samples/Makefile])
AC_CONFIG_HEADERS([framework/XSECConfig.hpp])

# Get user options

AC_ARG_WITH(xalan, 
	AC_HELP_STRING(--with-xalan, 
		[Enable Xalan integration (Default = yes)]), 
	use_xalan=$withval, use_xalan=yes)

AC_ARG_ENABLE(debug,
	AC_HELP_STRING(--enable-debug,
		[Compile a version of the library with symbols (Default = no)]),
	enable_debug=$enableval, enable_debug=no)

if test "$enable_debug" = "yes" ; then
	CFLAGS="${CFLAGS} -g"
	CXXFLAGS="${CXXFLAGS} -g"
else
	CFLAGS="${CFLAGS} -O2 -DNDEBUG"
	CXXFLAGS="${CXXFLAGS} -O2 -DNDEBUG"
fi

# Create the Makefile.in files in each directory
library_l1_src_dirs="canon transformers dsig enc framework utils"
library_l2_src_dirs="enc/OpenSSL utils/unixutils"

AC_CONFIG_COMMANDS_PRE(
[# Define the library directories
for lib_dir in ${library_l1_src_dirs}
do
	AC_MSG_NOTICE([Creating Makefile.in in ${lib_dir}])
	lib_dir_tran=`echo ${lib_dir} | sed "s/\//_/g"`
    sed "s/__DIRECTORY__/${lib_dir_tran}/g" Makefile.in.tmpl | \
    sed "s/__DIRECTORY_PATH__/${lib_dir}/g" | \
	sed "s/__SRCBASEDISTANCE__/\.\./g" > ${lib_dir}/Makefile.in
done
# Now layer 2
for lib_dir in ${library_l2_src_dirs}
do
	AC_MSG_NOTICE([Creating Makefile.in in ${lib_dir}])
	lib_dir_tran=`echo ${lib_dir} | sed "s/\//_/g"`
	lib_dir_sed=`echo ${lib_dir} | sed "s/\//\\\\\\\\\\//g"`
    sed "s/__DIRECTORY__/${lib_dir_tran}/g" Makefile.in.tmpl | \
    sed "s/__DIRECTORY_PATH__/${lib_dir_sed}/g" | \
	sed "s/__SRCBASEDISTANCE__/\.\.\/\.\./g" > ${lib_dir}/Makefile.in
done])

# Check for basic programs

AC_PROG_CC
AC_PROG_CXX
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_AWK
AC_CHECK_PROG(AUTOCONF, autoconf, autoconf, true)

# AC_PROG_CC Defines the OBJEXT
objsuffix=".${OBJEXT}"


# Set up versions

package_version_major=`echo ${PACKAGE_VERSION} | ${AWK} -F '.' '{print $1}'`
package_version_medium=`echo ${PACKAGE_VERSION} | ${AWK} -F '.' '{print $2}'`
package_version_minor=`echo ${PACKAGE_VERSION} | ${AWK} -F '.' '{print $3}'`

# "cache" the check for gcc -E

AC_TRY_CPP()
AC_LANG(C++)
AC_TRY_CPP()
AC_LANG(C)

# Checks for required libraries
AC_CHECK_LIB(m, floor)

# Determine the host system and Makefile fragment

# Check for required includes
AC_CHECK_HEADERS(unistd.h direct.h)

AC_CANONICAL_HOST

# Platform-specific Makefile setup
# We have put most of the platform specific configuration here rather than
# in the Makefile.  Autoconf is designed with platform portability in mind,
# so by putting the complexity here we should be able to create a more
# portable makefile.

case "${host}" in
        *-*-solaris*)   platform=SOLARIS 
						shlibsuffix=.so
						lib_name="lib${PACKAGE_TARNAME}.so.${PACKAGE_VERSION}"
					    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_version_major}"
						lib_short_name="lib${PACKAGE_TARNAME}.so"
						if test "x${ac_cv_cxx_compiler_gnu}" = "xyes"; then
						# if test "x${CXX}" = "xg++"; then
							PLATFORM_OPTIONS=["-Wall"]
							PIC=["-fPIC"]
						else
							# Not sure if these will work - only tested with g++
							PIC=["-KPIC"]
							PLATFORM_OPTIONS=["-mt -ks"]
						fi
						# Should use -D_REENTRANT - but not yet OK
						PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} ${PIC} -DSOLARIS"]
						CC1=["${CXX} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						CC4=["${CC} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						MAKE_SHARED=["${CXX} ${CXXFLAGS} -DSOLARIS -shared ${PIC}"]
						LINK=["${CXX} ${CXXFLAGS} -DSOLARIS ${PIC}"]
						LINK_COMMAND_1=["(cd \$(LIB_DIR) ; rm -f \$(LIBMAJORNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBMAJORNAME))"]
						LINK_COMMAND_2=["(cd \$(LIB_DIR) ; rm -f \$(LIBSHORTNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBSHORTNAME))"]
						INSTALL_COMMAND_1=["\$(INSTALL) \${THISLIB} \${libdir}"]
						INSTALL_COMMAND_2=["\$(RM) \${libdir}/\${LIBMAJORNAME} && \$(LN_S) \${LIBNAME} \${libdir}/\${LIBMAJORNAME}"]
						INSTALL_COMMAND_3=["\$(RM) \${libdir}/\${LIBSHORTNAME} && \$(LN_S) \${LIBNAME} \${libdir}/\${LIBSHORTNAME}"]

						;;

        *-*-netbsd*)   platform=NETBSD 
						shlibsuffix=.so
						lib_name="lib${PACKAGE_TARNAME}.so.${PACKAGE_VERSION}"
					    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_version_major}"
						lib_short_name="lib${PACKAGE_TARNAME}.so"
						PLATFORM_OPTIONS=["-Wall"]
						PIC=["-fPIC"]

						# Should use -D_REENTRANT - but not yet OK
						PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} ${PIC} -DNETBSD"]
						CC1=["${CXX} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						CC4=["${CC} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						MAKE_SHARED=["${CXX} ${CXXFLAGS} -DNETBSD -shared ${PIC}"]
						LINK=["${CXX} ${CXXFLAGS} -DNETBSD ${PIC}"]
						LINK_COMMAND_1=["(cd \$(LIB_DIR) ; rm -f \$(LIBMAJORNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBMAJORNAME))"]
						LINK_COMMAND_2=["(cd \$(LIB_DIR) ; rm -f \$(LIBSHORTNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBSHORTNAME))"]
						INSTALL_COMMAND_1=["\$(INSTALL) \${THISLIB} \${libdir}"]
						INSTALL_COMMAND_2=["\$(RM) \${libdir}/\${LIBMAJORNAME} && \$(LN_S) \${LIBNAME} \${libdir}/\${LIBMAJORNAME}"]
						INSTALL_COMMAND_3=["\$(RM) \${libdir}/\${LIBSHORTNAME} && \$(LN_S) \${LIBNAME} \${libdir}/\${LIBSHORTNAME}"]

						;;

        *-*-linux*)     platform=LINUX ;
						shlibsuffix=.so
						lib_name="lib${PACKAGE_TARNAME}.so.${PACKAGE_VERSION}"
					    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_version_major}"
						lib_short_name="lib${PACKAGE_TARNAME}.so"
						#if test "x${CXX}" = "xg++"; then
						if test "x${ac_cv_cxx_compiler_gnu}" = "xyes"; then
							PLATFORM_OPTIONS=["-Wall"]
							PIC=["-fPIC"]
						else
							# Not sure if these will work - only tested with g++
							PIC=["-KPIC"]
							PLATFORM_OPTIONS=["-w1 -Xa -mp -prec_div"]
						fi
						# Should use -D_REENTRANT - but not yet OK
						PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} ${PIC} -DLINUX"]
						CC1=["${CXX} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						CC4=["${CC} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						MAKE_SHARED=["${CXX} ${CXXFLAGS} -Wl,-soname,\$(LIBNAME) -DLINUX -shared ${PIC}"]
						LINK=["${CXX} ${CXXFLAGS} -DLINUX ${PIC}"]
						LINK_COMMAND_1=["(cd \$(LIB_DIR) ; rm -f \$(LIBMAJORNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBMAJORNAME))"]
						LINK_COMMAND_2=["(cd \$(LIB_DIR) ; rm -f \$(LIBSHORTNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBSHORTNAME))"]
						INSTALL_COMMAND_1=["\$(INSTALL) \${THISLIB} \${libdir}"]
						INSTALL_COMMAND_2=["\$(RM) \${libdir}/\${LIBMAJORNAME} && \$(LN_S) \${LIBNAME} \${libdir}/\${LIBMAJORNAME}"]
						INSTALL_COMMAND_3=["\$(RM) \${libdir}/\${LIBSHORTNAME} && \$(LN_S) \${LIBNAME} \${libdir}/\${LIBSHORTNAME}"]

						;;

		*-*-cygwin*)	platform=CYGWIN
						shlibsuffix=.dll
						lib_name="cyg${PACKAGE_TARNAME}-${PACKAGE_VERSION}.dll"
					    lib_major_name="lib${PACKAGE_TARNAME}.dll.a"
						lib_short_name="cyg${PACKAGE_TARNAME}.dll"
						# Assume g++
						PLATFORM_OPTIONS=["-Wall"]

						# Should use -D_REENTRANT - but not yet OK
						PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DCYGWIN"]
						CC1=["${CXX} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						CC4=["${CC} ${CXXFLAGS} ${PLATFORM_OPTIONS}"]
						MAKE_SHARED=["${CXX} ${CXXFLAGS} -Wl,-soname,\$(LIBNAME) -DCYGWIN -shared -Xlinker --warn-once -Xlinker -Map -Xlinker ../lib/lib${PACKAGE_TARNAME}.map -Xlinker --output-def -Xlinker ../lib/lib${PACKAGE_TARNAME}.def -Xlinker --out-implib -Xlinker ../lib/\$(LIBMAJORNAME)"]
						LINK=["${CXX} ${CXXFLAGS} -DCYGWIN"]

						INSTALL_COMMAND_1=["\$(INSTALL) \${THISLIB} \${bindir}"]
						INSTALL_COMMAND_2=["\$(INSTALL) \$(LIB_DIR)/\$(LIBMAJORNAME) \${libdir}"]

						;;
						
#        *-*-irix*)      platform=IRIX ;;
#        *-*-aix*)       platform=AIX ;;
#        *-*-hp*)        platform=HPUX ;
#                        case "${host}" in
#                            *-*-hpux11*)  osver=HPUX11;;
#                            *-*-hpux10*)  osver=HPUX10;;
#                            *)            osver=HPUX11;;
#                        esac;;
#        *-*-mvs*)       platform=OS390 ;;
#        *-*-osf*)       platform=TRU64 ;;
#        *-apple-darwin*)       platform=MACOSX ;;
        *)              platform=UNKNOWN ;
						AC_MSG_ERROR([Unknown Platform.  Aborting])
						;;
esac

# Find Xerces

# Check for Xerces using XERCESCROOT (override any system version)

AC_LANG(C++)

if test $XERCESCROOT; then

  OLDCPPFLAGS=$CPPFLAGS
  CPPFLAGS=["${CPPFLAGS} -I${XERCESCROOT}/src"]

  AC_MSG_CHECKING([for Xerces 2.x headers in XERCESCROOT/src])
  AC_TRY_RUN([#include <xercesc/util/XercesVersion.hpp>
    int main(int argc, char **argv) {
      if (XERCES_VERSION_MAJOR < 2)
        return 1;
      else
        return 0;
    }],
    LIBS="${LIBS} -L${XERCESCROOT}/lib -lxerces-c"
    xerces_found=yes
    [AC_MSG_RESULT([found])],
    CPPFLAGS=$OLD_CPPFLAGS
    [AC_MSG_RESULT([no])])

fi

if test -z $xerces_found ; then

  # Xerces was not found

  AC_MSG_CHECKING([for Xerces 2.x headers in system includes])

  AC_TRY_RUN([#include <xercesc/util/XercesVersion.hpp>
    int main(int argc, char **argv) {
      if (XERCES_VERSION_MAJOR < 2)
        return 1;
      else
        return 0;
    }],
    LIBS="${LIBS} -lxerces-c"
    [AC_MSG_RESULT([found])],
    [AC_MSG_ERROR([Xerces 2.0 not found.  Ensure XERCESCROOT points to base directory of Xerces and Xerces 2.0 or later is available])])

fi

# Find out some properties of the version of Xerces we have

AC_MSG_CHECKING([whether Xerces XMLFormatter requires a version])

AC_TRY_COMPILE([#include <xercesc/framework/XMLFormatter.hpp>
	#include <xercesc/util/XercesDefs.hpp>
	#include <xercesc/util/XMLUniDefs.hpp>],
	[using namespace XERCES_CPP_NAMESPACE;
	  XMLCh s_encoding[] = {
		chLatin_U, chLatin_T, chLatin_F, chDash, chDigit_8, chNull
	  };
	  XMLFormatter f(s_encoding,
		NULL,
		NULL, 
		XMLFormatter::NoEscapes, 
		XMLFormatter::UnRep_CharRef);
	],
	[AC_MSG_RESULT([yes])]
	[AC_DEFINE(XSEC_XERCES_FORMATTER_REQUIRES_VERSION)],
	[AC_MSG_RESULT([no])])
	

# Now check for Xalan

if test "$use_xalan" = "yes" ; then

  if test $XALANCROOT; then

    AC_MSG_CHECKING([for Xalan headers in XALANCROOT])

    OLD_CPPFLAGS=$CPPFLAGS
    CPPFLAGS=["${CPPFLAGS} -I${XALANCROOT}/src"]

    AC_TRY_CPP([#include <Include/PlatformDefinitions.hpp>], 
      xalan_found=yes 
      LIBS="${LIBS} -L${XALANCROOT}/lib -lxalan-c1_5_0"
      [AC_MSG_RESULT([found])], 
      CPPFLAGS=$OLD_CPPFLAGS
      [AC_MSG_RESULT([no])]);

  fi

  if test -z $xalan_found ; then

    AC_MSG_CHECKING([for Xalan in system includes])
    AC_TRY_CPP([#include <Include/PlatformDefinitions.hpp>], 
      xalan_found=yes 
      LIBS="${LIBS} -lxalan-c1_5_0"
      [AC_MSG_RESULT([found])],
      [AC_DEFINE(XSEC_NO_XALAN)] 
      [AC_MSG_RESULT([no - WARNING - configuring for no Xalan])]);

  fi

else

  # NO_XALAN is defined - so we skip

  AC_MSG_NOTICE([Warning - Xalan being configured out - XPath and XSLT will not be available])
  AC_DEFINE(XSEC_NO_XALAN)

fi

AC_LANG(C)

# Check for OpenSSL
if test $OPENSSL ; then

AC_MSG_CHECKING([for OpenSSL headers in OPENSSL])

OLD_CPPFLAGS=$CPPFLAGS
CPPFLAGS=["${CPPFLAGS} -I${OPENSSL}/include"]

AC_TRY_CPP([#include <openssl/opensslv.h>], 
   openssl_found=yes 
   AC_DEFINE(HAVE_OPENSSL)
   LIBS="${LIBS} -L${OPENSSL} -lcrypto"
   [AC_MSG_RESULT([found])], 
   CPPFLAGS=$OLD_CPPFLAGS
   [AC_MSG_RESULT([no])]);

fi

if test -z $openssl_found ; then

AC_MSG_CHECKING([for OpenSSL in system includes])
AC_TRY_CPP([#include <openssl/opensslv.h>], 
   openssl_found=yes 
   AC_DEFINE(HAVE_OPENSSL)
   LIBS="${LIBS} -lcrypto"
   [AC_MSG_RESULT([found])],
   [AC_MSG_RESULT([no])]
   [AC_MSG_ERROR([OpenSSL header files not find.  Please define OPENSSL])]);

fi

# Build the object file lists necessary to link the library
# At the same time, build the include list

lib_obj_files=""

for lib_dir in ${library_l1_src_dirs}
do
	AC_CREATE_OBJ_FILELIST([${lib_dir}], 
		[*.cpp], 
		[\.cpp], 
		[[${objsuffix}]], 
		[../../lib/obj])
	eval ${lib_dir}_obj_files="\${ac_find_files}"

	# Create the libraries list
	AC_REPLACE_STRING([${ac_find_files}], [[\.\.\/\.\.\/]], [[\.\.\/]])
	lib_obj_files="${lib_obj_files} $ac_replace_string"

	# Create the Include list
	AC_CREATE_FILELIST([${lib_dir}],[*.hpp])
	eval ${lib_dir}_include_files="\${ac_find_files}"

done

for lib_dir in ${library_l2_src_dirs}
do
	lib_dir_tran=`echo ${lib_dir} | sed "s/\//_/g"`
	AC_CREATE_OBJ_FILELIST([${lib_dir}], 
		[*.cpp], 
		[\.cpp], 
		[[${objsuffix}]], 
		[../../../lib/obj])
	eval ${lib_dir_tran}_obj_files="\${ac_find_files}"

	# Create the libraries list
	AC_REPLACE_STRING([${ac_find_files}], [[\.\.\/\.\.\/\.\.\/]], [[\.\.\/]])
	lib_obj_files="${lib_obj_files} $ac_replace_string"

	# Create the Include list
	AC_CREATE_FILELIST([${lib_dir}],[*.hpp])
	eval ${lib_dir_tran}_include_files="\${ac_find_files}"


done

# Check to ensure the config.hpp file has been added :

check_cnf=`echo ${framework_include_files} | grep XSECConfig`

if test "${framework_include_files}" != "${check_cnf}" ; then
	framework_include_files="${framework_include_files} XSECConfig.hpp"
fi

# Use the found lists to populate the relevant makefiles

AC_SUBST(canon_obj_files)
AC_SUBST(transformers_obj_files)
AC_SUBST(dsig_obj_files)
AC_SUBST(enc_obj_files)
AC_SUBST(enc_OpenSSL_obj_files)
AC_SUBST(framework_obj_files)
AC_SUBST(utils_obj_files)
AC_SUBST(utils_unixutils_obj_files)
AC_SUBST(lib_obj_files)

AC_SUBST(canon_include_files)
AC_SUBST(transformers_include_files)
AC_SUBST(dsig_include_files)
AC_SUBST(enc_include_files)
AC_SUBST(enc_OpenSSL_include_files)
AC_SUBST(framework_include_files)
AC_SUBST(utils_include_files)
AC_SUBST(utils_unixutils_include_files)
AC_SUBST(lib_include_files)

# Populate Install directories

AC_SUBST(prefix)
AC_SUBST(exec_prefix)
AC_SUBST(includedir)
AC_SUBST(libdir)
AC_SUBST(bindir)

# Populate special platform specific make commands

AC_SUBST(LINK_COMMAND_1)
AC_SUBST(LINK_COMMAND_2)
AC_SUBST(INSTALL_COMMAND_1)
AC_SUBST(INSTALL_COMMAND_2)
AC_SUBST(INSTALL_COMMAND_3)

# Now populate the platform and Compiler related variables.

AC_SUBST(platform)

cxx=${CXX}
AC_SUBST(cxx)

cc=${CC}
AC_SUBST(cc)

AC_SUBST(prefix)
AC_SUBST(osver)

cxxflags=${CXXFLAGS}
AC_SUBST(cxxflags)

cppflags=${CPPFLAGS}
AC_SUBST(cppflags)

cflags=${CFLAGS}
AC_SUBST(cflags)

ldflags=${LDFLAGS}
AC_SUBST(ldflags)

libs=${LIBS}
AC_SUBST(libs)

transcoder=${TRANSCODER}
AC_SUBST(transcoder)

threads=${THREADS}
AC_SUBST(threads)

bitstobuild=${BITSTOBUILD}
AC_SUBST(bitstobuild)

cc1=${CC1}
AC_SUBST(cc1)

cc4=${CC4}
AC_SUBST(cc4)

ln_s=${LN_S}
AC_SUBST(ln_s)

install=${INSTALL}
install_data=${INSTALL_DATA}

AC_SUBST(install)
AC_SUBST(install_data)

make_shared=${MAKE_SHARED}
AC_SUBST(make_shared)

link=${LINK}
AC_SUBST(link)

AC_SUBST(objsuffix)
AC_SUBST(shlibsuffix)

# Versioning
package_version=${PACKAGE_VERSION}
AC_SUBST(package_version)
AC_SUBST(package_version_major)
AC_DEFINE_UNQUOTED(XSEC_VERSION, "$package_version")
AC_DEFINE_UNQUOTED(XSEC_VERSION_MAJOR, $package_version_major)
AC_DEFINE_UNQUOTED(XSEC_VERSION_MEDIUM, $package_version_medium)
AC_DEFINE_UNQUOTED(XSEC_VERSION_MINOR, $package_version_minor)

package_name=${PACKAGE_NAME}
AC_SUBST(package_name)
package_tarname=${PACKAGE_TARNAME}
AC_SUBST(package_tarname)

# Library names
AC_SUBST(lib_name)
AC_SUBST(lib_major_name)
AC_SUBST(lib_short_name)

# output the Makefiles
AC_OUTPUT()
