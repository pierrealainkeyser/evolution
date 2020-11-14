<template>
<div @mouseenter="enter" @mouseleave="leave" :style="wrapperStyle">
  <span :class="foodColor?`${foodColor}--text`:null">
    <v-icon :color="foodColor" large>mdi-barley</v-icon> {{food}}
  </span>
  <span>
  <v-icon large>mdi-cards</v-icon> {{cards}}
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
      current: 'action/current',
    }),
    foodColor() {
      if (this.food != this.foodPool)
        return 'primary';
      return null;
    },
    cards() {
      return this.cardsPool;
    },
    food() {
      if (this.current && this.current.type === 'feed' && this.current.valid) {
        const delta = this.current.effects.reduce((accumulator, e) => accumulator + (e.deltaFat || 0) + (e.deltaFood || 0), 0);
        return this.foodPool - delta;
      }
      return this.foodPool;
    },
    wrapperStyle() {
      const theme = this.$vuetify.theme.defaults.dark;
      if (this.current && this.current.type === 'feed') {
        return {
          backgroundColor: colors.grey.darken3,
          borderColor: (this.current.valid ? theme.success : theme.error)
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
