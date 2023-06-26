## Etape 1

Il y a différentes sortes de SortedMap : notamment TreeMap qui trie en fonction des clés. 

J'ai utilisé une HashMap et le tri par défaut de Java en précisant la comparaison utilisée.

---

### deontologie_police_nationale.txt

Les mots les plus fréquents dans  sont :

de=98, la=50, police=35, des=33, et=33

**Remarque : je mets tous les mots en minuscule dans tous les fichiers.**

---

### domaine_public_fluvial.txt

Les mots les plus fréquents sont :

de=630, le=425, la=364, du=347, et=266

---

### sante_publique.txt

Les mots les plus fréquents sont :

de=190781, la=82333, des=67761, à=65500, les=62322

Comptage des mots temps total (sur mon ordi): 741ms

Tri temps total (sur mon ordi): 107ms

---

### fichier du commoncrawl

Les mots les plus fréquents dans le fichier commun récupéré sur le premier fichier du commoncrawl : 

the=448016, to=304221, and=291146, a=280111, de=265616

Comptage des mots temps total (sur ordi de l'école): 10328ms

Tri temps total (sur ordi de l'école): 3007ms


## Etape Suivantes


J'ai implementé le double map reduce en utilisant une TreeMap pour le sort du dernier reduce.
Pour équilibrer la quantité de données envoyée aux slaves, j'ai utilisé un hash pour le 2ème reduce, même si ça donne plus de travail au master quand il trie.

J'ai testé mon programme sur un corpus de 6 fichiers (les fichiers CC 00006 à 00011). J'ai executé le map reduce avec 1, 2, 3 et 6 slaves.

Lest résultats détaillés sont dans le tableau suivant.

               |---------|----------|----------|----------|
               | 1 slave | 2 slaves | 3 slaves | 6 slaves |
    |----------|---------|----------|----------|----------|
    |   TOTAL  |         |          |          |          |
    |----------|---------|----------|----------|----------|
    |   Map    |         |          |          |          |
    |----------|---------|----------|----------|----------|
    |  Shuffle |         |          |          |          |
    |----------|---------|----------|----------|----------|
    |  Reduce  |         |          |          |          |
    |----------|---------|----------|----------|----------|
    |   Map2   |         |          |          |          |
    |----------|---------|----------|----------|----------|
    | Shuffle2 |         |          |          |          |
    |----------|---------|----------|----------|----------|
    |  Reduce2 |         |          |          |          |
    |----------|---------|----------|----------|----------|       