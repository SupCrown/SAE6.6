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
communication reseau en string donc dificile à utiliser sans une documentation precise.
dans Env2DViewer.java Le code tente de masquer ses problèmes de concurrence en masquant les exeptions de concurrence dans des blocs try catch, ce qui n'est pas une bonne pratique.
dans environnement2d des boucle while sont utilisées pour forcer le code à attendre. le thread principal est donc monopilé pour rien.

De plus, de la documentation et des commentaires seraient les bienvenus sur les parties les plus complexes de 
l'application.