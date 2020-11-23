<template>
<span class="specie" ref="main" @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">

  <span class="icon">
      <v-icon x-large :style="iconStyle" :color="iconColor">{{carnivorous?'mdi-paw':'mdi-cow'}}</v-icon>
  </span>

<span class="internal">
      <span class="traits">
        <Trait ref="traits" :key="`trait-${i}`" v-for="(t,i) in effectiveTraits" :specie="id" :index="t.index" :trait="t.trait" :additional="t.additional || false" @enter="enterTrait" @leave="leaveTrait" />
      </span>

<span class="stats">
        <span ref="SIZE" @mouseenter="enterStat('SIZE')" @mouseleave="leaveStat('SIZE')" :class="sizeColor?`${sizeColor}--text`:null"><v-icon :color="sizeColor">mdi-ruler</v-icon>{{effectiveSize}}</span>
<span class="spacer"></span>
<span ref="POPULATION"  @mouseenter="enterStat('POPULATION')" @mouseleave="leaveStat('POPULATION')" :class="populationColor?`${populationColor}--text`:null"><v-icon :color="populationColor">mdi-sigma</v-icon>{{effectivePopulation}}</span>
</span>

<span class="food">
        <v-icon v-for="(e,i) in eaten" :key="`food-${i}`" small :color="e.color">{{e.icon}}</v-icon>
      </span>

<span class="food" >
        <template v-if="fatTissue">
          <v-icon v-for="(e,i) in fatEaten" :key="`fat-${i}`" small :color="e.color">{{e.icon}}</v-icon>
        </template>
      </span>
</span>

</span>
</template>

<script>
import {
  mapActions,
  mapGetters,
  mapState
} from 'vuex';
import colors from 'vuetify/lib/util/colors';
import boundingBox from '@/mixins/boundingBox';
import Trait from '@/components/game/Trait';
export default {
  components: {
    Trait
  },
  name: 'Specie',
  props: {
    id: {
      type: String,
    },
    size: {
      type: Number,
    },
    population: {
      type: Number,
    },
    food: {
      type: Number,
    },
    fat: {
      type: Number,
    },
    traits: {
      type: Array,
    }
  },
  mixins: [boundingBox],
  computed: {
    carnivorous() {
      return this.effectiveTraits.some(t => t.trait === 'CARNIVOROUS');
    },

    fatTissue() {
      return this.effectiveTraits.some(t => t.trait === 'FAT_TISSUE');
    },

    eaten() {
      const out = [];
      const additional = this.food + this.deltaFood;
      for (var i = 0; i < 6; ++i) {
        const index = i + 1;
        const enabled = index <= this.effectivePopulation;
        const filled = index <= this.food;

        if (enabled || filled) {
          var altered = index > this.food && index <= additional;
          var color = altered ? 'primary' : null;
          const empty = !(filled || altered);
          var icon = this.carnivorous ? 'mdi-food-drumstick' : 'mdi-seed';

          if (index > this.effectivePopulation) {
            icon += '-off';
            color = 'warning';
          }

          if (empty)
            icon += '-outline';

          out.push({
            icon,
            color
          });
        }
      }
      return out;
    },

    fatEaten() {
      const out = [];
      const additional = (this.fat + this.deltaFat);
      for (var i = 0; i < 6; ++i) {
        const index = i + 1;
        const enabled = index <= this.effectiveSize;
        const filled = index <= this.fat;
        const altered = index > this.fat && index <= additional;
        const icon = (filled || altered) ? 'mdi-peanut' : 'mdi-peanut-outline';
        if (enabled || filled)
          out.push({
            color: altered ? 'primary' : null,
            icon
          });
      }
      return out;
    },

    wrapperStyle() {
      const theme = this.$vuetify.theme.defaults.dark;
      if (this.possibleSource) {
        return {
          backgroundColor: colors.grey.darken3,
          borderColor: theme.primary
        };
      } else if (this.currentTarget) {
        return {
          backgroundColor: colors.grey.darken3,
          borderColor: (this.currentInteraction.valid ? theme.success : theme.error)
        };
      } else if (this.possibleTarget) {
        return {
          borderColor: colors.grey.darken3,
          backgroundColor: colors.grey.darken4
        };
      }

      return null;
    },

    iconColor() {
      if (this.possibleSource)
        return 'primary';
      else if (this.currentTarget) {
        if (['target-size', 'target-population', 'target-trait'].includes(this.currentInteraction.interaction))
          return colors.grey.darken4;

        if (this.currentInteraction.valid)
          return 'success';
        else
          return 'error';
      } else
        return colors.grey.darken3;
    },

    iconStyle() {
      const style = {};
      if (this.possibleSource || this.currentTarget || this.possibleStart)
        style.transform = 'scale(1.5)';
      return style;
    },

    interactionEffects() {
      if (this.currents) {
        return this.currents.flatMap(c => {
          if (c.effects)
            return c.effects.filter(e => e.specie === this.id);
          return [];
        })
      }

      return [];
    },

    effectiveTraits() {
      if (this.currentTarget) {
        if (this.currentInteraction.type === 'add-trait') {
          return [...this.traits, {
            index: this.traits.length,
            trait: this.currentInteraction.trait,
            additional: true
          }];
        }

        if (this.currentInteraction.type === 'replace-trait') {
          const out = [...this.traits];
          const index = this.currentInteraction.index;
          out[index] = {
            index: index,
            trait: this.currentInteraction.trait,
            additional: true
          }
          return out;
        }
      }

      return this.traits;
    },

    effectiveSize() {
      const found = this.interactionEffects.find(e => e.size > -1);
      if (found)
        return found.size;

      if (this.currentInteraction && ['target-size'].includes(this.currentInteraction.interaction))
        return this.currentInteraction.size;

      return this.size;
    },

    effectivePopulation() {
      const found = this.interactionEffects.find(e => e.population > -1);
      if (found)
        return found.population;

      if (this.currentInteraction && ['target-population'].includes(this.currentInteraction.interaction))
        return this.currentInteraction.population;

      return this.population;
    },

    populationColor() {
      if (this.currentInteraction && ['target-population'].includes(this.currentInteraction.interaction))
        return 'success';

      if (this.effectivePopulation != this.population)
        return 'primary';

      return null;
    },

    sizeColor() {
      if (this.currentInteraction && this.currentTarget && this.currentInteraction.violations) {
        if (this.currentInteraction.violations.some(v => 'size' === v.type && !v.disabled))
          return 'error';
        else if (this.currentInteraction.violations.some(v => 'size' === v.type && v.disabled))
          return 'warning';
      }

      if (this.currentInteraction && ['target-size'].includes(this.currentInteraction.interaction))
        return 'success';

      return null;
    },

    deltaFat() {
      const delta = this.interactionEffects.filter(e => e.deltaFat > 0)
        .map(e => e.deltaFat).reduce((a, c) => a + c, 0);
      return delta;
    },

    deltaFood() {
      const delta = this.interactionEffects.filter(e => e.deltaFood > 0)
        .map(e => e.deltaFood).reduce((a, c) => a + c, 0);
      return delta;
    },

    currentInteraction() {
      if (this.currents)
        return this.findFirstInteraction(this.currents);

      return null;
    },

    possibleInteraction() {
      if (this.starteds)
        return this.findFirstInteraction(this.starteds);

      return null;
    },

    possibleStart() {
      return this.startOnSpecie === this.id;
    },

    possibleSource() {
      return this.possibleInteraction && this.possibleInteraction.interaction === 'source-specie';
    },

    possibleTarget() {
      return this.possibleInteraction && this.possibleInteraction.valid && ['target-specie', 'target-size', 'target-population', 'target-trait'].includes(this.possibleInteraction.interaction);
    },

    currentTarget() {
      return this.currentInteraction && ['target-specie', 'target-size', 'target-population', 'target-trait'].includes(this.currentInteraction.interaction);
    },

    ...mapState({
      starteds: state => state.action.starteds
    }),

    ...mapGetters({
      currents: 'action/currents',
      startOnSpecie: 'action/startOnSpecie'
    }),
  },
  methods: {
    ...mapActions({
      enterSpecie: 'selection/enterSpecie',
      leaveSpecie: 'selection/leaveSpecie',
      doEnterTrait: 'selection/enterTrait',
      doLeaveTrait: 'selection/leaveTrait',
      doEnterStat: 'selection/enterStat',
      doLeaveStat: 'selection/leaveStat'
    }),

    findFirstInteraction(actions) {
      const mapped = actions.flatMap(a => {
        const inter = this.computeInteraction(a);
        if (inter) {
          return [inter];
        }
        return [];
      });
      if (mapped.length > 0)
        return mapped[0];

      return null;
    },

    computeInteraction(action) {
      const id = this.id;
      const sizeId = `SIZE-${id}`;
      const populationId = `POPULATION-${id}`;

      function init(more) {

        const effects = action.effects ? action.effects.filter(e => e.specie === id) : null;
        const out = {
          ...more,
          type: action.type,
          valid: action.valid,
          violations: action.violations,
          effects
        };

        if ('add-trait' === action.type)
          out.trait = action.trait;

        return out;
      }

      if (action.specie === id) {
        const traitsId = this.traits.map(t => `${id}-${t.trait}`);
        if (traitsId.includes(action.target)) {
          return init({
            interaction: 'target-trait',
            index: action.position,
            trait: action.trait
          });
        }

        return init({
          interaction: 'source-specie'
        });
      } else if (action.target === id) {
        return init({
          interaction: 'target-specie'
        });
      } else if (action.target === sizeId) {
        return init({
          interaction: 'target-size',
          size: action.size
        });
      } else if (action.target === populationId) {
        return init({
          interaction: 'target-population',
          population: action.population
        });
      }

      return null;
    },

    leave() {
      this.leaveSpecie(this.prepareBox());
    },
    enter() {
      this.enterSpecie(this.prepareBox());
    },
    prepareBox() {
      return this.wrapEvent({});
    },
    leaveStat(stat) {
      const evt = this.prepareBox();
      evt.stat = stat;
      this.doLeaveStat(evt);
    },
    enterStat(stat) {
      const evt = this.prepareBox();
      evt.stat = stat;
      this.doEnterStat(evt);
    },
    wrapEvent(e) {
      return {
        ...e,
        specie: this.id
      };
    },
    leaveTrait(e) {
      this.doLeaveTrait(this.wrapEvent(e));
    },
    enterTrait(e) {
      this.doEnterTrait(this.wrapEvent(e));
    }
  }
}
</script>

<style scoped>
.specie {
  display: inline-block;
  position: relative;
  user-select: none;
  width: 120px;
  padding: 5px 5px 5px 2px;
  border-radius: 0px 0px 8px 0px;
  border-left: 2px solid #121212;
  overflow: hidden;
  transition: background-color 0.2s cubic-bezier(0.4, 0, 0.6, 1), border .2ms cubic-bezier(0.4, 0, 0.6, 1), width 0.2s ease-in-out;
}

.specie:not(:last-child) {
  margin: 0px 2px 0px 0px;
}

.internal {
  flex-direction: column;
}

.stats {
  margin-top: 3px;
}

.traits {
  height: 25px;
}

.stats,
.traits,
.food,
.internal {
  display: flex;
  flex-wrap: nowrap;
}

.food {
  height: 16px;
}

.spacer {
  flex-grow: 1;
}

.stats>span {
  text-align: center;
  white-space: nowrap;
}

.specie>.icon {
  z-index: 0;
  pointer-events: none;
  top: 0;
  position: absolute;
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}
</style>
