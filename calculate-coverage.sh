#!/bin/bash

cov_missed=0
cov_covered=0

for report in $( find . -name jacoco.xml | grep -v "/staging/" ); do
	module_cov=$(grep -o -e "<counter [^/]*LINE[^/]*/>" $report | tail -n 1)
	echo $( echo $report | sed -e 's#\(.*\)/target/.*#\1#g' ) - $module_cov
	cov_missed=$((  $( echo "$module_cov" | grep -o -e 'missed="[0-9]*"'  | grep -o -e "[0-9]*" ) + $cov_missed  ))
	cov_covered=$(( $( echo "$module_cov" | grep -o -e 'covered="[0-9]*"' | grep -o -e "[0-9]*" ) + $cov_covered ))	 
done

cov_total=$(( $cov_missed + $cov_covered ))
echo  $cov_covered / $cov_total
if [ $cov_total == 0 ]; then
	echo "Total coverage: 100.0%"
else
	cov_percent=$(( $cov_covered * 1000 / $cov_total ))
	cov_percent_round=$(( $cov_percent / 10 ))
	cov_percent_float=$(( $cov_percent - ( $cov_percent_round * 10 ) ))
	
	echo "Total coverage: $cov_percent_round.$cov_percent_float%"
fi
