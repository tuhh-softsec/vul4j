# Copyright 2002-2006 The Apache Software Foundation.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# All these setting come from the configure script
# We use configure to do most of the hard work as it is more
# designed to handle different system complexity than make
#

# Process this file with autoconf to produce a configure script

AC_INIT([[XML-Security-C]], [1.6.0], [security-dev@xml.apache.org], [xml-security-c])
AC_CONFIG_AUX_DIR([config])

# kick off automake

AM_INIT_AUTOMAKE(xml-security-c, 1.6.0)
sinclude(m4/acx_pthread.m4)
AM_PROG_LIBTOOL

# We want to control the autotools invocation

AM_MAINTAINER_MODE

# framework is a unique directory in src/ - allows configure to check
# it is running in the correct directory

AC_CONFIG_SRCDIR(src)

# Define the files we wish to generate

AC_CONFIG_FILES([Makefile\
  lib/Makefile \
  include/Makefile \
  bin/Makefile ])

# AC_CONFIG_FILES([Makefile canon/Makefile transformers/Makefile dsig/Makefile enc/Makefile enc/OpenSSL/Makefile framework/Makefile utils/Makefile utils/unixutils/Makefile tools/Makefile samples/Makefile])
AC_CONFIG_HEADERS([src/framework/XSECConfig.hpp])

# Get user options

AC_ARG_WITH(xerces, 
	AC_HELP_STRING(--with-xerces, 
		[Define Xerces directory.  Values = 'yes' or installation directory. 'No' is not permitted (Default = yes)]), 
	use_xerces=$withval, use_xerces=yes)

if test x"$use_xerces" != "xyes" ; then
  if test x"$use_xerces" != "xno" ; then
    XERCESCROOT=$use_xerces
  else
    AC_MSG_ERROR([Cannot configure without Xerces])
  fi
fi


AC_ARG_WITH(xalan, 
	AC_HELP_STRING(--with-xalan, 
		[Enable Xalan integration.  Values = 'yes' or installation directory (Default = yes)]), 
	use_xalan=$withval, use_xalan=yes)

if test x"$use_xalan" != "xyes" ; then
  if test x"$use_xalan" != "xno" ; then
    XALANCROOT=$use_xalan
  fi
fi

AC_ARG_WITH(openssl,
    AC_HELP_STRING(--with-openssl,
        [Use Openssl.  Values = 'yes' or installation directory (Default = yes)]),
    use_openssl=$withval, use_openssl=yes)

if test x"$use_openssl" != "xyes" ; then
  if test x"$use_openssl" != "xno" ; then
    # Must be the source directory
    OPENSSL=$use_openssl
  fi
fi

AC_ARG_ENABLE(debug,
	AC_HELP_STRING(--enable-debug,
		[Compile a version of the library with symbols (Default = no)]),
	enable_debug=$enableval, enable_debug=no)

# As of version 1.3, by default treat warnings as errors

AC_ARG_ENABLE(warnerror,
	AC_HELP_STRING(--enable-warnerrer,
		[Treat warnings as errors during compile (Default = no)]),
	enable_warnerror=$enableval, enable_warnerror=no)

# Check for basic programs

AC_PROG_CC
AC_PROG_CXX
AC_PROG_INSTALL
AC_PROG_LN_S
AC_PROG_AWK
AC_CHECK_PROG(AUTOCONF, autoconf, autoconf, true)

# AC_PROG_CC Defines the OBJEXT
objsuffix=".${OBJEXT}"

# pthreads
ACX_PTHREAD
CXXFLAGS="${CXXFLAGS} ${PTHREAD_CFLAGS}"
LIBS="${LIBS} ${PTHREAD_LIBS}"

# Set up versions

package_version_major=`echo ${PACKAGE_VERSION} | ${AWK} -F '.' '{print $1}'`
package_version_medium=`echo ${PACKAGE_VERSION} | ${AWK} -F '.' '{print $2}'`
package_version_minor=`echo ${PACKAGE_VERSION} | ${AWK} -F '.' '{print $3}'`

package_lib_major=${package_version_major}${package_version_medium}
package_lib_minor=${package_version_minor}
package_lib_version=${package_lib_major}.${package_lib_minor}

# "cache" the check for gcc -E

AC_TRY_CPP()
AC_LANG(C++)
AC_TRY_CPP()

# Need to reset CXXFLAGS if defaults have been set
if test "$ac_test_CXXFLAGS" != set; then
  CXXFLAGS=""
fi

# Check for Intel > 8.0 (which fools configure into thinking it is GNU
if test "x${ac_cv_cxx_compiler_gnu}" = "xyes"; then
	AC_MSG_CHECKING([for Intel compiler disguised as GNU])
	intel_check=`${CXX} -v 2>&1 | ${AWK} '{print $1}'`
	if test "x${intel_check}" = "xVersion"; then
		AC_MSG_RESULT([yes])
		ac_cv_cxx_compiler_gnu="no"
	else
		AC_MSG_RESULT([no])
	fi
fi

AC_LANG(C)

# Checks for required libraries
AC_CHECK_LIB(m, floor)
AC_CHECK_LIB(socket, getservent)

# Determine the host system and Makefile fragment

# Check for required includes
AC_CHECK_HEADERS(unistd.h direct.h)

# As a part of the automake process, AM_CHECK_LIBTOOL calls
# this automatically.  Adding here causes spurious warning messages
#AC_CANONICAL_HOST

# Ensure we are always compiling using library defs

CFLAGS="${CFLAGS} -DXSEC_LIBRARY_BUILD"
CXXFLAGS="${CXXFLAGS} -DXSEC_LIBRARY_BUILD"


# Platform-specific Makefile setup
# We have put most of the platform specific configuration here rather than
# in the Makefile.  Autoconf is designed with platform portability in mind,
# so by putting the complexity here we should be able to create a more
# portable makefile.

case "${host}" in
  *-*-solaris*)   
    platform=SOLARIS 
    shlibsuffix=.so
    lib_name="lib${PACKAGE_TARNAME}.so.${package_lib_version}"
    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_lib_major}"
    lib_short_name="lib${PACKAGE_TARNAME}.so"

    if test "x${ac_cv_cxx_compiler_gnu}" != "xyes"; then
      # Flags for warnings as errors
      cxxflags_warnerror="-xwe -errtags"
      # Flags for Optimise
      cxxflags_optimise="-xO2"
      # Flags for debug
      cxxflags_debug="-g"
    fi

    # Should use -D_REENTRANT - but not yet OK
    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DSOLARIS"]
    SHARED_OPTIONS="-G -DSOLARIS"
    LINK_OPTIONS="-DSOLARIS"
						;;

  *-*-netbsd*)   platform=NETBSD 
    shlibsuffix=.so
    lib_name="lib${PACKAGE_TARNAME}.so.${package_lib_version}"
    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_lib_major}"
    lib_short_name="lib${PACKAGE_TARNAME}.so"

    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DFREEBSD"]
    SHARED_OPTIONS=["-DNETBSD -shared"]
	LINK_OPTIONS=["-DNETBSD"]

    # NetBSD puts packages in /usr/pkg
    CPPFLAGS="${CPPFLAGS} -I/usr/pkg/include"
    LIBS="${LIBS} -L/usr/pkg/lib"
						;;

  *-*-freebsd*)   platform=FREEBSD 
    shlibsuffix=.so
    lib_name="lib${PACKAGE_TARNAME}.so.${package_lib_version}"
    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_version_major}"
    lib_short_name="lib${PACKAGE_TARNAME}.so"

    # Should use -D_REENTRANT - but not yet OK
    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DFREEBSD"]
    SHARED_OPTIONS="-DFREEBSD -shared -pthread -lc_r"
    LINK_OPTIONS="-DFREEBSD -pthread -lc_r"

						;;

  *-*-linux*)     platform=LINUX ;
    shlibsuffix=.so
    lib_name="lib${PACKAGE_TARNAME}.so.${package_lib_version}"
    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_lib_major}"
    lib_short_name="lib${PACKAGE_TARNAME}.so"

    if test "x${ac_cv_cxx_compiler_gnu}" != "xyes"; then
      # Assume Intel CC
      PLATFORM_OPTIONS=["-w1 -mp -prec_div"]
    fi

    # Should use -D_REENTRANT - but not yet OK
    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DLINUX"]
    SHARED_OPTIONS=["-Wl,-soname,\$(LIBNAME) -DLINUX -shared"]
    LINK_OPTIONS=["-DLINUX -ldl"]

						;;

  *-*-cygwin*)	
    platform=CYGWIN
    shlibsuffix=.dll
    lib_name="cyg${PACKAGE_TARNAME}-${package_lib_version}.dll"
    lib_major_name="lib${PACKAGE_TARNAME}.dll.a"
    lib_short_name="cyg${PACKAGE_TARNAME}.dll"

    # Assume g++
    PLATFORM_OPTIONS=["-Wall"]
    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DCYGWIN"]
    PIC=""
    SHARED_OPTIONS="-Wl,-soname,\$(LIBNAME) -DCYGWIN -shared -Xlinker --warn-once -Xlinker -Map -Xlinker ../lib/lib${PACKAGE_TARNAME}.map -Xlinker --output-def -Xlinker ../lib/lib${PACKAGE_TARNAME}.def -Xlinker --out-implib -Xlinker ../lib/\$(LIBMAJORNAME)"
    LINK_OPTIONS=["-DCYGWIN"]

    # Define installation commands as cygwin uses different defaults
    INSTALL_COMMAND_1=["\$(INSTALL) -d \$(DESTDIR)\${bindir} && \$(INSTALL) \${THISLIB} \$(DESTDIR)\${bindir}"]
    INSTALL_COMMAND_2=["\$(INSTALL) -d \$(DESTDIR)\${libdir} && \$(INSTALL) \$(LIB_DIR)/\$(LIBMAJORNAME) \$(DESTDIR)\${libdir}"]

    # Define this to kill the link commands
    LINK_COMMAND_1=true
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

  *-*-osf*)
    platform=TRU64
    shlibsuffix=.so
    lib_name="lib${PACKAGE_TARNAME}.so.${package_lib_version}"
    lib_major_name="lib${PACKAGE_TARNAME}.so.${package_lib_major}"
    lib_short_name="lib${PACKAGE_TARNAME}.so"

    # Should use -D_REENTRANT - but not yet OK
    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DTRU64"]
    SHARED_OPTIONS=["-Wl,-soname,\$(LIBNAME) -DTRU64 -shared"]
    LINK_OPTIONS=["-DTRU64"]
                        ;;

  *-apple-darwin*)
    platform=MACOSX 
    shlibsuffix=.dylib
    lib_name="lib${PACKAGE_TARNAME}.${package_lib_version}.dylib"
    lib_major_name="lib${PACKAGE_TARNAME}.${package_lib_major}.dylib"
    lib_short_name="lib${PACKAGE_TARNAME}.dylib"

    if test "x${ac_cv_cxx_compiler_gnu}" != "xyes"; then
      # Not sure if these will work - only tested with g++
      PLATFORM_OPTIONS=["-w1 -mp -prec_div"]
    fi

    PLATFORM_OPTIONS=["${PLATFORM_OPTIONS} -DMACOSX"]
    SHARED_OPTIONS=["-Xlinker -install_name -Xlinker \$(LIBNAME) -DMACOSX -dynamiclib"]
    LINK_OPTIONS=["-DMACOSX"]
                        ;;

  *)              platform=UNKNOWN 
    AC_MSG_ERROR([Unknown Platform.  Aborting])
						;;
esac

# Now we should have all compiler specific flags.  So set up specific flags
# we need

# First if we are using gcc then...
if test "x${ac_cv_cxx_compiler_gnu}" = "xyes" ; then

  CXXFLAGS=["${CXXFLAGS} ${PLATFORM_OPTIONS} -Wall"]

  if test "$enable_debug" = "yes" ; then
	CXXFLAGS="${CXXFLAGS} -g"
  else
	CXXFLAGS="${CXXFLAGS} -O2 -DNDEBUG"
  fi

  if test "x${enable_warnerror}" = "xyes" ; then
    AC_MSG_WARN([Enabling error on warnings])
	CXXFLAGS="${CXXFLAGS} -Werror -Wno-deprecated"
  fi
else

  if test "$enable_debug" = "yes" ; then
	CXXFLAGS="${CXXFLAGS} ${cxxflags_debug}"
  else
	CXXFLAGS="${CXXFLAGS} ${cxxflags_optimise} -DNDEBUG"
  fi

  if test "x${enable_warnerror}" = "xyes" ; then
    AC_MSG_WARN([Enabling error on warnings])
	CXXFLAGS="${CXXFLAGS} ${cxxflags_warnerror}"
  fi

fi

# How do we run the C++ compiler?
CC1=["${CXX} ${CXXFLAGS}"]
# Command to make the shared library
MAKE_SHARED=["${CXX} ${CXXFLAGS} ${SHARED_OPTIONS}"]
# Command to link an executable
LINK=["${CXX} ${CXXFLAGS} ${LINK_OPTIONS}"]

# Default commands to create the library in the correct place

if test -z "$LINK_COMMAND_1" ; then 
    LINK_COMMAND_1=["(cd \$(LIB_DIR) ; rm -f \$(LIBMAJORNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBMAJORNAME))"]
    LINK_COMMAND_2=["(cd \$(LIB_DIR) ; rm -f \$(LIBSHORTNAME) ; \$(LN_S) \$(LIBNAME) \$(LIBSHORTNAME))"]
fi

# Default commands to install

if test -z "$INSTALL_COMMAND_1" ; then
    INSTALL_COMMAND_1=["\$(INSTALL) -d \$(DESTDIR)\${libdir} && \$(INSTALL) \${THISLIB} \$(DESTDIR)\${libdir}"]
    INSTALL_COMMAND_2=["\$(RM) \$(DESTDIR)\${libdir}/\${LIBMAJORNAME} && \$(LN_S) \${LIBNAME} \$(DESTDIR)\${libdir}/\${LIBMAJORNAME}"]
    INSTALL_COMMAND_3=["\$(RM) \$(DESTDIR)\${libdir}/\${LIBSHORTNAME} && \$(LN_S) \${LIBNAME} \$(DESTDIR)\${libdir}/\${LIBSHORTNAME}"]
fi


# Find Xerces

# Check for Xerces using XERCESCROOT (override any system version)

AC_LANG(C++)

if test $XERCESCROOT; then

  OLDCPPFLAGS=$CPPFLAGS
  CPPFLAGS=["${CPPFLAGS} -I${XERCESCROOT}/include"]

  AC_MSG_CHECKING([for Xerces 2.x or 3.x headers in XERCESCROOT/include])
  AC_TRY_RUN([#include <xercesc/util/XercesVersion.hpp>
    int main(int argc, char **argv) {
      if (XERCES_VERSION_MAJOR < 2)
        return 1;
      else
        return 0;
    }],
    xerces_lib_base=${XERCESCROOT}
    xerces_found=yes
    [AC_MSG_RESULT([found])],
    CPPFLAGS=$OLD_CPPFLAGS
    [AC_MSG_RESULT([no])])

fi

if test -z "$xerces_found" ; then

  # Xerces was not found

  AC_MSG_CHECKING([for Xerces 2.x or 3.x headers in system includes])

  AC_TRY_RUN([#include <xercesc/util/XercesVersion.hpp>
    int main(int argc, char **argv) {
      if (XERCES_VERSION_MAJOR < 2)
        return 1;
      else
        return 0;
    }],
    [AC_MSG_RESULT([found])],
    [AC_MSG_ERROR([Xerces 2.0 not found.  Ensure XERCESCROOT points to base directory of Xerces and Xerces 2.0 or later is available])])

fi

# Where is the Xerces lib?
AC_MSG_CHECKING([for Xerces 2.x or 3.x library naming convention])
OLD_LIBS=$LIBS
if test -z "$xerces_lib_base" ; then
  LIBS="${LIBS} -lxerces-c"
else
  LIBS="-L${xerces_lib_base}/lib -lxerces-c ${LIBS}"
fi

AC_TRY_LINK([#include <xercesc/util/PlatformUtils.hpp>],
   [XERCES_CPP_NAMESPACE_USE XMLPlatformUtils::Initialize(); return 0; ],
   [AC_MSG_RESULT([xerces-c])]
   xerceslib_found=yes,
   [LIBS=$OLD_LIBS])

if test -z "$xerceslib_found" ; then
  if test -z "$xerces_lib_base" ; then
    LIBS="${LIBS} -lxerces-3.0"
  else
    LIBS="-L${xerces_lib_base}/lib -lxerces-3.0 ${LIBS}"
  fi

  AC_TRY_LINK([#include <xercesc/util/PlatformUtils.hpp>],
     [XERCES_CPP_NAMESPACE_USE XMLPlatformUtils::Initialize(); return 0; ],
     [AC_MSG_RESULT([xerces-3.0])]
     xerceslib_found=yes,
     [AC_MSG_RESULT([unable to determine])]
     [AC_MSG_ERROR([cannot link to Xerces library])])
fi

# Find out some properties of the version of Xerces we have

AC_MSG_CHECKING([whether Xerces is 64-bit clean])

AC_TRY_COMPILE([#include <xercesc/framework/MemBufInputSource.hpp>],
    [using namespace XERCES_CPP_NAMESPACE;
      XMLFilePos testvar;
    ],
    [AC_MSG_RESULT([yes])]
    [AC_DEFINE(XSEC_XERCES_64BITSAFE)],
    [AC_MSG_RESULT([no])])

AC_MSG_CHECKING([whether Xerces BinInputStream requires getContentType])

AC_TRY_COMPILE([#include <xercesc/util/BinMemInputStream.hpp>],
    [using namespace XERCES_CPP_NAMESPACE;
      XMLByte buf[1024];
      BinMemInputStream in(buf,1024);
      in.getContentType();
    ],
    [AC_MSG_RESULT([yes])]
    [AC_DEFINE(XSEC_XERCES_INPUTSTREAM_HAS_CONTENTTYPE)],
    [AC_MSG_RESULT([no])])

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
	[AC_DEFINE(XSEC_XERCES_REQUIRES_MEMMGR)]
	[AC_DEFINE(XSEC_XERCES_FORMATTER_REQUIRES_VERSION)],
	[AC_MSG_RESULT([no])])
	
AC_MSG_CHECKING([whether Xerces XMLString has ::release])

AC_TRY_COMPILE([#include <xercesc/util/XMLString.hpp>],
	[using namespace XERCES_CPP_NAMESPACE;
		XMLCh * tst;
		XMLString::release(&tst);
	],
	[AC_MSG_RESULT([yes])]
	[AC_DEFINE(XSEC_XERCES_XMLSTRING_HAS_RELEASE)],
	[AC_MSG_RESULT([no])])

AC_MSG_CHECKING([whether Xerces XMLElement has ::setIdAttribute(XMLCh*)])

AC_TRY_COMPILE([#include <xercesc/dom/DOM.hpp>],
	[using namespace XERCES_CPP_NAMESPACE;
		DOMElement * elt;
		elt->setIdAttribute(NULL);
	],
	[AC_MSG_RESULT([yes])]
    xerces_has_setidattribute=yes
	[AC_DEFINE(XSEC_XERCES_HAS_SETIDATTRIBUTE)],
	[AC_MSG_RESULT([no])])

if test -z "$xerces_has_setidattribute" ; then

  AC_MSG_CHECKING([whether Xerces XMLElement has ::setIdAttribute(XMLCh*, bool)])
  AC_TRY_COMPILE([#include <xercesc/dom/DOM.hpp>],
	  [using namespace XERCES_CPP_NAMESPACE;
		  DOMElement * elt;
		  elt->setIdAttribute(NULL, false);
	  ],
	  [AC_MSG_RESULT([yes])]
      xerces_has_setidattribute=yes
	  [AC_DEFINE(XSEC_XERCES_HAS_BOOLSETIDATTRIBUTE)],
	  [AC_MSG_RESULT([no])])

fi

# For Xerces 3.x we now have a stricter DOM L3 implementation
AC_MSG_CHECKING([whether Xerces DOMImplementationLS has DOMLSSerializer])

AC_TRY_COMPILE([#include <xercesc/dom/DOM.hpp>],
    [using namespace XERCES_CPP_NAMESPACE;
        DOMImplementation *impl = DOMImplementationRegistry::getDOMImplementation(NULL);
        DOMLSSerializer *ls = ((DOMImplementationLS*)impl)->createLSSerializer();
    ],
    [AC_MSG_RESULT([yes])]
    [AC_DEFINE(XSEC_XERCES_DOMLSSERIALIZER)],
    [AC_MSG_RESULT([no])])

AC_MSG_CHECKING([whether Xerces DOMEntity uses getInputEncoding()])

AC_TRY_COMPILE([#include <xercesc/dom/DOM.hpp>],
    [using namespace XERCES_CPP_NAMESPACE;
        DOMEntity *t;
        t->getInputEncoding();
    ],
    [AC_MSG_RESULT([yes])]
    [AC_DEFINE(XSEC_XERCES_DOMENTITYINPUTENCODING)],
    [AC_MSG_RESULT([no])])


# Now check for Xalan

if test x"$use_xalan" != x"no" ; then

  if test $XALANCROOT; then

    AC_MSG_CHECKING([for Xalan headers in XALANCROOT])

    OLD_CPPFLAGS=$CPPFLAGS
    # Updated to include nls/include as this is generally needed for
    # compilation against non-installed xalan.
    # Also now include XALANCROOT/include to cater for installed xalan
    CPPFLAGS=["-I${XALANCROOT}/src -I${XALANCROOT}/include -I${XALANCROOT}/nls/include ${CPPFLAGS}"]

    AC_TRY_CPP([#include <xalanc/Include/XalanVersion.hpp>], 
      xalan_found=yes 
      LIBS="-L${XALANCROOT}/lib -lxalan-c ${LIBS}"
      [AC_MSG_RESULT([found])], 
      CPPFLAGS=$OLD_CPPFLAGS
      [AC_MSG_RESULT([no])]);

  fi

  if test -z "$xalan_found" ; then

    AC_MSG_CHECKING([for Xalan in system includes])
    AC_TRY_CPP([#include <xalanc/Include/XalanVersion.hpp>], 
      xalan_found=yes 
      LIBS="${LIBS} -lxalan-c"
      [AC_MSG_RESULT([found])],
      [AC_DEFINE(XSEC_NO_XALAN)] 
      [AC_MSG_RESULT([no - WARNING - configuring for no Xalan])]);

  fi

else

  # NO_XALAN is defined - so we skip

  AC_MSG_NOTICE([Warning - Xalan being configured out - XPath and XSLT will not be available])
  AC_DEFINE(XSEC_NO_XALAN)

fi

if test "${xalan_found}" = "yes" ; then
  # Do we need xalanMsg.so?
  AC_MSG_CHECKING([if libxalanMsg is required])
  old_libs=$LIBS
  LIBS="${LIBS} -lxalanMsg"
  AC_TRY_LINK([],[
    int test=1;
  ],
  AC_MSG_RESULT([yes]),
  AC_MSG_RESULT([no])
  LIBS=${old_libs});
fi

# Check for handling of XSLException types
if test "$xalan_found" = "yes" ; then

  AC_MSG_CHECKING([whether XSLException::getType() returns XalanDOMString])

  AC_TRY_COMPILE([#include <xalanc/PlatformSupport/XSLException.hpp>], 
    [ try {
          int x = 1;
      } 
      catch (XALAN_CPP_NAMESPACE_QUALIFIER XSLException &e) {
        e.getType().c_str();
      }
    ],
    AC_DEFINE(XSEC_XSLEXCEPTION_RETURNS_DOMSTRING)
    [AC_MSG_RESULT([yes])], 
    [AC_MSG_RESULT([no])]);

  AC_MSG_CHECKING([whether XercesParserLiaison ctor takes XercesDOMSupport])

  AC_TRY_COMPILE([#include <xalanc/XercesParserLiaison/XercesDOMSupport.hpp>
    #include <xalanc/XercesParserLiaison/XercesParserLiaison.hpp>],
	[ 	XALAN_CPP_NAMESPACE_QUALIFIER XercesDOMSupport 
           theDOMSupport;
	    XALAN_CPP_NAMESPACE_QUALIFIER XercesParserLiaison 
           theParserLiaison(theDOMSupport);
	],
	AC_DEFINE(XSEC_XERCESPARSERLIAISON_REQS_DOMSUPPORT)
	[AC_MSG_RESULT([yes])],
	[AC_MSG_RESULT([no])]);

  AC_MSG_CHECKING([whether XPathEvaluator::selectNodeList reqs NodeRefList])

  AC_TRY_COMPILE([#include <xalanc/XercesParserLiaison/XercesDOMSupport.hpp>
	#include <xalanc/XPath/XPathEvaluator.hpp>
	#include <xalanc/XPath/NodeRefList.hpp>
    #include <xalanc/XercesParserLiaison/XercesParserLiaison.hpp>],
	[ 	XALAN_CPP_NAMESPACE_QUALIFIER XercesDOMSupport 
           theDOMSupport;
		XALAN_CPP_NAMESPACE_QUALIFIER XPathEvaluator	theEvaluator;
		XALAN_CPP_NAMESPACE_QUALIFIER NodeRefList	theResult(
			theEvaluator.selectNodeList(
			theDOMSupport,
			NULL,
			NULL,
			NULL));
	],
	[AC_MSG_RESULT([no])],
	AC_DEFINE(XSEC_SELECTNODELIST_REQS_NODEREFLIST)
	[AC_MSG_RESULT([yes])]);

  AC_MSG_CHECKING([whether Xalan classes req MemoryManager])

  AC_TRY_COMPILE([
 	#include <xalanc/XPath/XPathDefinitions.hpp>
    #include <xalanc/XPath/Function.hpp>],
    [
    }
	XALAN_CPP_NAMESPACE_BEGIN

	class FunctionTest : public Function
	{
		public:
 	       typedef Function        ParentType;

    	    FunctionTest();

        	virtual ~FunctionTest();      
	        virtual XObjectPtr
    	    execute(
        	   XPathExecutionContext& executionContext,
               XalanNode* context,
               const XObjectArgVectorType& args,
               const LocatorType* locator) const;

            XObjectPtr
            execute(
                XPathExecutionContext&          executionContext,
                XalanNode*                                      context,
                const XObjectPtr                        arg1,
                const XObjectPtr                        arg2,
                const LocatorType*                      locator) const;

            XObjectPtr
            execute(
                XPathExecutionContext&          executionContext,
                XalanNode*                                      context,
                   
                const XObjectPtr                        arg1,
                const XObjectPtr                        arg2,
                const XObjectPtr                        arg3,
                const LocatorType*                      locator) const;

	#if !defined(XALAN_NO_USING_DECLARATION)
        using ParentType::execute;
	#endif

	#if defined(XALAN_NO_COVARIANT_RETURN_TYPE)
        virtual Function*
	#else
        virtual FunctionTest*
	#endif
        clone() const;

	protected:

        virtual const XalanDOMString
        getError() const;

    private:

        // Not implemented...
        FunctionTest&
        operator=(const FunctionTest&);

        bool
        operator==(const FunctionTest&) const;
	};

  void main2(void){

	FunctionTest t;

  }

XALAN_CPP_NAMESPACE_END
  int main3(void) {

	],
	[AC_MSG_RESULT([no])],
	AC_DEFINE(XSEC_XALAN_REQS_MEMORYMANAGER)
	[AC_MSG_RESULT([yes])]);

fi

# AC_LANG(C)

# Check for OpenSSL
if test $OPENSSL ; then

# First check includes

AC_MSG_CHECKING([for OpenSSL headers in $OPENSSL])

OLD_CPPFLAGS=$CPPFLAGS
CPPFLAGS=["${CPPFLAGS} -I${OPENSSL}/include"]

AC_TRY_CPP([#include <openssl/opensslv.h>], 
   openssl_found=yes 
   AC_DEFINE(HAVE_OPENSSL)
   [AC_MSG_RESULT([found])], 
   [AC_MSG_ERROR(["Unable to find OpenSSL headers"])]);

# Now check libs
# Note we keep both the base OPENSSL dir and /lib to handle
# both an installed version or a source dir

OLD_LIBS=$LIBS
LIBS=["${LIBS} -L${OPENSSL} -lcrypto"]

# As of 1.3 - use AC_TRY_LINK to check as AC_CHECK_LIB does
# the wrong thing

AC_MSG_CHECKING([for libcrypto])
AC_TRY_LINK([#include <openssl/evp.h>],
   [EVP_EncryptInit(NULL, NULL, NULL, NULL) ; return 0; ],
   [AC_MSG_RESULT([found])]
   openssllib_found=yes,
   [LIBS=$OLD_LIBS])

   if test -z "$openssllib_found" ; then

   LIBS=["${LIBS} -L${OPENSSL}/lib -lcrypto"]
   AC_TRY_LINK([#include <openssl/evp.h>],
     [EVP_EncryptInit(NULL, NULL, NULL, NULL) ; return 0; ],
      [AC_MSG_RESULT([found])]
      openssllib_found=yes,
      [LIBS=$OLD_LIBS]
          [AC_MSG_RESULT([no])]
	  [AC_MSG_ERROR(["Unable to find libcrypto"])])

  fi

fi

if test -z "$openssl_found" ; then

    AC_PATH_PROG(PKG_CONFIG, pkg-config)
    if test "x$PKG_CONFIG" != x && test "x$PKG_CONFIG" != "xno" ; then
        if pkg-config openssl ; then
            CPPFLAGS="$CPPFLAGS `$PKG_CONFIG --cflags openssl`"
            LIBS="$LIBS `$PKG_CONFIG --libs openssl`"
        else
            AC_MSG_WARN([OpenSSL not supported by pkg-config, using default settings])
            LIBS="${LIBS} -lcrypto"
        fi
    else
        LIBS="${LIBS} -lcrypto"
    fi

    AC_MSG_CHECKING([for OpenSSL in system includes])
    AC_TRY_CPP([#include <openssl/opensslv.h>], 
       openssl_found=yes 
       AC_DEFINE(HAVE_OPENSSL)
       [AC_MSG_RESULT([found])],
       [AC_MSG_RESULT([no])]
       [AC_MSG_ERROR([OpenSSL header files not found.  Please define OPENSSL])]);

fi

# Now try to find out some things about this version of OpenSSL

AC_LANG(C++)

AC_MSG_CHECKING([for const input buffers in OpenSSL])
AC_TRY_COMPILE([#include <openssl/rsa.h>],
	[ const unsigned char * buf;
	  unsigned char * outbuf;
	  RSA rsa;

		RSA_private_decrypt(1,
							buf,
							outbuf,
							&rsa,
							RSA_PKCS1_PADDING);

	],
	[AC_MSG_RESULT([yes])]
	[AC_DEFINE(XSEC_OPENSSL_CONST_BUFFERS)],
	[AC_MSG_RESULT([no])])

# Check for full strength AES - Solaris 10 install is broken
# and only supports low end AES.  As the library does not have
# this granularity, we have to disable AES in this instance.

# AC_CHECK_DECL(EVP_aes_256_cbc,
# 	[AC_DEFINE(XSEC_OPENSSL_HAVE_AES)],
# 	,[#include <openssl/evp.h>])

AC_MSG_CHECKING([for non-broken AES support])
AC_TRY_LINK([#include <openssl/evp.h>],
	[ EVP_aes_256_cbc();
	],
	[AC_MSG_RESULT([yes])]
	[AC_DEFINE(XSEC_OPENSSL_HAVE_AES)],
	[AC_MSG_RESULT([no])])

AC_CHECK_DECL(EVP_PKEY_set1_EC_KEY,
    [AC_DEFINE(XSEC_OPENSSL_HAVE_EC)],
    ,[#include <openssl/evp.h>])

AC_CHECK_DECL(EVP_CIPHER_CTX_set_padding,
	[AC_DEFINE(XSEC_OPENSSL_CANSET_PADDING)],
	,[#include <openssl/evp.h>])

AC_CHECK_DECL(CRYPTO_cleanup_all_ex_data,
	[AC_DEFINE(XSEC_OPENSSL_HAVE_CRYPTO_CLEANUP_ALL_EX_DATA)],
	,[#include <openssl/crypto.h>])

AC_MSG_CHECKING([for const input buffer in loadX509Base64Bin])
AC_TRY_COMPILE([#include <openssl/x509.h>],
	[ const unsigned char * buf;
	  d2i_X509(NULL, &buf, 1);
	],
	[AC_MSG_RESULT([yes])]
	[AC_DEFINE(XSEC_OPENSSL_D2IX509_CONST_BUFFER)],
	[AC_MSG_RESULT([no])])

# String functions
AC_CHECK_DECL(_strdup,
	[AC_DEFINE(XSEC_HAVE__STRDUP)],
	,[#include <string.h>])

AC_CHECK_DECL(_stricmp,
	[AC_DEFINE(XSEC_HAVE__STRDUP)],
	,[#include <string.h>])

AC_CHECK_DECL(_getcwd,
	[AC_DEFINE(XSEC_HAVE__GETCWD)],
	,[#include <string.h>])

AC_CHECK_DECL(_getcwd,
	[AC_DEFINE(XSEC_HAVE__GETCWD)],
	,[#include <string.h>])

AC_CHECK_DECL(stricmp,
	[AC_DEFINE(XSEC_HAVE_STRICMP)],
	,[#include <string.h>])

AC_CHECK_DECL(strcasecmp,
	[AC_DEFINE(XSEC_HAVE_STRCASECMP)],
	,[#include <string.h>])

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

# Populate the Makefile conditionals
AM_CONDITIONAL(XSEC_AM_HAVE_OPENSSL, test x$openssl_found = xyes)

# Versioning
package_version=${PACKAGE_VERSION}
package_libtool_version="${package_version_major}${package_version_medium}:${package_version_minor}"
AC_SUBST(package_version)
AC_SUBST(package_version_major)
AC_SUBST(package_libtool_version)
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
