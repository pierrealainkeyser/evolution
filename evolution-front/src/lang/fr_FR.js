export default {
  login: {
    title: 'Identification',
    chooseOpenId: 'Choissisez un fournisseur OpenID Connect pour vous identifier.',
    form: 'Choissisez un nom d\'utilisateur',
    formSubmit: 'Se connecter',
    user: 'Nom d\'utilisateur',
    retry: 'Réssayer',
    error: 'Erreur durant l\'identifiation'
  },
  lobby: {
    newGame: {
      'new': 'Nouvelle partie',
      title: 'Créer une nouvelle partie',
      quickplay: 'Jeu rapide',
      players: 'Joueurs',
      start: 'Démarrer',
      cancel: 'Annuler',
    },
    game: {
      date: 'Créée le',
      players: 'Joueurs',
      mode: 'Mode',
      standard: 'Partie standard',
      quickplay: 'Partie rapide',
      status: 'Progression',
      results: 'Résultat',
      score: 'Score',
      winner: 'Espèce dominante',
      looser: 'Perdu',
    }
  },
  game: {
    pass: 'Passer',
    score: {
      player: 'Joueur',
      title: 'Fin de partie',
      food: 'Nourriture',
      population: 'Population',
      traits: 'Traits',
      close: 'Fermer'
    },
    step: {
      SELECT_FOOD: 'Sélectioner nourriture',
      PLAY_CARDS: 'Jouer des cartes',
      FEEDING: 'Nourrire',
      ENDED: 'Terminée',
      'new': 'Nouvelle phase :',
      yourturn: 'À vous de jouer !'
    },
    trait: {
      '?': 'Masqué pour le moment',
      CARNIVOROUS: 'Carnivore',
      BURROWING: 'Fouisseur',
      DEFENSIVE_HERDING: 'Horde défensive',
      HORNS: 'Cornes',
      COOPERATION: 'Coopération',
      SCAVENGER: 'Charognard',
      FERTILE: 'Fertile',
      FORAGING: 'Fourrageur',
      LONGNECK: 'Long cou',
      WARNING_CALL: 'Cri d\'alarme',
      AMBUSH: 'Ambuscade',
      CLIMBING: 'Grimpant',
      HARD_SHELL: 'Carapace',
      SYMBIOSIS: 'Symbiose',
      PACK_HUNTING: 'Chasse en meute',
      FAT_TISSUE: 'Tissus adipeux',
      INTELLIGENT: 'Intelligence',
    },
    action: {
      'add-new-specie': 'Ajouter une espèce',
      'increase-size': 'Augmenter la taille',
      'increase-population': 'Augmenter la population',
      'add-trait': 'Ajouter un trait',
      'replace-trait': 'Remplacer un trait',
      'feed': 'Nourrire',
      'attack': 'Attaquer',
      'intelligent-feed': 'Nourrire (Intelligence)',
      'select-food': 'Sélection de la nourriture'
    },
    cards: '{count} carte | {count} cartes',
    position: {
      RIGHT: 'droite',
      LEFT: 'gauche'
    },
    'pool-added': 'aucune nourriture n\'est rajoutée au bassin | 1 nourriture est rajoutée au bassin | {count} nourritures sont rajoutées au bassin',
    'pool-removed': '1 nourriture est retirée du bassin | {count} nourritures sont retirées du bassin',
    eat: {
      meat: '1 morceau de viande | {count} morceaux de viandes',
      plant: '1 plante | {count} plantes',
    },
    events: {
      'player-card-dealed': '{player} pioche {cards}',
      'player-card-added-to-pool': '{player} place une nourriture dans le bassin',
      'specie-added': '{player} rajoute une espèce à {position}',
      'specie-trait-added': '{player} rajoute un nouveau trait {trait} à {specieName}',
      'pool-revealed': '{pool-revealed}',
      'specie-food-eaten': '{specieName} mange {foodConsumption}',
      'specie-attacked': '{attackerName} attaque {specieName}',
      'specie-extincted': '{specieName} s\'éteint',
      'new-turn': 'Un nouveau tour commence. {player} devient premier joueur',
      'player-passed' : '{player} passe'
    }
  }
};
