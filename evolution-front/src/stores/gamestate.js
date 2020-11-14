const species = [{
    id: "0p0",
    size: 3,
    population: 4,
    food: 2,
    fat: 1,
    traits: [{
        index: 0,
        trait: 'CARNIVOROUS'
      },
      {
        index: 1,
        trait: 'AMBUSH'
      },
      {
        index: 2,
        trait: 'FAT_TISSUE'
      }
    ]
  },
  {
    id: "1p0",
    size: 3,
    population: 4,
    food: 1,
    fat: 0,
    traits: [{
        index: 0,
        trait: 'FORAGING'
      },
      {
        index: 1,
        trait: 'INTELLIGENT'
      }
    ]
  },
  {
    id: "1p1",
    size: 2,
    population: 2,
    food: 1,
    traits: [{
        index: 0,
        trait: 'BURROWING'
      },
      {
        index: 1,
        trait: 'HORNED'
      }
    ]
  },
  {
    id: "2p1",
    size: 5,
    food: 3,
    population: 4,
    traits: [{
        index: 0,
        trait: 'WARNING_CALL'
      },
      {
        index: 1,
        trait: 'SCAVENGER'
      },
      {
        index: 2,
        trait: 'COLLABORATIVE'
      }
    ]
  },
  {
    id: "3p1",
    size: 1,
    food: 0,
    population: 1,
    traits: [{
      index: 0,
      trait: 'HARD_SHELL'
    }]
  },
  {
    id: "4p2",
    size: 1,
    food: 1,
    population: 1,
    traits: [{
      index: 0,
      trait: 'CLIMBING'
    }]
  },
  {
    id: "5p3",
    size: 1,
    food: 1,
    population: 1,
    traits: [{
      index: 0,
      trait: 'CLIMBING'
    }]
  },
  {
    id: "6p4",
    size: 1,
    food: 1,
    population: 1,
    traits: []
  }
];

const players = [{
    name: 'Player 1'
  },
  {
    name: 'Player 2'
  },
  {
    name: 'Player 3'
  },
  {
    name: 'Player 4'
  },
  {
    name: 'Player 5'
  }
];

const hands = [{

}];

export default {
  namespaced: true,
  state: {
    species: species,
    players: players,
    hands: hands,
    pool: {
      food: 5,
      cards: 0,
    },
    myself: 0
  },
  mutations: {

  },
  actions: {

  },
  getters: {
    food: (state) => {
      return state.pool.food;
    },
    cards: (state) => {
      return state.pool.cards;
    },
    players: (state) => {
      const byPlayers = [];
      for (var i in state.species) {
        const specie = state.species[i];
        const player = specie.id.match(/\d+p(\d+)/)[1];
        const to = byPlayers[player] = byPlayers[player] || [];
        to.push(specie);
      }

      return state.players.map((p, index) => ({ ...p,
        index,
        species: byPlayers[index]
      }));
    }
  }
}
