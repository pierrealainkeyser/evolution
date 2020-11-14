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

export default {
  name: 'App',
  components: {
    Bar
  },
  data: () => ({
    //
  }),
  created() {
    this.$vuetify.theme.dark = true
  }
};
</script>
