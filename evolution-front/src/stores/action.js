function findSpecieAction(actions, rootGetters, specie = true) {
  const onlySpecieId = rootGetters['selection/specieId'];
  if (onlySpecieId) {
    return actions.find(p => onlySpecieId === (specie ? p.specie : p.target));
  }
  return null;
}

const possibles = [{
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

export default {
  namespaced: true,
  state: {
    possibles: possibles,
    starteds: null,
    playing: null,
    type: 'FEEDING',
    //type: 'PLAY_CARD',
    //type: 'SELECT_FOOD'
  },
  mutations: {
    setStarteds: (state, starteds) => {
      state.starteds = starteds;
    }
  },
  actions: {
    mouseup: ({
      commit,
      getters
    }) => {

      const act = getters.activable;
      if (act && act.valid)
        console.log('activate', JSON.parse(JSON.stringify(act)));

      commit('setStarteds', null);
    },
    mousedown: ({
      //dispatch,
      state,
      commit,
      getters,
      rootGetters,
      rootState
    }) => {
      if (!state.starteds) {
        const startOnSpecie = getters.startOnSpecie;
        const startOnCard = getters.startOnCard;
        if (startOnSpecie) {
          const starteds = state.possibles.filter(p => startOnSpecie === p.specie);
          commit('setStarteds', starteds);
        } else if (startOnCard) {
          if ('SELECT_FOOD' === state.type) {
            // TODO
          } else {
            // TODO
            const myself = rootState.gamestate.myself;
            const species = rootGetters['gamestate/players'][myself].species;
            const starteds = [];

            species.filter(s => s.size < 6)
              .forEach(s => {
                starteds.push({
                  type: 'increase-size',
                  card: startOnCard.id,
                  target: `SIZE-${s.id}`,
                  size: s.size + 1,
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
                    index: i,
                    trait: startOnCard.trait,
                    target: `${s.id}-${s.traits[i].trait}`,
                    specie: s.id,
                    valid: true
                  });
                }

                if (len < 3) {
                  starteds.push({
                    type: 'add-trait',
                    card: startOnCard.id,
                    trait: startOnCard.trait,
                    target: s.id,
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
      if (('SELECT_FOOD' === state.type || 'PLAY_CARD' === state.type) && !state.starteds && state.possibles) {
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
        } else if ('PLAY_CARD' === state.type) {
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
