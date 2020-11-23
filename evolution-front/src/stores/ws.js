import stomp from '@/services/stomp';
import router from '@/plugins/router';

const getDefaultState = () => ({
  connected: false
});

const state = getDefaultState();

const ROOT = {
  root: true
};

export default {
  namespaced: true,
  state,
  mutations: {
    setConnected: (state, connected) => {
      state.connected = connected;
    }
  },
  actions: {
    bind({
      commit,
      dispatch,
      rootState
    }) {
      stomp.addListener((st) => {
        commit('setConnected', st.status);

        if (!st.status && rootState.io.connecting) {
          router.push('/');
          //TODO alerte
        }
      });

      dispatch('overview/bind', null, ROOT);
    },
  }
}
