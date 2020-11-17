<template>
<span ref="main" class="trait" @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">
    <v-tooltip top>
        <template #activator="{ on, attrs }">
          <TraitIcon :trait="trait" :color="traitColor"  v-bind="attrs" v-on="on"/>
        </template>
        <span>{{traitTooltip}}</span>
</v-tooltip>
</span>
</template>

<script>
import colors from 'vuetify/lib/util/colors';
import boundingBox from '@/mixins/boundingBox';
import TraitIcon from '@/components/game/TraitIcon';
import {
  mapGetters
} from 'vuex';
export default {
  name: 'Trait',
  components: {
    TraitIcon
  },
  props: {
    index: {
      type: Number
    },
    trait: {
      type: String
    },
    specie: {
      type: String
    },
    additional: {
      type: Boolean
    }
  },
  mixins: [boundingBox],
  computed: {
    ...mapGetters({
      currents: 'action/currents',
    }),
    traitId() {
      return `${this.specie}-${this.trait}`;
    },
    traitTooltip() {
      return this.trait.replace(/_/g, ' ');
    },

    traitColor() {
      const theme = this.$vuetify.theme.defaults.dark;
      if (this.additional)
        return theme.success;


      const act = this.currents && this.currents.length ? this.currents[0] : null;
      if (act) {

        if (act.violations && act.violations.some(v => 'trait' === v.type && v.trait === this.traitId && v.disabled))
          return theme.warning;

        if (act.violations && act.violations.some(v => 'trait' === v.type && v.trait === this.traitId))
          return theme.error;

        if (act.violations && act.violations.some(v => 'trait' === v.type && v.disabled === this.traitId))
          return theme.primary;

        if (act.effects && act.effects.some(e => e.specie === this.specie && e.traits && e.traits.includes(this.trait)))
          return theme.primary;
      }

      return colors.grey.lighten1;
    },

    wrapperStyle() {
      return {
        'color': this.traitColor
      };
    },
  },
  methods: {

    prepareEvent() {
      return {
        index: this.index,
        trait: this.trait
      };
    },
    leave() {
      this.$emit('leave', this.prepareEvent());
    },
    enter() {
      this.$emit('enter', this.prepareEvent());
    }
  }
}
</script>

<style scoped>
.trait {
  display: inline-block;
  width: 38px;
  overflow: hidden;
  margin: 1px;
  text-align: center;
}
</style>
