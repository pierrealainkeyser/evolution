import Vue from 'vue';
import Vuex from 'vuex';

import user from '@/stores/user';
import overview from '@/stores/overview';
import selection from '@/stores/selection';
import action from '@/stores/action';
import gamestate from '@/stores/gamestate';
import io from '@/stores/io';
import ws from '@/stores/ws';


Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    user,
    overview,
    selection,
    action,
    gamestate,
    io,
    ws
  }
});
