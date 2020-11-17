<template>
<div class="cardsWrapper">
  <transition-group name="cards" tag="div" class="d-flex">
    <Card v-for="s in hands" :key="s.id" :id="s.id" :trait="s.trait" :food="s.food" />
  </transition-group>
</div>
</template>

<script>
import {
  mapState
} from 'vuex';
import Card from '@/components/game/Card';

export default {
  components: {
    Card
  },
  computed: {
    ...mapState({
      hands: state => state.gamestate.hands
    })
  }
}
</script>

<style scoped>
.cardsWrapper {
  user-select: none;
  position: absolute;
  bottom: 5px;
  right: 5px;
  border-top: 1px solid white;
}

.cards-enter-active {
  animation: cards 0.2s ease-in-out;
}

.cards-leave-active {
  animation: cards 0.2s reverse;
}

@keyframes cards {
  0% {
    padding-left: 0px;
    padding-right: 0px;
    width: 0px;
  }

  100% {
    padding-left: 2px;
    padding-right: 5px;
    width: 120px;
  }
}
</style>
