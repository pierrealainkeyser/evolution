import stomp from '@/services/stomp';
import axios from '@/services/axios';
import router from '@/plugins/router';

const getDefaultState = () => ({
  label: null,
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
      state.name = v.name;
      state.label = v.label;
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
    myself: state => {
      return {
        label: state.label,
        name: state.name
      };
    },
    get: state => {
      return state.label;
    },
    uid: state => {
      return state.name;
    }
  }
}
