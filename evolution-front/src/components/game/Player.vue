<template>
<div>
  <div class="playerWrapper">
    <div class="d-flex header" :class="playerClass">
      <v-icon>{{playerIcon}}</v-icon> <span>{{player.name}}</span>
      <v-spacer />
      <template v-if="!isMyself">
        <v-icon small class="ml-2">mdi-cards</v-icon><span > {{handSize}}</span>
        <v-icon small class="ml-1">{{player.connected?'mdi-wifi':'mdi-wifi-off'}}</v-icon>
    </template>
    </div>
  </div>
  <transition-group name="specie" tag="div" class="d-flex">
    <AddSpecieArea v-if="isMyself" key="l" position="left" class="left" :playerId="playerId" />
    <Specie v-for="s in species" :key="s.id" :id="s.id" :size="s.size" :population="s.population" :food="s.food" :fat="s.fat" :traits="s.traits" ref="species" />
    <AddSpecieArea v-if="isMyself" key="r" position="right" :playerId="playerId" />
  </transition-group>
</div>
</template>

<script>
import {
  mapGetters,
  mapState
} from 'vuex';
import Specie from '@/components/game/Specie';
import AddSpecieArea from '@/components/game/AddSpecieArea';

export default {
  components: {
    Specie,
    AddSpecieArea
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
      players: 'gamestate/players',
    }),

    player() {
      return this.players[this.playerId];
    },
    species() {
      return this.player.species;
    },
    handSize() {
      return this.player.hands;
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
      const status = this.player.status;
      if (['select_food', 'play_cards', 'feeding'].includes(status))
        classes.push('active');
      return classes.join(" ");

    }
  },
  methods: {
    specieBox(specieId) {
      const index = this.species.findIndex(s => s.id === specieId);
      if (index > -1)
        return this.$refs.species[index].box();
      return null;
    }
  }
}
</script>

<style scoped>
.playerWrapper {
  user-select: none;
  padding-top: 2px;
}

.header.myself {
  border-top: 1px solid white;
  border-left: 2px solid white;

}

.header {
  position: relative;
  padding: 2px;
  padding-left: 5px;
  padding-right: 5px;
}

.header>span {
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
  background: #ff4081;
  z-index: 0;
}

.header.myself::before {
  background: #2196f3;
}

.header.active::before {
  width: 100%;
}

.specie.specie-enter-active {
  animation: specie 0.2s ease-in-out;
}

.specie.specie-leave-active {
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
