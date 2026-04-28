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

De plus, de la documentation et des commentaires seraient les bienvenus sur les parties les plus complexes de 
l'application.