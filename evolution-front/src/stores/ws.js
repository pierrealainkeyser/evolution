import stomp from '@/services/stomp';

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
      dispatch
    }) {
      stomp.addListener((st) => {
        commit('setConnected', st.status);
      });

      dispatch('users/bind', null, ROOT);
    },
  }
}
