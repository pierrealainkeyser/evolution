var species = [{
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

var players = [{
    user: {
      name: 'pak',
      id:'pak@form'
    },
    status: 'active',
    hands: 2,
  },
  {
    user: {
      name: 'Player 2',
    },
    status: 'idle',
    hands: 3,

  },
  {
    user: {
      name: 'Player 3',
    },
    status: 'idle',
    hands: 4,
  },
  {
    user: {
      name: 'Player 4',
    },
    status: 'idle',
    hands: 1,
  },
  {
    user: {
      name: 'Player 5',
    },
    status: 'idle',
    hands: 0,
  }
];

var hands = [{
    id: "c1",
    trait: "CARNIVOROUS",
    food: 2
  },
  {
    id: "c2",
    trait: "INTELLIGENT",
    food: 4
  }
];

var food = 5;

// species = [];
// players = [];
// hands = [];
food = 0;



const getDefaultState = () => ({
  species: species,
  players: players,
  hands: hands,
  step: 'FEEDING',
  lastTurn: false,
  pool: {
    food: food,
    cards: 0,
  },
  myself: 0
});

const state = getDefaultState();

export default {
  namespaced: true,
  state,
  mutations: {
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
    resetAction: (state) => {
      state.players[state.myself].status = 'idle';
    },
    loadComplete: (state, evt) => {
      const game = evt.game;
      state.step = game.step;
      state.lastTurn = game.lastTurn;
      state.pool.food = game.foodPool.food;
      state.pool.cards = game.foodPool.waiting;
      state.species = game.players.flatMap(p => (p.species));
      state.players = game.players.map(p => ({
        user: p.player,
        status: p.status.toLowerCase(),
        hands: p.inHands,
      }));

      const user = evt.user;
      state.myself = user.myself;
      state.hands = user.hand;
    },
  },
  getters: {
    food: (state) => {
      return state.pool.food;
    },
    cards: (state) => {
      return state.pool.cards;
    },
    players: (state, getters, rootState, rootGetters) => {
      const byPlayers = [];
      for (var i in state.species) {
        const specie = state.species[i];
        const player = specie.id.match(/\d+p(\d+)/)[1];
        const to = byPlayers[player] = byPlayers[player] || [];
        to.push(specie);
      }

      const connectedUsers = rootGetters['overview/users'];

      return state.players.map((p, index) => ({
        connected: connectedUsers.some((cu) => cu.name === p.user.name),
        name: p.user.label,
        status: p.status,
        hands: p.hands,
        index,
        species: byPlayers[index]
      }));
    }
  }
}
