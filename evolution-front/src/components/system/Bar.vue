<template>
<v-system-bar class="text-uppercase">


  <span>Feeding</span>
  <v-icon>mdi-chevron-right</v-icon>

  <v-chip v-for="(a,i) in actions" :key="`action-${i}`" label x-small :color="a.color" class="mr-1">
    <v-avatar left>
      <v-icon small>{{a.icon}}</v-icon>
    </v-avatar>{{a.label}}
  </v-chip>
  <v-spacer></v-spacer>
  <v-icon>mdi-wifi</v-icon>
</v-system-bar>
</template>

<script>
import {
  mapGetters
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

  return null;
}

export default {
  computed: {
    ...mapGetters({
      current: 'action/current',
      startedsType: 'action/startedsType'
    }),
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
