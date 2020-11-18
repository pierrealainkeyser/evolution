import Vue from 'vue';
import Vuex from 'vuex';

import user from '@/stores/user';
import selection from '@/stores/selection';
import action from '@/stores/action';
import gamestate from '@/stores/gamestate';
import io from '@/stores/io';


Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    user,
    selection,
    action,
    gamestate,
    io
  }
});
