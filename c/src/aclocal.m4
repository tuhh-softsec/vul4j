#
# Custom m4 macros for XSEC builds
#

# AC_CREATE_OBJ_FILELIST(DIR, FILESPEC, FIND, REPLACE, PATH)
# -------------------------------------------
# Use the FILESPEC to find a series of files in DIR and then
# replace the FIND part of each file with REPLACE (i.e. change
# .cpp to .o.  Finally prepend PATH to each obj file
AC_DEFUN(AC_CREATE_OBJ_FILELIST,
[AC_MSG_NOTICE([Creating object file list from files in $1])
ac_find_files=""
for fl in `(cd $1; ls $2)`
do
  fl_rep=`echo $fl | sed "s/\$3/$4/"`
  fl_rep_full="$5/${fl_rep}"
  ac_find_files="${ac_find_files} ${fl_rep_full}"
done])

# AC_CREATE_FILELIST(DIR, FILESPEC)
# -------------------------------------------
# Use the FILESPEC to find a series of files in DIR and then
# build a variable with each file
AC_DEFUN(AC_CREATE_FILELIST,
[AC_MSG_NOTICE([Creating file list from $2 files in $1])
ac_find_files=""
for fl in `(cd $1; ls $2)`
do
  ac_find_files="${ac_find_files} ${fl}"
done])

# AC_REPLACE_STRING(STRING, FIND, REPLACE)
# ----------------------------------------
# Find all occurences of FIND in STRING and replace with REPLACE
# and place in ac_replace_string
AC_DEFUN(AC_REPLACE_STRING,
[ac_replace_string=`echo $1 | sed "s/$2/$3/g"`])


