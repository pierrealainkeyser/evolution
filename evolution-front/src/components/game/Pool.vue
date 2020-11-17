<template>
<div @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">
  <span :class="foodColor?`${foodColor}--text`:null">
    <v-icon :color="foodColor" large>mdi-barley</v-icon> {{food}}
  </span>
  <span :class="cardsColor?`${cardsColor}--text`:null">
  <v-icon :color="cardsColor" large>mdi-cards</v-icon> {{cards}}
</span>
</div>
</template>

<script>
import {
  mapActions,
  mapGetters
} from 'vuex';
import colors from 'vuetify/lib/util/colors';
export default {
  computed: {
    ...mapGetters({
      foodPool: 'gamestate/food',
      cardsPool: 'gamestate/cards',
      currents: 'action/currents',
      startedsType: 'action/startedsType'
    }),
    foodColor() {
      if (this.food != this.foodPool)
        return 'primary';
      return null;
    },

    current() {
      if (this.currents && this.currents.length)
        return this.currents[0];

      return null;
    },

    food() {
      if (this.current && this.current.type === 'feed' && this.current.valid) {
        const delta = this.current.effects.reduce((accumulator, e) => accumulator + (e.deltaFat || 0) + (e.deltaFood || 0), 0);
        return Math.max(0, this.foodPool - delta);
      }
      return this.foodPool;
    },
    cardsColor() {
      if (this.cards != this.cardsPool)
        return 'primary';
      return null;
    },
    cards() {
      if (this.current && this.current.type === 'selectFood' && this.current.valid) {
        return this.cardsPool + 1;
      }

      return this.cardsPool;
    },
    wrapperStyle() {
      const theme = this.$vuetify.theme.defaults.dark;
      if (this.current && (this.current.type === 'feed' || this.current.type === 'selectFood')) {
        return {
          backgroundColor: colors.grey.darken3,
          borderColor: (this.current.valid ? theme.success : theme.error)
        };
      } else if (this.startedsType && (this.startedsType.includes('feed') || this.startedsType.includes('selectFood'))) {
        return {
          borderColor: colors.grey.darken3,
          backgroundColor: colors.grey.darken4
        };
      }

      return null;
    },
  },
  methods: {
    ...mapActions({
      enter: 'selection/enterPool',
      leave: 'selection/leavePool'
    })
  }
}
</script>

<style scoped>
.pool {
  padding: 20px;
  user-select: none;
  border-radius: 0px 0px 8px 0px;
  border-left: 3px solid #121212;
}
</style>
