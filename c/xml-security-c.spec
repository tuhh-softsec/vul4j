Name:           xml-security-c
Version:        1.4.1
Release:        1
Summary:        C++ XML security library

Group:          System Environment/Libraries
License:        Apache Software License
URL:            http://xml.apache.org/security/c/
Source0:        http://xml.apache.org/security/dist/c-library/%{name}-%{version}.tar.gz
BuildRoot:      %{_tmppath}/%{name}-%{version}-%{release}-root-%(%{__id_u} -n)

%if 0%{?suse_version} > 1030
BuildRequires:  libXerces-c-devel >= 2.8.0
%else
BuildRequires:  xerces%{?xercesver}-c-devel >= 2.3
%endif
BuildRequires:  openssl-devel
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

%prep
%setup0 -q

%build
%configure %{!?_with_xalan: --without-xalan}
%{__make} # %{?_smp_mflags} # fails as of 1.[01].0.

%install
%{__rm} -rf $RPM_BUILD_ROOT
%{__mkdir} -pm 755 $RPM_BUILD_ROOT%{_libdir} # FIXME in Makefiles
%{__make} install DESTDIR=$RPM_BUILD_ROOT

%clean
%{__rm} -rf $RPM_BUILD_ROOT


%post -p /sbin/ldconfig
%postun -p /sbin/ldconfig


%files
%defattr(-,root,root,-)
%{_libdir}/*.so.*
%{_libdir}/*.a
%exclude %{_libdir}/*.la
%{_bindir}/*

%files devel
%defattr(-,root,root,-)
%{_includedir}/xsec
%{_libdir}/*.so

%changelog
* Fri Sep 19 2008   Scott Cantor  <cantor.2@osu.edu> 1.4.1-1
- update to 1.4.1
- fix Xerces dependency name on SUSE
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
