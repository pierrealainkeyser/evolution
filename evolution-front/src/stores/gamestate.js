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

function idPredicate(id) {
  return t => t.id === id;
}

function removeCardFromHand(state, card) {
  const index = state.hands.findIndex(idPredicate(card.id));
  if (index > -1)
    state.hands.splice(index, 1);
}

export default {
  namespaced: true,
  state,
  mutations: {
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
    loadComplete: (state, evt) => {
      const game = evt.game;
      state.step = game.step;
      state.lastTurn = game.lastTurn;
      state.pool.food = game.foodPool.food;
      state.pool.cards = game.foodPool.waiting;
      state.species = game.players.flatMap(p => p.species);
      state.players = game.players.map(p => ({
        user: p.player,
        status: p.status.toLowerCase(),
        hands: p.inHands,
      }));

      const user = evt.user;
      state.myself = user.myself;
      state.hands = user.hand;
    },
    'new-step': (state, evt) => {
      state.step = evt.step;
    },
    'player-state-changed': (state, evt) => {
      state.players[evt.player].status = evt.state.toLowerCase();
    },
    'player-card-added-to-pool': (state, evt) => {
      --state.players[evt.player].hands;
      if (evt.card)
        removeCardFromHand(state, evt.card);

      ++state.pool.cards;
    },
    'specie-trait-added': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      const index = evt.index;

      --state.players[evt.player].hands;
      if (evt.card)
        removeCardFromHand(state, evt.card);

      const card = evt.card || {
        trait: '?'
      };
      if (index > target.traits.length)
        target.traits.push(card);
      else
        target.traits.splice(index, 1, card);
    },
    'traits-revealed': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      Object.keys(evt.traits).forEach((prop) => {
        const index = parseInt(prop);
        const card = evt.traits[prop];
        target.traits.splice(index, 1, card);
      });
    },
    'specie-added': (state, evt) => {
      const id = evt.specie;

      const discarded = evt.discarded;
      if (discarded) {
        --state.players[evt.player].hands;
        removeCardFromHand(state, discarded);
      }

      const specie = {
        id,
        fat: 0,
        food: 0,
        population: 1,
        size: 1,
        traits: []
      };

      if ('LEFT' === evt.position) {
        state.species.splice(0, 0, specie);
      } else {
        state.species.push(specie);
      }
    },

    'specie-population-increased': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.population = evt.population;

      --state.players[evt.player].hands;
      removeCardFromHand(state, evt.discarded);
    },
    'specie-size-increased': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.size = evt.size;

      --state.players[evt.player].hands;
      removeCardFromHand(state, evt.discarded);
    },

    'specie-food-eaten': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.fat += evt.fat;
      target.food += evt.food;

      const delta = evt.delta;
      if (delta) {
        state.pool.food += evt.delta;
      }

      const discarded = evt.discarded;
      if (discarded) {
        --state.players[evt.player].hands;
        removeCardFromHand(state, discarded);
      }
    },

    'specie-extincted': (state, evt) => {
      const index = state.species.findIndex(idPredicate(evt.specie));
      if (index > -1)
        state.species.splice(index, 1);
    },

    'pool-revealed': (sate, evt) => {
      state.pool.food += evt.delta;
      state.pool.cards = 0;
    }
  },
  getters: {
    food: (state) => {
      return state.pool.food;
    },
    cards: (state) => {
      return state.pool.cards;
    },
    myStatus: (state) => {
      const mySelf = state.myself;
      if (state.players.length > mySelf && mySelf >= 0) {
        const status = state.players[mySelf].status;
        if (status)
          return status.toUpperCase();
      }
      return null;
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
