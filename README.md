# SAE6A01 — Android Game Server

Serveur de jeu 2D multijoueur en Java. Des bots contrôlés à distance (via TCP) s'affrontent dans une arène physique simulée par **jBox2D**. Le serveur expose plusieurs modes de jeu ; les clients (bots Android ou toute appli réseau) envoient des commandes texte et reçoivent des informations sur l'environnement en retour.

**Équipe :** Beroud · Bloise · DelBove · Kebaili

---

## Table des matières

1. [Architecture générale](#architecture-générale)
2. [Modes de jeu](#modes-de-jeu)
3. [Structure du projet](#structure-du-projet)
4. [Prérequis & compilation](#prérequis--compilation)
5. [Lancement](#lancement)
6. [Protocole réseau](#protocole-réseau)
7. [Commandes disponibles](#commandes-disponibles)
8. [IA intégrées](#ia-intégrées)
9. [Paramétrage](#paramétrage)

---

## Architecture générale

```
                     ┌─────────────────────┐
                     │   GameModeMenu      │  ← fenêtre de sélection du mode
                     └────────┬────────────┘
                              │ lance
                     ┌────────▼────────────┐
                     │  AndroidGameServer  │  ← boucle principale (runTask)
                     └──┬─────────────────┘
          ┌─────────────┼──────────────────┐
          │             │                  │
   ┌──────▼──────┐ ┌────▼─────┐  ┌────────▼───────┐
   │ ServerThread│ │ AppFrame │  │  Env2DViewer   │
   │  (TCP :6969)│ │  (GUI)   │  │  (rendu 2D)    │
   └──────┬──────┘ └──────────┘  └────────────────┘
          │ 1 thread / client
   ┌──────▼──────┐
   │ClientHandler│  ← parse les commandes, répond aux requêtes
   └──────┬──────┘
          │ possède
   ┌──────▼──────┐
   │  BotBody2D  │  ← corps physique dans jBox2D
   │  (WarBot)   │
   └─────────────┘
```

La simulation tourne dans le thread principal (`runTask`). Chaque client réseau a son propre thread (`ClientHandler`) qui écrit les valeurs d'actionneurs et lit des informations de l'environnement de manière thread-safe.

---

## Modes de jeu

### Free For All (FFA)

Chaque bot combat seul contre tous. Le score est le nombre de kills individuels. Le classement s'affiche en temps réel dans la fenêtre de rendu. Un score limite optionnel met fin à la partie (`scoreLimit` dans les options).

- Carte : `MAP_FFA`
- Équipe : chaque bot a sa propre équipe (couleur aléatoire)
- Tir ami : désactivé (un bot ne peut pas se tirer dessus lui-même)

### Capture the Points (CTP)

Deux équipes fixes (Rouge et Bleu) s'affrontent pour capturer et tenir deux zones (ALPHA et BETA). Une zone est capturée en y restant ; elle est bloquée si les deux équipes s'y trouvent simultanément. La première équipe à contrôler les deux zones à 100 % gagne.

- Carte : `MAP_CTP` (hérite de `MAP_FFA`)
- Équipes : Red / Blue, affectation automatique à l'équipe la moins peuplée
- Zones : deux `Area` positionnées aléatoirement, suffisamment éloignées l'une de l'autre

### Team Death Match (TDM) 

Deux équipes fixes (Rouge et Bleu). Le score d'une équipe est la somme des kills de ses membres. La première équipe à atteindre le score limite gagne. Le tir ami est désactivé.

- Carte : `MAP_TDM` (hérite de `MAP_FFA`)
- Équipes : Red / Blue, affectation automatique à l'équipe la moins peuplée

---

## Prérequis & compilation

- **Java** 17 ou supérieur
- **jBox2D** 2.2.1.1 (inclus dans `lib/`)

Compilation depuis la racine du projet :

```bash
javac -cp lib/jbox2d-library-2.2.1.1.jar -d out $(find . -name "*.java" ! -path "./.git/*")
```

---

## Lancement

```bash
java -cp out:lib/jbox2d-library-2.2.1.1.jar androidgameserver.AndroidGameServer
```

Une fenêtre de menu s'affiche pour choisir le mode de jeu. En mode FFA le serveur démarre immédiatement ; en mode CTP ou TDM, une fenêtre d'attente s'ouvre jusqu'à ce qu'au moins 2 joueurs/bots soient connectés.

Le serveur écoute sur le port **6969** (TCP).

---

## Protocole réseau

La communication est en **texte UTF-8**, une commande par ligne (`\n`).

- Plusieurs commandes peuvent être envoyées dans le même message, séparées par `#`.
- Les arguments sont séparés de la clé par `=`.
- Les réponses du serveur utilisent le même séparateur `#` quand plusieurs valeurs sont retournées.

Exemple d'envoi :
```
NAME=MonBot#MotL=0.8#MotR=0.6#GunTrig=1.0
```

---

## Commandes disponibles

### Commandes d'action (écriture)

| Commande | Argument | Description |
|----------|----------|-------------|
| `NAME` | `string` | Définit le nom du bot |
| `COL` | `r g b` (entiers) | Définit la couleur du bot |
| `EXIT` | — | Déconnecte le bot proprement |
| `LIVE` | — | Signal keepalive (réinitialise le timeout) |
| `MSG` | `string` | Affiche un message au-dessus du bot (max 40 car.) |
| `SKN` | paires de flottants `[-3,3]` | Skin personnalisé (liste de points 2D) |
| `MotL` | `[0,1]` | Moteur gauche (0 = arrière, 0.5 = arrêt, 1 = avant) |
| `MotR` | `[0,1]` | Moteur droit |
| `GunTrig` | `[0,1]` | Déclencheur du canon (> 0.5 = tir) |
| `GunTrav` | `[0,1]` | Traverse du canon (orientation du tir) |

### Commandes de requête (lecture)

| Commande | Argument | Réponse |
|----------|----------|---------|
| `CBOT` | — | Info sur le bot ennemi le plus proche |
| `CPROJ` | — | Info sur le projectile ennemi le plus proche |
| `NBOT` | `string` (nom) | Info sur le bot portant ce nom |
| `NLIST` | — | Liste des noms de bots séparés par `=` |
| `ORIENT` | — | Orientation normalisée `[0,1]` du bot |
| `USRMSG` | `string` (nom) | Dernier message affiché par ce bot |

En l'absence de résultat, le serveur répond `EMPTY`.

---

## IA intégrées

Accessibles depuis le menu **AI Bots** de la fenêtre de contrôle :

| IA | Comportement |
|----|-------------|
| **AIDumb** | Tire en continu mais ne se déplace pas |
| **AIRandom** | Se déplace aléatoirement et tire, change de direction toutes les 200 ticks |

---

## Paramétrage

Toutes les constantes sont dans `AppDefines.java` et modifiables à chaud via **General → Options** dans la fenêtre de contrôle :

| Paramètre | Défaut | Description |
|-----------|--------|-------------|
| `maxLives` | 3 | Nombre de vies par bot |
| `deathRespawnTime` | 600 ticks | Délai de respawn après mort |
| `targetFPS2DPhysics` | 30 | Pas de simulation physique |
| `arenaWidth` / `arenaHeight` | 100 / 80 | Demi-dimensions de l'arène |
| `ObstacleProbability` | 0.3 | Probabilité de génération d'un obstacle par cellule |
| `bot_RateOfFire` | 100 ticks | Cadence de tir minimale |
| `penaltyTime` | 1000 ticks | Durée de la pénalité (blessure sans mort) |
| `connectionTimeoutDelay` | 10 000 ms | Délai avant déconnexion automatique |
| `messageDuration` | 1000 ticks | Durée d'affichage des messages des bots |
| `scoreLimit` | -1 (infini) | Score à atteindre pour gagner (-1 = désactivé) |
