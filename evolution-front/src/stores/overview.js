import stomp from '@/services/stomp';

function userPredicate(user) {
  return u => u.name === user.name;
}

function gamePredicate(game) {
  return g => g.game === game.game;
}

export default {
  namespaced: true,
  state: {
    users: [],
    games: [],
    subs: [],
  },
  mutations: {
    addUser: (state, user) => {
      if (!state.users.find(userPredicate(user))) {
        state.users.push(user);
      }
    },
    removeUser: (state, user) => {
      const index = state.users.findIndex(userPredicate(user));
      if (index >= 0)
        state.users.splice(index, 1);
    },
    setUsers: (state, users) => {
      state.users.splice(0, state.users.length);
      Array.prototype.push.apply(state.users, users);
    },
    updateGame: (state, game) => {
      const index = state.games.findIndex(gamePredicate(game));
      if (index >= 0)
        state.games.splice(index, 1, game);
    },
    addGame: (state, game) => {
      if (!state.games.find(gamePredicate(game))) {
        state.games.push(game);
      }
    },
    removeGame: (state, game) => {
      const index = state.games.findIndex(gamePredicate(game));
      if (index >= 0)
        state.games.splice(index, 1);
    },
    setGames: (state, games) => {
      state.games.splice(0, state.games.length);
      Array.prototype.push.apply(state.games, games);
    },
    subs: (state, subs) => {
      state.subs = subs;
    }
  },

  actions: {
    userEvent({
      commit
    }, event) {
      if ('connect' === event.type) {
        commit('addUser', event.user);
      } else if ('disconnect' === event.type) {
        commit('removeUser', event.user);
      } else if ('all' === event.type) {
        commit('setUsers', event.users);
      }
    },

    gameEvent({
      commit
    }, event) {
      if ('terminated' === event.type) {
        commit('updateGame', event.game);
      } else if ('started' === event.type) {
        commit('addGame', event.game);
      }
    },

    bind({
      dispatch,
      commit
    }) {
      const userEvent = (e) => dispatch('userEvent', e);
      const gameEvent = (e) => dispatch('gameEvent', e);

      const subs = [];
      ['/topic/users', '/app/users'].forEach((item) => subs.push(stomp.subscribe(item, userEvent)));
      subs.push(stomp.subscribe('/user/my-games', gameEvent));
      subs.push(stomp.subscribe('/app/my-games', (e) => commit('setGames', e)));

      commit('subs', subs);
    }
  },

  getters: {
    games: state => {
      return state.games;
    },
    users: (state, getters, rootState, rootGetters) => {
      const myId = rootGetters['user/uid'];
      return state.users.filter(u => u.name !== myId);
    }
  }
}
