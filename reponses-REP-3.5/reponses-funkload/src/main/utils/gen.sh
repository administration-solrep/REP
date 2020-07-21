#!/bin/sh

max=200

outadd=./all-add.ldiff
outmod=./all-mod.ldiff

rm -f $outadd
rm -f $outmod

i=0
while [ $i -lt $max ]; do

  str=$(printf "%03d" $i)

  sed "s/INDEX/$str/g" add-funkload.ldiff >> $outadd
  sed "s/INDEX/$str/g" modif-funkload.ldiff >> $outmod


  let i++
done
