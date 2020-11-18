function findSpecieAction(actions, rootGetters, specie = true) {
  const onlySpecieId = rootGetters['selection/specieId'];
  if (onlySpecieId) {
    return actions.find(p => onlySpecieId === (specie ? p.specie : p.target));
  }
  return null;
}

var possibles = [{
    type: 'feed',
    specie: '1p0',
    valid: true,
    effects: [{
      specie: '1p0',
      traits: ['FORAGING'],
      deltaFood: 2,
    }]
  },
  {
    type: 'intelligent-feed',
    specie: '1p0',
    valid: true,
    card: 'c1',
    effects: [{
      specie: '1p0',
      traits: ['INTELLIGENT', 'FORAGING'],
      deltaFood: 3,
    }]
  },
  {
    type: 'intelligent-feed',
    specie: '1p0',
    valid: true,
    card: 'c2',
    effects: [{
      specie: '1p0',
      traits: ['INTELLIGENT', 'FORAGING'],
      deltaFood: 3,
    }]
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '1p1',
    valid: true,
    effects: [{
        specie: '1p1',
        traits: ['HORNED'],
        population: 1
      },
      {
        specie: '0p0',
        traits: ['CARNIVOROUS', 'FAT_TISSUE', 'AMBUSH'],
        population: 3,
        deltaFood: 1,
        deltaFat: 1
      },
      {
        specie: '2p1',
        traits: ['SCAVENGER', 'COLLABORATIVE'],
        deltaFood: 1
      },
      {
        specie: '3p1',
        deltaFood: 1
      }
    ],
    violations: [{
      type: 'trait',
      trait: '2p1-WARNING_CALL',
      disabled: '0p0-AMBUSH'
    }]
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '6p4',
    valid: true,
    effects: [{
        specie: '0p0',
        traits: ['CARNIVOROUS'],
        deltaFood: 1,
      },
      {
        specie: '6p4',
        population: 0
      }
    ],
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '1p0',
    valid: false,
    violations: [{
      type: 'size'
    }]
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '2p1',
    valid: false,
    violations: [{
      type: 'size'
    }]
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '3p1',
    valid: false,
    violations: [{
      type: 'trait',
      trait: '3p1-HARD_SHELL',
    }, {
      type: 'trait',
      trait: '2p1-WARNING_CALL',
      disabled: '0p0-AMBUSH'
    }]
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '4p2',
    valid: false,
    violations: [{
      type: 'trait',
      trait: '4p2-CLIMBING',
    }]
  },
  {
    type: 'attack',
    specie: '0p0',
    target: '5p3',
    valid: false,
    violations: [{
      type: 'trait',
      trait: '5p3-CLIMBING',
    }]
  }
];

possibles = null;

const getDefaultState = () => ({
  possibles: possibles,
  starteds: null,
  playing: null,
  //type: 'PLAY_CARDS'
  type: 'FEEDING'
  //type: 'SELECT_FOOD'
});

const state = getDefaultState();

export default {
  namespaced: true,
  state,
  mutations: {
    setStarteds: (state, starteds) => {
      state.starteds = starteds;
    },
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
    loadComplete: (state, evt) => {
      state.possibles = evt.user.actions;

      const game = evt.game;
      const player = game.players[evt.user.myself];
      state.type = player.status;
    },
    resetAction: (state) => {
      state.type = null;
      state.possibles = null;
    }
  },
  actions: {
    mouseup({
      commit,
      dispatch,
      getters
    }) {
      const act = getters.activable;
      const performs = (act && act.valid) ? JSON.parse(JSON.stringify(act)) : null;
      commit('setStarteds', null);
      if (performs) {
        return dispatch('io/emit', performs, {
          root: true
        });
      } else {
        return Promise.resolve(null);
      }
    },
    mousedown({
      //dispatch,
      state,
      commit,
      getters,
      rootGetters,
      rootState
    }) {
      if (!state.starteds) {
        const startOnSpecie = getters.startOnSpecie;
        const startOnCard = getters.startOnCard;


        if (startOnSpecie) {
          const starteds = state.possibles.filter(p => startOnSpecie === p.specie);
          commit('setStarteds', starteds);
        } else if (startOnCard) {
          if ('SELECT_FOOD' === state.type) {
            const starteds = [{
              type: 'select-food',
              card: startOnCard.id,
              valid: true
            }];
            commit('setStarteds', starteds);


          } else if ('PLAY_CARDS' === state.type) {

            const myself = rootState.gamestate.myself;
            const species = rootGetters['gamestate/players'][myself].species;
            const starteds = [{
                type: 'add-new-specie',
                card: startOnCard.id,
                position: 'left',
                valid: true
              },
              {
                type: 'add-new-specie',
                card: startOnCard.id,
                position: 'right',
                valid: true
              }
            ];

            species.filter(s => s.size < 6)
              .forEach(s => {
                starteds.push({
                  type: 'increase-size',
                  card: startOnCard.id,
                  target: `SIZE-${s.id}`,
                  size: s.size + 1,
                  targetSpecie: s.id,
                  valid: true
                });
              });

            species.filter(s => s.population < 6)
              .forEach(s => {
                starteds.push({
                  type: 'increase-population',
                  card: startOnCard.id,
                  target: `POPULATION-${s.id}`,
                  population: s.population + 1,
                  targetSpecie: s.id,
                  valid: true
                });
              });

            species.forEach(s => {
              if (!s.traits.some(t => t.trait === startOnCard.trait)) {
                const len = s.traits.length;
                for (var i = 0; i < len; ++i) {
                  starteds.push({
                    type: 'replace-trait',
                    card: startOnCard.id,
                    position: i,
                    trait: startOnCard.trait,
                    target: `${s.id}-${s.traits[i].trait}`,
                    specie: s.id,
                    targetSpecie: s.id,
                    valid: true
                  });
                }

                if (len < 3) {
                  starteds.push({
                    type: 'add-trait',
                    card: startOnCard.id,
                    trait: startOnCard.trait,
                    position: len,
                    target: s.id,
                    targetSpecie: s.id,
                    valid: true
                  });
                }
              }
            });

            commit('setStarteds', starteds);
          }
        }
      }
    },
  },
  getters: {
    playcard: (state) => {
      return 'PLAY_CARDS' === state.type;
    },

    startable: (state, getters) => {
      return !!getters.startOnSpecie;
    },
    startOnSpecie: (state, getters, rootState, rootGetters) => {
      if ('FEEDING' === state.type && !state.starteds && state.possibles) {
        const found = findSpecieAction(state.possibles, rootGetters, true);
        if (found)
          return found.specie;

      }
      return null;
    },
    startOnCard: (state, getters, rootState) => {
      if (('SELECT_FOOD' === state.type || 'PLAY_CARDS' === state.type) && !state.starteds) {
        return rootState.selection.card;
      }
      return null;
    },

    currents: (state, getters) => {
      if (getters.activable)
        return [getters.activable];

      return state.playing;
    },

    activable: (state, getters, rootState, rootGetters) => {
      if (state.starteds) {
        if ('FEEDING' === state.type) {
          if (getters.startedsType.includes('attack'))
            return findSpecieAction(state.starteds, rootGetters, false);

          if (getters.startedsType.includes('feed')) {
            if (rootGetters['selection/pool']) {
              return state.starteds.find(s => s.type === 'feed');
            }
          }

          if (getters.startedsType.includes('intelligent-feed')) {
            const cardId = rootGetters['selection/cardId'];
            if (cardId) {
              return state.starteds.find(s => s.type === 'intelligent-feed' && s.card === cardId);
            }

          }
        } else if ('PLAY_CARDS' === state.type) {
          const onStat = rootState.selection.stat;
          if (onStat) {
            const specieId = rootGetters['selection/specieId'];
            const target = `${onStat.name}-${specieId}`;
            return state.starteds.find(s => s.target === target);
          }

          const onlySpecieId = rootGetters['selection/onlySpecieId'];
          if (onlySpecieId)
            return state.starteds.find(p => onlySpecieId === p.target);


          const traitId = rootGetters['selection/traitId'];
          if (traitId)
            return state.starteds.find(p => traitId === p.target);

          const newSpecie = rootGetters['selection/newSpecie'];
          if (newSpecie) {
            return state.starteds.find(s => s.type === 'add-new-specie' && s.position === newSpecie);
          }

        } else if ('SELECT_FOOD' === state.type) {
          if (rootGetters['selection/pool']) {
            return state.starteds.find(s => s.type === 'select-food');
          }
        }
      }

      return null;
    },

    startedsType: (state) => {
      const starteds = state.starteds;
      if (starteds) {
        const actions = [];

        for (var st in starteds) {
          const type = starteds[st].type;
          if (!actions.includes(type)) {
            actions.push(type);
          }
        }
        return actions;
      }
      return null;
    }
  }
}
