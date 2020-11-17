<template>
<div>
  <div class="playerWrapper">
    <div class="d-flex header" :class="playerClass">
      <v-icon>{{playerIcon}}</v-icon> <span>{{player.name}}</span>
      <v-spacer />
      <v-icon small v-if="!isMyself">{{player.connected?'mdi-wifi':'mdi-wifi-off'}}</v-icon>
    </div>
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
    playerClass() {
      const classes = [];
      if (this.isMyself) {
        classes.push('myself');
      }
      classes.push(this.player.status);
      return classes.join(" ");

    }
  }
}
</script>

<style scoped>
.playerWrapper {
  user-select: none;
  padding-left: 2px;
  padding-top: 2px;
  transition: background-color 0.2s ease-in-out;
}

.header.myself {
  border-top: 1px solid white;
  border-left: 2px solid white;
}

.header {
  position: relative;
  padding: 2px;
}

.header > span{
  z-index: 1;
}

.header::before {
  content: "";
  display: block;
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  width: 0%;
  transition: width 0.2s cubic-bezier(0.4, 0, 0.6, 1);
  background: rgb(66, 66, 66);
  z-index: 0;
}

.header.active::before {
  width: 100%;
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
