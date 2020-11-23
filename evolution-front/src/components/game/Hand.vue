<template>
<div class="cardsWrapper ">
  <transition-group name="cards" tag="div" class="d-flex align-center">
    <v-btn v-if="playcard" key="pass" class="pass" @click="pass">{{$t('game.pass')}}</v-btn>
    <Card v-for="s in hands" :key="s.id" :id="s.id" :trait="s.trait" :food="s.food" />
  </transition-group>
</div>
</template>

<script>
import {
  mapGetters,
  mapActions,
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
    }),
    ...mapGetters({
      playcard: 'action/playcard',
    }),
  },
  methods: {
    ...mapActions({
      pass: 'io/pass'
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
  height: 55px;
  border-top: 1px solid white;
}


.cardsWrapper>div{
  height: 100%;
}

.pass.cards-enter-active {
  animation: pass 0.2s ease-in-out;
}

.pass.cards-leave-active {
  animation: pass 0.2s reverse;
}

.card.cards-enter-active {
  animation: cards 0.2s ease-in-out;
}

.card.cards-leave-active {
  animation: cards 0.2s reverse;
}

.cardsWrapper .v-btn.pass {
  margin-right: 10px;
  margin-left: 10px;
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

@keyframes pass {
  0% {
    margin-left: 0px;
    margin-right: 0px;
    max-width: 0px;
  }

  100% {
    margin-left: 10px;
    margin-right: 10px;
    max-width: 100%;
  }
}
</style>
