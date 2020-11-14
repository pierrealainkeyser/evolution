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
    effects: [{
      specie: '1p0',
      traits: ['INTELIGENT', 'FORAGING'],
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
  },
  mutations: {
    setStarteds: (state, starteds) => {
      state.starteds = starteds;
    }
  },
  actions: {
    mouseup: ({
      commit
    }) => {
      commit('setStarteds', null);
    },
    mousedown: ({
      //dispatch,
      state,
      commit,
      getters,
      //rootGetters
    }) => {
      if (!state.starteds) {
        const startOnSpecie = getters.startOnSpecie;
        if (startOnSpecie) {
          const starteds = state.possibles.filter(p => startOnSpecie === p.specie);
          commit('setStarteds', starteds);
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
    current: (state, getters) => {
      if (getters.activable)
        return getters.activable;

      return state.playing;
    },
    activable: (state, getters, rootState, rootGetters) => {
      if ('FEEDING' === state.type && state.starteds) {
        if (getters.startedsType.includes('attack'))
          return findSpecieAction(state.starteds, rootGetters, false);

        if (getters.startedsType.includes('feed')) {
          if (rootGetters['selection/pool']) {
            return state.starteds.find(s => s.type === 'feed');
          }
        }
      }
      return null;
    },
    specieSource: (state) => {
      const starteds = state.starteds;
      if (starteds && starteds.length) {
        return starteds[0].specie;
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
