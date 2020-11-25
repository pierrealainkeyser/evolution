<template>
<v-container fluid class="areaWrapper d-flex align-start">

  <div class="area flex-grow-1" @mousedown="mousedown" @mouseup="mouseup" ref="container" :style="containerStyle">
    <v-fade-transition mode="out-in">
      <div class="area" v-if="loaded">
        <v-btn icon small @click="incRotation(1)">
          <v-icon>mdi-rotate-left</v-icon>
        </v-btn>
        <v-btn icon small @click="incRotation(-1)">
          <v-icon>mdi-rotate-right</v-icon>
        </v-btn>

        <v-btn v-if="scoreBoards" icon small @click="showScore=true">
          <v-icon>mdi-trophy</v-icon>
        </v-btn>


        <Player ref="players" :playerId="index" class="player" v-for="(p,index) in players" :key="index" :style="computeStyle(index)" />
        <Pool ref="pool" class="pool" :style="poolStyle" />
        <Hand />

        <AnimationLayer />


        <v-fade-transition>
          <v-progress-circular v-if="sendingData" class="working" indeterminate :width="2" :value="0" :size="28" color="accent">
            <v-icon small color="grey darken-1">mdi-cloud-upload-outline</v-icon>
          </v-progress-circular>
        </v-fade-transition>
      </div>
      <v-progress-circular v-else class="loading" indeterminate :size="100" color="accent">
        <v-icon large>mdi-cloud-sync-outline</v-icon>
      </v-progress-circular>
    </v-fade-transition>
  </div>
  <Events />




  <v-dialog v-model="showScore">
    <v-card>
      <v-card-title class="grey darken-3">
        <v-icon class="pr-1">mdi-trophy</v-icon>
        {{$t('game.score.title')}}
      </v-card-title>
      <v-card-text class="pa-3">

        <v-simple-table>
          <thead>
            <tr>
              <td>{{$t('game.score.player')}}</td>
              <td>{{$t('lobby.game.results')}}</td>
              <td class="text-center">{{$t('game.score.food')}}</td>
              <td class="text-center">{{$t('game.score.population')}}</td>
              <td class="text-center">{{$t('game.score.traits')}}</td>
              <td class="text-center">{{$t('lobby.game.score')}}</td>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(s,i) in scoreBoards" :key="i" :class="s.alpha?'accent':null">
              <td>{{s.player}}</td>
              <td>
                {{$t(s.alpha?'lobby.game.winner':'lobby.game.looser')}}
              </td>
              <td class="text-center">
                {{s.food}}
              </td>
              <td class="text-center">
                {{s.population}}
              </td>
              <td class="text-center">
                {{s.traits}}
              </td>
              <td class="text-center">
                <strong>{{s.score}}</strong>
              </td>
            </tr>
          </tbody>
        </v-simple-table>

      </v-card-text>
      <v-card-actions>
        <v-btn @click="showScore=false">{{$t('game.score.close')}}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

</v-container>
</template>

<script>
import {
  mapState,
  mapActions,
  mapGetters
} from 'vuex';

import Player from '@/components/game/Player';
import Hand from '@/components/game/Hand';
import Pool from '@/components/game/Pool';
import AnimationLayer from '@/components/game/AnimationLayer';
import Events from '@/components/game/Events';

export default {
  name: 'GameView',
  components: {
    Player,
    Pool,
    Hand,
    AnimationLayer,
    Events
  },
  props: {
    gameId: {
      type: String,
    }
  },
  data() {
    return {
      container: {
        width: 0,
        height: 0
      },
      offset: {
        vertical: 40,
        horizontal: 50
      },
      pool: {
        width: 0,
        height: 0
      },
      showScore: false,
    };
  },
  computed: {
    ...mapState({
      rotation: state => state.selection.rotation,
      started: state => !!state.action.starteds,
      loaded: state => state.io.loaded,
      connecting: state => state.io.connecting,
      sendingData: state => state.io.sendingData
    }),
    ...mapGetters({
      startable: 'action/startable',
      activable: 'action/activable',
      players: 'gamestate/players',
      scoreBoards: 'gamestate/scoreBoards',
    }),
    degForPlayer() {
      return 360.0 / this.players.length;
    },
    containerStyle() {
      const style = {};
      if (this.activable) {
        if (this.activable.valid)
          style.cursor = 'pointer';
        else
          style.cursor = 'grab';
      } else if (this.startable) {
        style.cursor = 'pointer';
      } else if (this.started) {
        style.cursor = 'grab';
      }

      return style;
    },
    containerCenter() {
      const container = this.container;
      return {
        left: container.width / 2,
        top: container.height / 2,
      };
    },
    containerLocations() {
      const length = this.players.length;
      const increase = Math.PI * 2 / length;
      const base = -Math.PI / 2;


      var paddingL = 160;
      var paddingT = 60;
      if (Array.isArray(this.$refs.player)) {

        const maxSizes = {
          width: 0,
          height: 0
        };

        this.$refs.player.forEach(e => {
          const w = e.$el.clientWidth;
          const h = e.$el.clientHeight;
          maxSizes.width = Math.max(maxSizes.width, w);
          maxSizes.height = Math.max(maxSizes.height, h);
        });

        paddingL = maxSizes.width / 2;
        paddingT = maxSizes.height / 2;
      }
      const centerLeft = this.containerCenter.left;
      const centerTop = this.containerCenter.top - this.offset.vertical;
      const radiusL = centerLeft - paddingL - this.offset.horizontal;
      const radiusT = centerTop - paddingT;

      var locations = this.players.map((p, index) => {
        const i = (index - this.rotation + length) % length;
        const angle = base + (i * increase);
        return {
          left: radiusL * Math.cos(angle),
          top: radiusT * Math.sin(angle)
        };
      })

      locations = locations.map(location => ({
        left: location.left + centerLeft,
        top: location.top + centerTop
      }));

      if (Array.isArray(this.$refs.player)) {
        locations = locations.map((location, index) => {
          const player = this.$refs.player[index];
          if (player) {
            const elem = player.$el;
            if (elem.clientWidth && elem.clientHeight) {
              return {
                left: location.left - (elem.clientWidth / 2),
                top: location.top - (elem.clientHeight / 2)
              };
            }
          }
          return location;
        });
      } else {
        locations = locations.map((location, index) => {
          const pl = this.players[index];
          if (pl) {
            const estimated = pl.species.length * 120;
            return {
              left: location.left - (estimated / 2),
              top: location.top - (125 / 2)
            };
          }
          return location;
        });
      }

      return locations;
    },
    poolStyle() {
      const center = this.containerCenter;
      const pool = this.pool;

      const left = center.left - (pool.width / 2) - this.offset.horizontal;
      const top = center.top - (pool.height / 2) - this.offset.vertical;

      return {
        left: `${left}px`,
        top: `${top}px`
      }
    }
  },
  created() {
    window.addEventListener("resize", this.updatingContainers);
  },
  destroyed() {
    window.removeEventListener("resize", this.updatingContainers);
  },
  mounted() {
    this.updatingContainers();
    this.connectGame(this.gameId);
  },
  beforeDestroy() {
    this.resetGame();
  },
  watch: {
    scoreBoards() {
      if (this.scoreBoards)
        this.showScore = true;
    },
  },
  methods: {
    ...mapActions({
      mousedown: 'action/mousedown',
      mouseup: 'action/mouseup',
      mousemove: 'selection/mouseMove',
      incRotation: 'selection/incRotation',
      connectGame: 'io/connect',
      resetGame: 'io/reset'
    }),
    specieBox(specieId) {
      const index = specieId.match(/\d+p(\d+)/)[1];
      return this.$refs.players[index].specieBox(specieId);
    },
    updateContainers() {
      const container = this.$refs.container;
      this.container.height = container.clientHeight;
      this.container.width = container.clientWidth;

      if (this.$refs.pool) {
        const pool = this.$refs.pool.$el;
        this.pool.height = pool.clientHeight;
        this.pool.width = pool.clientWidth;
      }
    },
    updatingContainers() {
      this.$nextTick(() => this.updateContainers());
    },
    computeStyle(index) {
      const loca = this.containerLocations[index];
      return {
        top: `${loca.top}px`,
        left: `${loca.left}px`
      };
    }
  }
}
</script>

<style scoped>
.area,
.areaWrapper {
  height: 100%;
  overflow: hidden;
  position: relative;
}

.area>.working {
  position: absolute;
  right: 5px;
  top: 5px;
}

.area>.loading {
  position: absolute;
  left: 50%;
  top: 50%;
  margin-left: -50px;
  margin-top: -50px;
}

.events {
  width: 300px;
  height: 100%;
  max-height: 100%;
  overflow: hidden;
}

.player,
.pool {
  position: absolute;
  overflow: visible;
  transition: top .2s ease-in-out, left .2s ease-in-out, background-color 0.2s cubic-bezier(0.4, 0, 0.6, 1), border .2ms cubic-bezier(0.4, 0, 0.6, 1);
}
</style>
