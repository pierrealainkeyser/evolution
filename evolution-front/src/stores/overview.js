import stomp from '@/services/stomp';

export default {
  namespaced: true,
  state: {
    users: [],
    games: [],
    subs: [],
  },
  mutations: {
    addUser: (state, user) => {
      if (!state.users.find(u => u.name === user.name)) {
        state.users.push(user);
      }
    },
    removeUser: (state, user) => {
      const index = state.users.findIndex(u => u.name === user.name);
      if (index >= 0)
        state.users.splice(index, 1);
    },
    setUsers: (state, users) => {
      state.users.splice(0, state.users.length);
      Array.prototype.push.apply(state.users, users);
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
