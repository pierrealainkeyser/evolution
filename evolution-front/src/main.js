import 'roboto-fontface/css/roboto/roboto-fontface.css'
import '@mdi/font/css/materialdesignicons.css'

import Vue from 'vue'
import App from './App.vue'
import vuetify from '@/plugins/vuetify';
import store from '@/plugins/vuex'
import router from '@/plugins/router'


Vue.config.productionTip = false;

var check=false;

// add router guard with store
router.beforeEach((to, from, next) => {


  if (check && to.path !== '/login' && !store.getters['user/connected']) {
    store.commit('user/afterlogin', to);
    next('/login');
  } else {
    next();
  }
})

new Vue({
  vuetify,
  router,
  store,
  render: h => h(App)
}).$mount('#app')
