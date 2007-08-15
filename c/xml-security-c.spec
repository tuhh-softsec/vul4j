Name:           xml-security-c
Version:        1.4.0
Release:        1
Summary:        C++ XML security library

Group:          System Environment/Libraries
License:        Apache Software License
URL:            http://xml.apache.org/security/c/
Source0:        http://xml.apache.org/security/dist/c-library/%{name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

BuildRequires:  xerces%{?xercesver}-c-devel >= 2.3, openssl-devel
%{?_with_xalan:BuildRequires: xalan-c-devel >= 1.6}

%description
The Apache %{summary}.

Non-default rpmbuild options:
"--with xalan":   use the Xalan XSLT processor.

%package        devel
Summary:        Development files for the Apache C++ XML security library
Group:          Development/Libraries
Requires:       %{name} = %{version}-%{release}

%description    devel
%{summary}.

%package        docs
Summary:        Developer documentation for the Apache C++ XML security library
Group:          Documentation

%description    docs
%{summary}.


%prep
%setup0 -q

%build
%configure %{!?_with_xalan: --without-xalan}
make # %{?_smp_mflags} # fails as of 1.[01].0.


%install
rm -rf $RPM_BUILD_ROOT
mkdir -pm 755 $RPM_BUILD_ROOT%{_libdir} # FIXME in Makefiles
make install DESTDIR=$RPM_BUILD_ROOT


%clean
rm -rf $RPM_BUILD_ROOT


%post -p /sbin/ldconfig
%postun -p /sbin/ldconfig


%files
%defattr(-,root,root,-)
%doc LICENSE NOTICE
%{_libdir}/*.so.*
%{_libdir}/*.a
%exclude %{_libdir}/*.la
%{_bindir}/*

%files devel
%defattr(-,root,root,-)
%{_includedir}/xsec
%{_libdir}/*.so

%files docs
%defattr(644,root,root,755)
%doc doc/c/*


%changelog
* Wed Aug 15 2007   Scott Cantor  <cantor.2@osu.edu> 1.4.0-1
- update to 1.4.0
* Mon Jun 11 2007   Scott Cantor  <cantor.2@osu.edu> 1.3.1-1
- update to 1.3.1
* Thu Mar 23 2006   Ian Young     <ian@iay.org.uk> - 1.2.0-2
- patch to remove extra qualifications for compat with g++ 4.1
* Sun Jul 03 2005   Scott Cantor  <cantor.2@osu.edu> - 1.2.0-1
- Updated version.
* Mon Oct 19 2004   Derek Atkins  <derek@ihtfp.com> - 1.1.1-1
- First Package.
