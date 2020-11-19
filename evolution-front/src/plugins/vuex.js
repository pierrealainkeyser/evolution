import Vue from 'vue';
import Vuex from 'vuex';

import user from '@/stores/user';
import users from '@/stores/users';
import selection from '@/stores/selection';
import action from '@/stores/action';
import gamestate from '@/stores/gamestate';
import io from '@/stores/io';
import ws from '@/stores/ws';


Vue.use(Vuex);

export default new Vuex.Store({
  modules: {
    user,
    users,
    selection,
    action,
    gamestate,
    io,
    ws
  }
});
