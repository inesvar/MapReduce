# Implémentation de MapReduce en Java

## Qu'est-ce que le MapReduce ?

"MapReduce est un patron de conception de développement informatique, inventé par Google, dans lequel sont effectués des calculs parallèles, et souvent distribués, de données potentiellement très volumineuses"</br>source : https://fr.wikipedia.org/wiki/MapReduce

Concrètement et façon simplifiée, il s'agit pour la machine "maître" de séparer une tâche principale en sous-tâches qui peuvent être executées en parallèle sur différentes machines "esclaves". L'atout du MapReduce est d'utiliser cette structure maître-esclave de façon récursive ce qui permet de réduire la charge de travail liée à la séparation des données et à la reconstruction.

## Description des fonctionnalités

Cette implémentation est un double Map Reduce non-récursif qui permet de compter puis trier les mots d'un fichier (ou plusieurs) par order d'apparition. La taille des fichiers à traiter peut monter jusqu'à l'ordre de 1 Go en fonction des machines.

Le premier Map Reduce produit une hashmap qui associe tous les mots à leur nombre d'occurences, et le deuxième associe des nombres d'occurences à des listes de mots.

Le rapport du TP est [là](./rapport/Descriptif.md), il contient des observations et une preuve empirique de la loi d'Amdahl.

Les esclaves et le maîtres communiquent entre eux par TCP.

## Exécuter le code

Ce code est destiné à être exécuté sur un réseau de machines accessibles en SSH (voir le [script d'exécution](./mapReduce.sh)). J'ai réalisé les mesures de temps présentées dans le rapport sur les machines de mon école, et pour ré-exécuter ce script, il faudrait le modifier pour qu'il s'applique aux spécificités de la situation.

Il est toutefois possible d'exécuter le code facilement en local pour avoir une démonstration simple. 
```bash
./localMapReduce.sh
```

Le fichier lu est cet [exemple](./slave/example.txt) et le résultat est présent [ici](./master/output.txt).

Comme il s'agit juste d'une simplification du code distribué, le maître et les esclaves locaux communiquent toujours par TCP même si cela n'est plus nécessaire.