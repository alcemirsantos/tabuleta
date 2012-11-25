#!/bin/bash
rm -rf update
git checkout master "br.ufmg.dcc.tabuleta.site"
git reset HEAD
mv "br.ufmg.dcc.tabuleta.site" update

