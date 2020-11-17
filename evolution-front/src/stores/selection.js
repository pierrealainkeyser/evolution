export default {
  namespaced: true,
  state: {
    rotation: 0,
    specie: null,
    trait: null,
    stat: null,
    card: null,
    pool: false,
    mouse: {
      x: 0,
      y: 0
    }
  },
  mutations: {
    setPool: (state, pool) => {
      state.pool = pool;
    },
    setSpecie: (state, specie) => {
      state.specie = specie;
    },
    setTrait: (state, trait) => {
      state.trait = trait;
    },
    setStat: (state, stat) => {
      state.stat = stat;
    },
    setCard: (state, card) => {
      state.card = card;
    },
    setRotation: (state, rotation) => {
      state.rotation = rotation;
    },
    setMouse: (state, mouse) => {
      state.mouse.x = mouse.x;
      state.mouse.y = mouse.y;
    },
  },
  actions: {
    incRotation({
      commit,
      state
    }, delta) {
      commit('setRotation', state.rotation + delta);
    },
    mouseMove({
      commit
    }, evt) {
      commit('setMouse', {
        x: evt.clientX,
        y: evt.clientY
      });
    },
    enterCard: ({
      commit
    }, evt) => {
      commit('setCard', {
        id: evt.card,
        trait: evt.trait,
        food: evt.food,
      });
    },
    leaveCard: ({
      commit
    }) => {
      commit('setCard', null);
    },
    enterSpecie: ({
      commit
    }, evt) => {
      commit('setSpecie', {
        id: evt.specie,
        box: evt.box
      });
    },
    leaveSpecie: ({
      commit
    }) => {
      commit('setSpecie', null);
    },
    enterTrait: ({
      commit
    }, evt) => {
      commit('setTrait', {
        index: evt.index,
        name: evt.trait,
        box: evt.box
      });
    },
    leaveTrait: ({
      commit
    }) => {
      commit('setTrait', null);
    },

    enterStat: ({
      commit
    }, evt) => {
      commit('setStat', {
        name: evt.stat,
        box: evt.box
      });
    },
    leaveStat: ({
      commit
    }) => {
      commit('setStat', null);
    },

    enterPool: ({
      commit
    }) => {
      commit('setPool', true);
    },
    leavePool: ({
      commit
    }) => {
      commit('setPool', false);
    },
  },
  getters: {
    pool: state => {
      return state.pool;
    },

    onlySpecieId: state => {
      if (state.specie && (!state.trait) && (!state.stat)) {
        return state.specie.id;
      }
      return null;
    },

    specieId: state => {
      if (state.specie) {
        return state.specie.id;
      }
      return null;
    },

    traitId: state => {
      if (state.specie && state.trait) {
        return state.specie.id+'-'+state.trait.name;
      }
      return null;
    },

    cardId: state => {
      if (state.card) {
        return state.card.id;
      }
      return null;
    }
  }
}
