<template>
<v-system-bar class="text-uppercase">

  <template v-if="loaded">
    <span>{{phase}}</span>
    <v-icon>mdi-chevron-right</v-icon>

    <v-chip v-for="(a,i) in actions" :key="`action-${i}`" label x-small :color="a.color" class="mr-1">
      <v-avatar left>
        <v-icon small>{{a.icon}}</v-icon>
      </v-avatar>{{a.label}}
    </v-chip>
  </template>
  <v-spacer></v-spacer>
  <v-icon>{{connected?'mdi-wifi':'mdi-wifi-off'}}</v-icon>
</v-system-bar>
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

  return null;
}

export default {
  computed: {
    ...mapState({
      loaded: state => state.gamestate.loaded,
      phase: state => state.gamestate.phase,
      connected: state => state.user.connected
    }),
    ...mapGetters({
      currents: 'action/currents',
      startedsType: 'action/startedsType'
    }),
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
    formatAction(type) {
      return type.replace(/-/g, ' ');
    }
  }
};
</script>
