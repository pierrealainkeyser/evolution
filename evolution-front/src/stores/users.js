import stomp from '@/services/stomp';

export default {
  namespaced: true,
  state: {
    users: [],
    subs: [],
  },
  mutations: {
    add: (state, user) => {
      if (!state.users.find(u => u.id === user.id)) {
        state.users.push(user);
      }
    },
    remove: (state, user) => {
      const index = state.users.findIndex(u => u.id === user.id);
      if (index >= 0)
        state.users.splice(index, 1);
    },
    set: (state, users) => {
      state.users.splice(0, state.users.length);
      Array.prototype.push.apply(state.users, users);
    },
    subs: (state, subs) => {
      state.subs = subs;
    }
  },

  actions: {
    event({
      commit
    }, event) {
      if ('connect' === event.type) {
        commit('add', event.user);
      } else if ('disconnect' === event.type) {
        commit('remove', event.user);
      } else if ('all' === event.type) {
        commit('set', event.users);
      }
    },

    bind({
      dispatch,
      commit
    }) {
      const receive = (e) => dispatch('event', e);
      commit('subs', ['/topic/users', '/app/users'].map((item) => stomp.subscribe(item, receive)));
    }
  },

  getters: {
    all: state => {
      return state.users;
    }
  }
}
