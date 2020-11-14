import stomp from '@/services/stomp';
import axios from '@/services/axios';
import router from '@/plugins/router';

export default {
  namespaced: true,
  state: {
    id: null,
    name: null,
    to: null
  },
  mutations: {
    set: (state, user) => {
      const v = user || {}
      state.id = v.id;
      state.name = v.name;
    },
    afterlogin: (state, to) => {
      state.to = to;
    },
  },
  actions: {
    login: ({
      commit,
      state
    }, user) => {
      stomp.connect(user.xcsrf);
      axios.defaults.headers["X-CSRF-TOKEN"] = user.xcsrf;
      commit('set', user);
      router.push(state.to);
    },
    logout: ({
      commit
    }) => {
      stomp.deactivate();
      commit('set', null);
    }
  },
  getters: {
    connected: state => {
      return !!state.label;
    },
    get: state => {
      return state.name;
    },
    uid: state => {
      return state.id;
    }
  }
}
