import synchro from '@/services/synchro';

const ROOT = {
  root: true
};

const getDefaultState = () => ({
  animation: null
});

const state = getDefaultState();
export default {
  namespaced: true,
  state,
  mutations: {
    animation: (state, animation) => {
      state.animation = animation;
    },
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
  },
  getters: {
    current: (state) => {
      return state.animation
    }
  },
  actions: {
    attacked({
      commit
    }, events) {
      commit('action/loadPlaying', events, ROOT);
      const attacked = events[0];
      commit('animation', {
        type: 'attack',
        target: attacked.specie,
        attacker: attacked.attacker
      });

      return synchro.wait();
    },

    feed({
      commit
    }, events) {
      commit('action/loadPlaying', events, ROOT);
      const feed = events[0];
      commit('animation', {
        type: 'feed',
        specie: feed.specie
      });

      return synchro.wait();
    },

    newStep({
      commit
    }, step) {
      commit('animation', {
        type: 'newStep',
        step: step.step
      });
      return synchro.wait();
    },

    yourTurn({
      commit
    }) {
      commit('animation', {
        type: 'yourTurn'
      });
      return synchro.wait();
    },

    done({
      commit
    }) {
      commit('action/loadPlaying', null, ROOT);
      commit('animation', null);
      synchro.resolve();
    }
  }
};
