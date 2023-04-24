ant -q
echo '\nEXECUTION using parameters : '$@'\n'
java -cp target Main $@
echo '\n'
