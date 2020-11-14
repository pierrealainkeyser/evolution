<template>
<v-container>
  <v-card>
    <v-card-title>Please identify yourself</v-card-title>
    <v-card-text>Choose an OpenID Connect provider bellow</v-card-text>
    <v-card-text>
      <v-progress-circular v-if="loading" indeterminate color="primary" />
    </v-card-text>
    <v-card-actions>
      <v-btn v-for="(l,i) in links" :key="i" :href="l.uri">
        {{l.name}}
      </v-btn>
    </v-card-actions>
  </v-card>
</v-container>
</template>

<script>
import { mapActions } from 'vuex';
import axios from '@/services/axios';

export default {
  data() {
    return {
      links: [],
      loading: false
    };
  },
  mounted() {
    this.loading = true;
    axios.get("auth/principal").then(r => {
      this.login({
        id: r.data.id,
        name: r.data.name,
        xcsrf: r.headers['x-csrf-token']
      });
    }).catch(e => {
      const response = e.response;
      if (response && 401 === response.status) {
        const data = response.data;
        const baseURL = axios.defaults.baseURL || "";
        Object.keys(data).forEach(key => {
          const value = data[key];
          this.links.push({
            name: key,
            uri: baseURL + value
          })
        });
      } else {
        console.log(e);
      }

    }).finally(() => {
      this.loading = false;
    });
  },
  methods: {
    ...mapActions({
      login: 'user/login'
    })
  }

}
</script>
