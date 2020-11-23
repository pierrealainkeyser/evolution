<template>
<v-container>
  <v-card>
    <v-card-title>
      <v-icon class="mr-2">mdi-shield-alert-outline</v-icon> {{$t('login.title')}}
    </v-card-title>

    <v-fade-transition mode="in-out">
      <v-card-text v-if="loading" class="d-flex justify-center">
        <v-progress-circular indeterminate color="accent" :size="100">
          <v-icon large>mdi-cloud-sync-outline</v-icon>
        </v-progress-circular>
      </v-card-text>

      <v-card-text v-else-if="error">
        <v-alert border="left" type="error" prominent :icon="false">
          <v-row align="center">
            <v-col class="grow">
              <h5>{{$t('login.error')}}</h5>
              <p>{{error}}</p>
            </v-col>
            <v-col class="shrink">
              <v-btn @click="tryAuth">{{$t('login.retry')}}</v-btn>
            </v-col>
          </v-row>
        </v-alert>
      </v-card-text>

      <div v-else-if="openid">
        <v-card-text>{{$t('login.chooseOpenId')}}</v-card-text>
        <v-card-actions>
          <v-btn v-for="(l,i) in providers" :key="i" :href="l.uri">
            {{l.name}}
          </v-btn>
        </v-card-actions>
      </div>

      <div v-else-if="form">
        <v-card-text>{{$t('login.form')}}</v-card-text>
        <v-card-text>
          <v-text-field outlined :label="$t('login.user')" v-model.trim="username" />

        </v-card-text>
        <v-card-actions>
          <v-btn :disabled="!username" @click="tryFormLogin">
            {{$t('login.formSubmit')}}
          </v-btn>
        </v-card-actions>
      </div>

    </v-fade-transition>
  </v-card>
</v-container>
</template>

<script>
import {
  mapActions
} from 'vuex';
import axios from '@/services/axios';

export default {
  name: 'LoginView',
  data() {
    return {
      providers: [],
      loading: false,
      loaded: false,
      form: false,
      username: null,
      formLoginPage: null,
      error: null,
    };
  },
  computed: {
    openid() {
      return this.providers.length > 0 && this.loaded;
    }
  },
  mounted() {
    this.tryAuth();
  },
  methods: {
    ...mapActions({
      login: 'user/login'
    }),

    errorHandler(e) {
      const response = e.response;
      if (response && 401 === response.status) {
        this.loaded = true;
        const data = response.data;
        if (data.form) {
          this.form = true;
          this.formLoginPage = data.formLoginPage;
        } else {
          const providers = data.providers;
          const baseURL = axios.defaults.baseURL || "";
          Object.keys(providers).forEach(key => {
            const value = providers[key];
            this.providers.push({
              name: key,
              uri: baseURL + value
            })
          });
        }
      } else {
        this.error = e.message;
      }
    },
    finallyHandler() {
      this.loading = false;
    },
    reset() {
      this.loading = true;
      this.loaded = false;
      this.error = null;
      this.form = false;
      this.providers = [];
    },

    tryAuth() {
      this.reset();
      axios.get("auth/principal").then(r => {
          this.login({
            label: r.data.label,
            name: r.data.name,
            xcsrf: r.headers['x-csrf-token']
          });
        })
        .catch(e => this.errorHandler(e))
        .finally(() => this.finallyHandler());
    },

    tryFormLogin() {
      this.reset();

      const form = new URLSearchParams();
      form.append('username', this.username);
      form.append('password', 'evolution');
      axios.post(this.formLoginPage, form).then(r => {
          this.login({
            label: r.data.label,
            name: r.data.name,
            xcsrf: r.headers['x-csrf-token']
          });
        })
        .catch(e => this.errorHandler(e))
        .finally(() => this.finallyHandler());
    }
  }

}
</script>
