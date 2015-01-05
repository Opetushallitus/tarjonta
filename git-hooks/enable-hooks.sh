#!/bin/sh
# Tämä skripti asentaa tarjonnan git hookit
# Suorita juurikansiossa => $: ./git-hooks/enable-hooks.sh
ln -s ../../git-hooks/pre-commit .git/hooks/pre-commit
