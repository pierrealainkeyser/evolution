<template>
<span class="specie" ref="main" @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">

  <span class="icon">
      <v-icon x-large :style="iconStyle" :color="iconColor">{{carnivorous?'mdi-paw':'mdi-cow'}}</v-icon>
  </span>

<span class="internal">
      <span class="traits">
        <Trait ref="traits" :key="`trait-${i}`" v-for="(t,i) in traits" :specie="id" :index="t.index" :trait="t.trait" :additional="t.additional || false" @enter="enterTrait" @leave="leaveTrait" />
      </span>

<span class="stats">
        <span ref="size" @mouseenter="enterStat('size')" @mouseleave="leaveStat('size')" :class="sizeColor?`${sizeColor}--text`:null"><v-icon :color="sizeColor">mdi-ruler</v-icon>{{size}}</span>
<span class="spacer"></span>
<span ref="population"  @mouseenter="enterStat('population')" @mouseleave="leaveStat('population')" :class="populationColor?`${populationColor}--text`:null"><v-icon :color="populationColor">mdi-sigma</v-icon>{{effectivePopulation}}</span>
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
  mapGetters
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
      return this.traits.some(t => t.trait === 'CARNIVOROUS');
    },
    fatTissue() {
      return this.traits.some(t => t.trait === 'FAT_TISSUE');
    },
    eaten() {
      const out = [];
      const additional = this.food + this.deltaFood;
      for (var i = 0; i < 6; ++i) {
        const index = i + 1;
        const enabled = index <= this.population;
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
        const enabled = index <= this.size;
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
      if (this.currentOrStartedOnSelf) {
        return {
          backgroundColor: colors.grey.darken3,
          borderColor: theme.primary
        };

      } else if (this.currentTargetOnSelf) {
        return {
          backgroundColor: colors.grey.darken3,
          borderColor: (this.currentTargetOnSelf.valid ? theme.success : theme.error)
        };
      } else if (this.startOnSelf) {
        return {
          borderColor: colors.grey.darken3,
        };
      }
      return null;
    },
    iconColor() {
      if (this.currentOrStartedOnSelf)
        return 'primary';
      else if (this.currentTargetOnSelf)
        if (this.currentTargetOnSelf.valid)
          return 'success';
        else
          return 'error';
      else
        return colors.grey.darken3;
    },
    startOnSelf() {
      return this.startOnSpecie === this.id;
    },
    iconStyle() {
      const style = {};
      if (this.onSelf) {
        if (this.startOnSelf) {
          style.transform = 'scale(1.5)';
        } else if (this.currentTargetOnSelf && this.current.valid) {
          style.transform = 'scale(1.5)';
        }
      }
      return style;
    },
    effectsOnSelf() {
      const act = this.current;
      var found = null;
      if (act && act.valid && act.effects) {
        found = act.effects.filter(e => e.specie === this.id);
      }

      return found || [];
    },
    effectivePopulation() {
      const found = this.effectsOnSelf.find(e => e.population > -1);
      if (found)
        return found.population;
      return this.population;
    },
    populationColor() {
      if (this.effectivePopulation != this.population)
        return 'primary';
      return null;
    },
    sizeColor() {
      if (this.currentTargetOnSelf && this.current.violations && this.current.violations.some(v => 'size' === v.type))
        return 'error';
      return null;
    },
    deltaFat() {
      const found = this.effectsOnSelf.find(e => e.deltaFat > 0);
      if (found)
        return found.deltaFat;
      return 0;
    },
    deltaFood() {
      const found = this.effectsOnSelf.find(e => e.deltaFood > 0);
      if (found)
        return found.deltaFood;

      return 0;
    },
    onSelf() {
      return this.id === this.specieId;
    },
    currentOrStartedOnSelf() {
      return this.id === this.specieSource || (this.current && this.current.specie === this.id);
    },
    currentTargetOnSelf() {
      if (this.current && this.current.target === this.id)
        return this.current;

      return null;
    },
    ...mapGetters({
      startOnSpecie: 'action/startOnSpecie',
      specieSource: 'action/specieSource',
      current: 'action/current',
      specieId: 'selection/specieId'
    })
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
  border-left: 3px solid #121212;
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
