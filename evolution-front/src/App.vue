<template>
<v-app>
  <Bar />
  <v-main>
    <v-fade-transition mode="out-in">
      <router-view />
    </v-fade-transition>
  </v-main>
</v-app>
</template>

<script>
import Bar from '@/components/system/Bar';

import {
  mapActions
} from 'vuex';

export default {
  name: 'App',
  components: {
    Bar
  },
  methods: {
    ...mapActions({
      bindStore: 'ws/bind'
    })
  },

  created() {
    this.$vuetify.theme.dark = true;
    this.bindStore();
  }
};
</script>
