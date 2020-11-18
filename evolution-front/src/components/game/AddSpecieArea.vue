<template>
<div class="addSpecieArea d-flex align-center" @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">
  <v-icon :style="iconStyle" :color="iconColor">mdi-plus</v-icon>
</div>
</template>

<script>
import {
  mapActions,
  mapState,
  mapGetters
} from 'vuex';
import colors from 'vuetify/lib/util/colors';
export default {
  name: 'addSpecieArea',
  props: {
    position: {
      type: String
    }
  },
  computed: {
    wrapperStyle() {
      const theme = this.$vuetify.theme.defaults.dark;
      const style = {}

      if (this.currentTarget || this.possibleTarget) {
        if (this.currentTarget) {
          style.backgroundColor = colors.grey.darken3;
          style.borderLeft = "2px solid";
          style.borderColor = (this.currentInteraction.valid ? theme.success : theme.error);
        } else if (this.possibleTarget) {
          style.backgroundColor = colors.grey.darken4;
          style.borderLeft = "2px solid";
          style.borderColor = colors.grey.darken3;
        }
      } else {
        style.visibility = 'hidden';
        if ('left' === this.position)
          style.marginRight = '0px';
        style.width = '0px';
      }
      return style;
    },

    iconColor() {
      if (this.currentTarget)
        return 'success';
      else
        return colors.grey.darken3;
    },

    iconStyle() {
      const style = {};
      if (this.currentTarget)
        style.transform = 'scale(1.5)';
      return style;
    },

    currentInteraction() {
      if (this.currents)
        return this.findFirstInteraction(this.currents);

      return null;
    },

    currentTarget() {
      return !!this.currentInteraction;
    },

    possibleInteraction() {
      if (this.starteds)
        return this.findFirstInteraction(this.starteds);

      return null;
    },

    possibleTarget() {
      return this.possibleInteraction && this.possibleInteraction.valid;
    },

    ...mapGetters({
      currents: 'action/currents'
    }),
    ...mapState({
      starteds: state => state.action.starteds
    })
  },
  methods: {
    ...mapActions({
      enterSpecieArea: 'selection/enterSpecieArea',
      leaveSpecieArea: 'selection/leaveSpecieArea'
    }),
    enter() {
      this.enterSpecieArea(this.prepareBox());
    },
    leave() {
      this.leaveSpecieArea(this.prepareBox());
    },
    prepareBox() {
      return this.position;
    },

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

      function init(more) {
        const out = {
          ...more,
          type: action.type,
          valid: action.valid
        };
        return out;
      }


      if (action.type === 'add-new-specie' && action.position === this.position) {
        return init({});
      }


      return null;
    },
  }
}
</script>

<style scoped>
.addSpecieArea {
  border-radius: 0px 0px 8px 0px;
  overflow: hidden;
  width: 32px;
  transition: background-color 0.2s cubic-bezier(0.4, 0, 0.6, 1), border .2ms cubic-bezier(0.4, 0, 0.6, 1), width 0.2s ease-in-out;
}

.addSpecieArea.left {
  margin-right: 2px;
}
</style>
