
const getDefaultState = () => ({
  species: [],
  players: [],
  hands: [],
  step: null,
  lastTurn: false,
  pool: {
    food: 0,
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
