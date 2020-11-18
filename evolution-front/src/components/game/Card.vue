<template>
<span class="card" ref="main" @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">

  <span class="icon">
      <v-icon x-large :style="iconStyle" :color="iconColor">mdi-cards</v-icon>
  </span>

<span class="internal">
      <span>
        <v-tooltip top>
            <template #activator="{ on, attrs }">
              <TraitIcon :trait="trait" v-bind="attrs" v-on="on"/>
            </template>
            <span>{{traitTooltip}}</span>
</v-tooltip>
</span>
<span class="spacer"></span>
<span ><v-icon>mdi-barley</v-icon>{{food}}</span>
</span>

</span>
</template>

<script>
import {
  mapActions,
  mapState,
  mapGetters
} from 'vuex';
import colors from 'vuetify/lib/util/colors';
import boundingBox from '@/mixins/boundingBox';
import TraitIcon from '@/components/game/TraitIcon';
export default {
  components: {
    TraitIcon
  },
  name: 'Card',
  props: {
    id: {
      type: String,
    },
    trait: {
      type: String,
    },
    food: {
      type: Number,
    }
  },
  mixins: [boundingBox],
  computed: {
    traitTooltip() {
      return this.$t(`game.trait.${this.trait}`);
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

    possibleStart() {
      return this.startOnCard && this.startOnCard.id === this.id;
    },

    possibleSource() {
      return this.possibleInteraction && this.possibleInteraction.interaction === 'source-card';
    },

    currentTarget() {
      return this.currentInteraction && ['intelligent-feed'].includes(this.currentInteraction.interaction);
    },

    possibleTarget() {
      return this.possibleInteraction && this.possibleInteraction.valid && ['intelligent-feed'].includes(this.possibleInteraction.interaction);
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
    ...mapState({
      starteds: state => state.action.starteds
    }),
    ...mapGetters({
      currents: 'action/currents',
      startOnCard: 'action/startOnCard'
    })
  },
  methods: {
    ...mapActions({
      enterCard: 'selection/enterCard',
      leaveCard: 'selection/leaveCard'
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

      function init(more) {
        const out = {
          ...more,
          type: action.type,
          valid: action.valid
        };
        return out;
      }

      if (action.card === id) {

        if (action.type === 'intelligent-feed') {
          return init({
            interaction: 'intelligent-feed'
          });
        }

        return init({
          interaction: 'source-card'
        });
      }

      return null;
    },

    leave() {
      this.leaveCard(this.prepareBox());
    },
    enter() {
      this.enterCard(this.prepareBox());
    },
    prepareBox() {
      return this.wrapEvent({});
    },
    wrapEvent(e) {
      return {
        ...e,
        card: this.id,
        trait: this.trait,
        food: this.food,
      };
    }
  }
}
</script>

<style scoped>
.card {
  display: inline-block;
  position: relative;
  user-select: none;
  width: 90px;
  padding: 17px 10px 10px 2px;
  border-radius: 0px 0px 8px 0px;
  border-left: 2px solid #121212;
  overflow: hidden;
  transition: background-color 0.2s cubic-bezier(0.4, 0, 0.6, 1), border .2ms cubic-bezier(0.4, 0, 0.6, 1), width 0.2s ease-in-out;
}

.card:not(:last-child) {
  margin: 0px 2px 0px 0px;
}

.internal {
  display: flex;
  flex-wrap: nowrap;
  margin-top: 3px;
  text-align: center;
  white-space: nowrap;
}

.card>.icon {
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
