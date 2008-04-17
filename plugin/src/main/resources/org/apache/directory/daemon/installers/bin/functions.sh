#!/bin/sh

#------------------------------------------------------------------------------
# Updates a variables that will be exported. The question, the variable name, 
# the default value and the mandatory aspect of the input are stored in the 
# "variables.sh" file under the following form:
#   <question> | <variable> | [<default value>] | [<mandatory>]
#------------------------------------------------------------------------------
ask_param()
{
    # Skipping empty lines
    if [ -z "$1" ]
    then 
        return
    fi

    # Searching for the current line (which is not a comment)
    curline=`cat -n variables.sh | grep -e "^[[:space:]]*$1[[:space:]][[:space:]]*" | sed -e 's/^[^a-aA-Z|#-]*//'`
    comment=`echo $curline | grep -e "^\#"`

    # Skipping comments
    if [ -n "$comment" ]
    then
        return
    fi
  
    # Getting question, variable, default and mandatory values
    question=`echo $curline | cut -d'|' -f1 | sed -e 's/^[ ]*//' -e 's/[ ]*$//'`   # The question
    variable=`echo $curline | cut -d'|' -f2 | sed -e 's/^[ ]*//' -e 's/[ ]*$//'`   # The variable
    default=`echo $curline | cut -d'|' -f3 | sed -e 's/^[ ]*//' -e 's/[ ]*$//'`     # The default value
    mandatory=`echo $curline | cut -d'|' -f4 | sed -e 's/^[ ]*//' -e 's/[ ]*$//'`  # Mandatory input ?
  
    # Use the commands below for debug
    #echo "Current Line  = " $curline
    #echo "Question      = " $question
    #echo "Variable      = " $variable
    #echo "Default value = " $default
    #echo "Mandatory     = " $mandatory
  
    # The line must include a variable
    if [ -z "$variable" ]
    then
        return
     fi

    # If there is no question, we update the variable and we return
    if [ -z "$question" ]
    then
        var=`eval echo $default`
        export $variable="$var"
        return
    fi
 
    # Removing extra spaces from mandatory
    if [ -n "$mandatory" ]
    then
        mandatory=`echo $mandatory | sed -e 's/[[:space:]*M[[:space:]]*/M/'`
    fi

    # Let's ask the question
    if [ -n "$default" ]
    then
        printf "$question? [Default: ";
        printf `eval echo $default`;
        printf "]\n"
    else
        echo "$question?"
    fi

    # Reading anwser
    read read_answer
    while [ -z "$read_answer" ]
    do
        if [ "x$mandatory" = "xM" ]
        then
            echo "You have to anwser, please."
            if [ -n "$default" ]
            then
                printf "$question? [Default: ";
                printf `eval echo $default`;
                printf "]\n"
            else
                echo "$question?"
            fi
            read read_answer
        else
            # If the user does not enter any input and the input is not 
            # mandatory, we use the default value
            read_answer=`eval echo $default`
        fi
    done

    # Exporting the variable
    export $variable="$read_answer"
}

#------------------------------------------------------------------------------
# Verifies the exit code of the last command used. If the exit code is 0, the
# execution continues, if not the execution is halted and we exit the program
# with a 1 value.
#------------------------------------------------------------------------------
verifyExitCode()
{
    if [ $? -ne 0 ]
    then    
		echo "An error occurred when installing Apache DS."
		echo "Apache DS installation failed."
		exit 1
    fi
}