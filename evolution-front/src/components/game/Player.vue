<template>
<div>
  <div :style="playerStyle" class="playerWrapper">
    <v-icon>{{playerIcon}}</v-icon> {{player.name}}
  </div>
  <transition-group name="specie" tag="div" class="d-flex">
    <Specie v-for="s in species" :key="s.id" :id="s.id" :size="s.size" :population="s.population" :food="s.food" :fat="s.fat" :traits="s.traits" />
  </transition-group>
</div>
</template>

<script>
import {
  mapGetters,
  mapState
} from 'vuex';
import colors from 'vuetify/lib/util/colors';
import Specie from '@/components/game/Specie';

export default {
  components: {
    Specie
  },
  props: {
    playerId: {
      type: Number,
    }
  },
  computed: {
    ...mapState({
      myself: state => state.gamestate.myself
    }),
    ...mapGetters({
      players: 'gamestate/players'
    }),
    player() {
      return this.players[this.playerId];
    },
    species() {
      return this.player.species;
    },
    isMyself() {
      return this.myself == this.playerId;
    },
    playerIcon() {
      if (this.isMyself) {
        return 'mdi-account';
      }

      return 'mdi-account-outline';
    },
    playerStyle() {
      const style = {};
      if (this.isMyself) {
        style['border-top'] = '1px solid white';
        style['border-left'] = '2px solid white';
        style.backgroundColor = colors.grey.darken3;
      }

      return style;
    }
  }
}
</script>

<style scoped>
.playerWrapper {
  user-select: none;
  padding-left: 2px;
  padding-top: 2px;
  transition: background-color 0.2s cubic-bezier(0.4, 0, 0.6, 1);
}

.specie-enter-active {
  animation: specie 0.2s ease-in-out;
}

.specie-leave-active {
  animation: specie 0.2s reverse;
}

@keyframes specie {
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
