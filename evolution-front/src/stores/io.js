import stomp from '@/services/stomp';
import Vue from 'vue';

const getDefaultState = () => ({
  gameId: null,
  subs: [],
  loaded: false,
  connecting: false,
  sendingData: false,
  draw: -1,
});

const state = getDefaultState();

const ROOT = {
  root: true
};

export default {
  namespaced: true,
  state,
  mutations: {
    setGame: (state, {
      gameId,
      subs
    }) => {
      state.gameId = gameId;
      state.connecting = true;
      state.subs = subs;
    },
    draw: (state, draw) => {
      state.draw = draw;
      state.connecting = false;
      state.sendingData = false;
      state.loaded = draw >= 0;
    },
    sendingData: (state, sendingData) => {
      state.sendingData = sendingData;
    },
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
  },
  actions: {
    disconnect({
      commit,
      state
    }) {
      state.subs.forEach(s => s.unsubscribe());

      commit('setGame', {
        gameId: null,
        subs: []
      });
    },

    reset({
      commit
    }) {
      commit('gamestate/resetState', null, ROOT);
      commit('action/resetState', null, ROOT);
      commit('selection/resetState', null, ROOT);
    },

    connect({
      commit,
      dispatch
    }, gameId) {

      const receive = (data) => dispatch('receive', data);
      const subs = [`/app/game/${gameId}`, `/user/game/${gameId}`]
        .map((topic) => stomp.subscribe(topic, receive));

      commit('setGame', {
        gameId,
        subs
      });
    },

    receive({
      commit,
      state
    }, evt) {
      const draw = evt.draw;
      if ('complete' === evt.type) {
        commit('draw', draw);
        commit('gamestate/loadComplete', evt, ROOT);
        commit('action/loadComplete', evt, ROOT);

        Vue.nextTick(() => {
          commit('selection/setRotation', evt.game.players.length, ROOT);
        });

      } else if ('partial' === evt.type) {
        if (state.draw < draw) {
          commit('draw', draw);
        }
      }
    },

    emit({
      commit,
      state
    }, action) {

      var name = null;
      const command = {};
      if ('select-food' === action.type) {
        name = action.type;
        command.card = action.card;
      } else if ('add-new-specie' === action.type) {
        name = 'add-specie';
        command.card = action.card;
        command.position = action.position.toUpperCase();
      } else if (['replace-trait', 'add-trait'].includes(action.type)) {
        name = 'add-trait';
        command.card = action.card;
        command.specie = action.targetSpecie;
        command.position = action.position;
      } else if (['increase-population', 'increase-size'].includes(action.type)) {
        name = action.type;
        command.card = action.card;
        command.specie = action.targetSpecie;
      } else if ('feed' === action.type) {
        name = action.type;
        command.specie = action.specie;
      } else if ('feed' === action.type) {
        name = action.type;
        command.specie = action.specie;
      } else if ('intelligent-feed' === action.type) {
        name = action.type;
        command.specie = action.specie;
        command.discarded = action.card;
      } else if ('attack' === action.type) {
        name = action.type;
        command.specie = action.specie;
        command.target = action.target
        command.violations = {};
      }

      if (name) {
        const to = `/app/game/${state.gameId}/${name}`;

        commit('action/resetAction', null, ROOT);
        commit('gamestate/resetAction', null, ROOT);

        commit('sendingData', true);

        if (stomp.status) {
          stomp.publish(to, command);
        } else {
          console.log("send", to, command);
        }
      }
    }
  }
};
