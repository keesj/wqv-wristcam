#!/bin/sh
for i in *.fig
do
	fig2dev -L gif -b 10 -t white -m 1.3 $i `basename $i .fig`.gif
done
