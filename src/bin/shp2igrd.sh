#!/bin/sh

validate_double() {
    if [[ "$1" =~ ^[0-9]*\.[0-9]+$ ]]; then
	echo 0
    else
         echo 1
    fi
}

SHP_PATH=
DBF_PATH=
SHX_PATH=
IGRD_PATH=
SILENT=0
ADD_KEY=0
EXEC_PROGRAM=
LAT_INTERVAL=
LON_INTERVAL=
ERROR_LOG_PATH=

# parse out command line arguments
errors=0

while test $# -gt 0; do
    value="$1"
    case $value in
        --shp)
	    shift
	    if [ -z $1 ]; then
	        echo "Shape file not found or empty"
                (( errors = errors + 1 ))
            else
	        SHP_PATH=$1
            fi
	    shift ;;

        --lat)
            shift
            if [ `validate_double $1` -eq 1 ]; then
	        echo "Error: --lat option requires a positive floating point argument"
		(( errors = errors + 1 ))
            else
                LAT_INTERVAL=$1
            fi
	    shift ;;

        --lon)
            shift
            if [ `validate_double $1` -eq 1 ]; then
	        echo "Error: --lon option requires a positive floating point argument"
		(( errors = errors + 1 ))
            else
                LON_INTERVAL=$1
            fi
	    shift ;;

	--igrd)
            shift
            IGRD_PATH=$1
            shift ;;

	--quiet|-q)
	   SILENT=1
	   shift ;;
	
        --verbose|-v)
            SILENT=0
            shift ;;
  
        --add-key)
            ADD_KEY=1
            shift ;;

        --index-replacer)
            shift
            EXEC_PROGRAM=$1
            shift ;;

    esac
done

if [ $errors -gt 0 ]; then
    echo "Exiting due to invalid input"
    exit 1
fi
