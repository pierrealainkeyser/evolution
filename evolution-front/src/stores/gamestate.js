const getDefaultState = () => ({
  species: [],
  players: [],
  hands: [],
  events: [],
  step: null,
  lastTurn: false,
  scoreBoards: null,
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

function middleTrait(trait) {
  if ('CARNIVOROUS' === trait)
    return 'Poterant';
  else if ('PACK_HUNTING' === trait)
    return 'Stiperant';
  else if ('CLIMBING' === trait)
    return 'Scandere';
  else if ('SCAVENGER' === trait)
    return 'Hyaenis';
  else if ('FAT_TISSUE' === trait)
    return 'Crassis';
  else if ('LONGNECK' === trait)
    return 'Longuae Colli';
  else if ('FORAGING' === trait)
    return 'Cavator';
  else if ('FERTILE' === trait)
    return 'Fertilum';
  else if ('SYMBIOSIS' === trait)
    return 'Symbiosum';
  else if ('COOPERATION' === trait)
    return 'Cooperantem';
    else if ('BURROWING' === trait)
      return 'Fodiensem';

  return 'Species';
}

function firstTrait(trait) {
  if ('CARNIVOROUS' === trait)
    return 'Poteris';
  else if ('PACK_HUNTING' === trait)
    return 'Stipus';
  else if ('CLIMBING' === trait)
    return 'Scandera';
  else if ('SCAVENGER' === trait)
    return 'Cadaveribus';
  else if ('FAT_TISSUE' === trait)
    return 'Crassus';
  else if ('LONGNECK' === trait)
    return 'Longus Colli';
  else if ('FORAGING' === trait)
    return 'Avarus';
  else if ('FERTILE' === trait)
    return 'Fertilius';
  else if ('SYMBIOSIS' === trait)
    return 'Symbiosis';
  else if ('COOPERATION' === trait)
    return 'Cooperante';
    else if ('BURROWING' === trait)
      return 'Fodiens';

  return 'Vulgaris';
}

function specieName(traits) {

  var names = [firstTrait(null), middleTrait(null)];
  if (traits) {
    if (traits.length === 1) {
      names = [firstTrait(traits[0]), middleTrait(null)];
    } else if (traits.length == 2) {
      names = [firstTrait(traits[0]), middleTrait(traits[1])];
    }
  }

  return names.filter(n => !!n).join(' ');
}

export default {
  namespaced: true,
  state,
  mutations: {
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
    loadScoreBoards: (state, scoreBoards) => {
      state.scoreBoards = scoreBoards;
    },
    loadComplete: (state, evt) => {
      const game = evt.game;
      const speciesById = {};

      state.events = evt.events.map(evt => {

        const out = { ...evt
        };

        if (evt.specie) {
          out.specieName = specieName(speciesById[evt.specie]);
        }

        if (evt.attacker) {
          out.attackerName = specieName(speciesById[evt.attacker]);
        }

        if (evt.type === 'specie-added') {
          speciesById[evt.specie] = [];
        } else if (evt.type === 'specie-trait-added') {
          if (evt.card) {
            if (!speciesById[evt.specie])
              speciesById[evt.specie] = [];

            speciesById[evt.specie][evt.index] = evt.card.trait;
          }
        } else if (evt.type === 'traits-revealed') {
          var target = speciesById[evt.specie];
          if (!target)
            target = speciesById[evt.specie] = [];
          Object.keys(evt.traits).forEach((prop) => {
            const index = parseInt(prop);
            const card = evt.traits[prop].trait;
            target.splice(index, 1, card);
          });
        }

        return out;
      });

      state.scoreBoards = game.scoreBoards;
      state.step = game.step;
      state.lastTurn = game.lastTurn;
      state.pool.food = game.foodPool.food;
      state.pool.cards = game.foodPool.waiting;
      state.species = game.players.flatMap(p => p.species.map(s => {
        return {
          ...s,
          name: specieName(s.traits.map(t => t.trait))
        }
      }));

      state.players = game.players.map(p => ({
        user: p.player,
        status: p.status.toLowerCase(),
        hands: p.inHands,
      }));

      const user = evt.user;
      state.myself = user.myself;
      state.hands = user.hand;
    },
    addEvent: (state, evt) => {
      const out = { ...evt
      };
      if (evt.specie) {
        out.specieName = specieName((state.species.find(idPredicate(evt.specie)) || {
          traits: []
        }).traits.map(t => t.trait));
      }

      if (evt.attacker) {
        out.attackerName = specieName((state.species.find(idPredicate(evt.attackerName)) || {
          traits: []
        }).traits.map(t => t.trait));
      }

      state.events.push(out);
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

      target.name = specieName(target.traits);
    },
    'traits-revealed': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      Object.keys(evt.traits).forEach((prop) => {
        const index = parseInt(prop);
        const card = evt.traits[prop];
        target.traits.splice(index, 1, card);
        target.name = specieName(target.traits);
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
        traits: [],
        name: specieName([])
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

    'specie-population-reduced': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.population = evt.population;
    },

    'specie-population-growed': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.population = evt.population;
    },

    'specie-size-increased': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.size = evt.size;

      --state.players[evt.player].hands;
      removeCardFromHand(state, evt.discarded);
    },

    'specie-fat-moved': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.food += evt.fat;
      target.fat -= evt.fat;
    },

    'specie-food-scored': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.food = 0;
    },

    'specie-food-eaten': (state, evt) => {
      const target = state.species.find(idPredicate(evt.specie));
      target.fat += evt.fat;
      target.food += evt.food;

      if ('POOL' === evt.source) {
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
    },

    'player-card-dealed': (state, evt) => {
      state.players[evt.player].hands += evt.count;

      const cards = evt.cards;
      if (cards)
        cards.forEach(card => state.hands.push(card));
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
    },

    scoreBoards: (state, getters) => {
      if (state.scoreBoards) {
        const players = getters['players'];

        return state.scoreBoards.map(p => ({
          ...p,
          player: players[p.player].name
        }));
      }
      return null;
    },

    events: (state) => {
      return state.events;
    }
  }
}
