# SAE 6.A.01

## Analyse de l’existant

### Installation du projet

Le projet se lance sans encombre.

### Structure générale



### Qualité

Le code semble plutôt lisible bien que du code fantôme gâche la lecture comme 
[preRunOps](/users/but/info/delbovea/Documents/SAE/SAE6/sae6a01_beroud_bloise_delbove_kebaili/environment/maps/MAP_FFA.java)() 
[postRepetitionOps](/users/but/info/delbovea/Documents/SAE/SAE6/sae6a01_beroud_bloise_delbove_kebaili/environment/Environment2D.java)() 
et [preStepOps](/users/but/info/delbovea/Documents/SAE/SAE6/sae6a01_beroud_bloise_delbove_kebaili/environment/Environment2D.java)() 
et que certains noms de variables manquent de sens comme des variables référencées par un simple caractère qui parfois 
n'a aucun lien implicite avec le nom de la classe associée (ex: `A_Kicker s`, dans la méthode drawKicker() de la 
classe [Anv2DViewer](/users/but/info/delbovea/Documents/SAE/SAE6/sae6a01_beroud_bloise_delbove_kebaili/Env2DViewer.java)).

Communication reseau en string donc dificile à utiliser sans une documentation precise.

Dans Env2DViewer.java le code tente de masquer ses problèmes de concurrence en masquant les exeptions de concurrence dans des blocs try catch, ce qui n'est pas une bonne pratique.

Dans environnement2d des boucle while sont utilisées pour forcer le code à attendre. Le thread principal est donc monopilé pour rien.

De plus, de la documentation et des commentaires seraient les bienvenus sur les parties les plus complexes de 
l'application.

## Focus sur le problème des `try catch`.

En ce qui concerne l'utilisation abusive de `try catch`, elle fait partie des prolbèmes les plus importants. En effet, 
bien que l'application ne plante pas, la gestion des erreur reste primordiale. 

## Améliorations prévues

### Améliorations basiques 

 - Classer l'affichage du score
 - Nombre de morts
 - Nombre de vies
 - Détecter les projectiles sans détecter les siens
 - Ne plus détecter les joueurs morts
 - Gestion des équipes

### Autre mode de jeu

 - Capture de points (il y en a 3)
 - Death Match par équipes

## Choix techniques

### Repirse de l'existant

Comme la période de travail de ce semestre est réduite et que notre objectif est d'étendre les possibilités et les 
fonctionnalités de l'application, le choix de garder les technologies actuellement utilisées est le plus judicicieux.
En effet, l'applicaiton a été concue en Java, langage que nous maîtrisons en partie. La montée en compétence est
donc minimale et nous laisse le tmeps de nous concentrer sur les fonctionnalités que nous avons décidés de concevoir.
