import 'roboto-fontface/css/roboto/roboto-fontface.css';
import '@mdi/font/css/materialdesignicons.css';

import Vue from 'vue';
import App from './App.vue';
import vuetify from '@/plugins/vuetify';
import store from '@/plugins/vuex';
import router from '@/plugins/router';
import i18n from '@/plugins/i18n';

Vue.config.productionTip = false;

// add router guard with store
router.beforeEach((to, from, next) => {
  if (to.path !== '/login' && !store.getters['user/logged']) {
    store.commit('user/afterlogin', to);
    router.push('/login');
  } else {
    next();
  }
})

new Vue({
  vuetify,
  router,
  store,
  i18n,
  render: h => h(App)
}).$mount('#app')
