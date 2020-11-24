<template>
<v-app-bar dense app>
  <v-app-bar-nav-icon @click="toHome">
    <v-icon>mdi-paw-outline</v-icon>
  </v-app-bar-nav-icon>
  <v-toolbar-items class="align-center" v-if="loaded">
    <span class="text-uppercase">{{stepLabel}}</span>
    <v-icon v-if="actions.length">mdi-chevron-right</v-icon>
    <v-chip v-for="(a) in actions" :key="`action-${a.label}`" label small :color="a.color" class="action mr-1">
      <v-avatar left>
        <v-icon small>{{a.icon}}</v-icon>
      </v-avatar>{{a.label}}
    </v-chip>
  </v-toolbar-items>
  <v-spacer></v-spacer>
  <v-toolbar-items class="align-center">
    <span class="mr-2">{{username}}</span>
    <v-icon small>{{connected?'mdi-wifi':'mdi-wifi-off'}}</v-icon>
  </v-toolbar-items>
</v-app-bar>
</template>

<script>
import {
  mapGetters,
  mapState
} from 'vuex';

function iconFor(actionType) {
  if (actionType === 'attack') {
    return 'mdi-paw';
  }
  if (actionType === 'feed') {
    return 'mdi-barley';
  }
  if (actionType === 'intelligent-feed') {
    return 'mdi-face-shimmer';
  }
  if (actionType === 'increase-size') {
    return 'mdi-ruler';
  }
  if (actionType === 'increase-population') {
    return 'mdi-sigma';
  }

  if (actionType === 'add-trait') {
    return 'mdi-pencil-plus';
  }

  if (actionType === 'replace-trait') {
    return 'mdi-pencil-plus-outline';
  }

  if (actionType === 'select-food') {
    return 'mdi-barley';
  }

  if (actionType === 'add-new-specie') {
    return 'mdi-plus';
  }

  return null;
}

export default {
  computed: {
    ...mapState({
      loaded: state => state.io.loaded,
      step: state => state.gamestate.step,
      connected: state => state.ws.connected
    }),
    ...mapGetters({
      currents: 'action/currents',
      startedsType: 'action/startedsType',
      username: 'user/get'
    }),
    stepLabel() {
      return this.$t(`game.step.${this.step}`);
    },
    current() {
      if (this.currents && this.currents.length) {
        return this.currents[0];
      }
      return null;
    },
    actions() {
      if (this.current) {
        const type = this.current.type;
        return [{
          label: this.formatAction(type),
          color: this.current.valid ? 'success' : 'error',
          icon: iconFor(type)
        }];
      }

      if (this.startedsType) {
        return this.startedsType.map(type => ({
          label: this.formatAction(type),
          icon: iconFor(type)
        }));
      }

      return [];
    }
  },
  methods: {
    toHome() {
      this.$router.push('/');
    },
    formatAction(type) {
      return this.$t(`game.action.${type}`);
    }
  }
};
</script>
