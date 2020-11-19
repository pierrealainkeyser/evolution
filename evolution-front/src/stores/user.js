import stomp from '@/services/stomp';
import axios from '@/services/axios';
import router from '@/plugins/router';

const getDefaultState = () => ({
  id: null,
  name: null,
  to: '/'
});

const state = getDefaultState();

export default {
  namespaced: true,
  state,
  mutations: {
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
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
    logged: state => {
      return !!state.name;
    },
    get: state => {
      return state.name;
    },
    uid: state => {
      return state.id;
    }
  }
}
