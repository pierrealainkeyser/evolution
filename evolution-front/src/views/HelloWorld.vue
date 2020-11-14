<template>
<v-container @mousemove="mousemove" @mousedown="mousedown" @mouseup="mouseup" @mouseleave="mouseup" fluid class="area pa-0" ref="container" :style="containerStyle">
  <v-btn icon small @click="incRotation(1)">
    <v-icon>mdi-rotate-left</v-icon>
  </v-btn>
  <v-btn icon small @click="incRotation(-1)">
    <v-icon>mdi-rotate-right</v-icon>
  </v-btn>


  <Player ref="player" :playerId="index" class="player" v-for="(p,index) in players" :key="index" :style="computeStyle(index)" />


  <Pool ref="pool" class="pool" :style="poolStyle" />
</v-container>
</template>

<script>
import {
  mapState,
  mapActions,
  mapGetters
} from 'vuex';

import Player from '@/components/game/Player';
import Pool from '@/components/game/Pool';

export default {
  name: 'HelloWorld',
  components: {
    Player,
    Pool
  },
  data() {
    return {
      fab: false,
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
      }
    };
  },
  computed: {
    ...mapState({
      players: state => state.gamestate.players,
      rotation: state => state.selection.rotation,
      started: state => !!state.action.starteds
    }),
    ...mapGetters({
      startable: 'action/startable',
      activable: 'action/activable'
    }),
    degForPlayer() {
      return 360.0 / this.players.length;
    },
    containerStyle() {
      const style = {};
      if (this.activable) {
        if (this.activable.valid)
          style.cursor = 'crosshair';
        else
          style.cursor = 'move';
      } else if (this.startable) {
        style.cursor = 'pointer';
      } else if (this.started) {
        style.cursor = 'move';
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
      var paddingT = 80;
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
          const elem = this.$refs.player[index].$el;
          if (elem.clientWidth && elem.clientHeight) {
            return {
              left: location.left - (elem.clientWidth / 2),
              top: location.top - (elem.clientHeight / 2)
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
  },
  methods: {
    ...mapActions({
      mousedown: 'action/mousedown',
      mouseup: 'action/mouseup',
      mousemove: 'selection/mouseMove',
      incRotation: 'selection/incRotation'
    }),
    updateContainers() {
      const container = this.$refs.container;
      this.container.height = container.clientHeight;
      this.container.width = container.clientWidth;

      const pool = this.$refs.pool.$el;
      this.pool.height = pool.clientHeight;
      this.pool.width = pool.clientWidth;
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
.area {
  height: 100%;
  overflow: hidden;
  position: relative;
}

.player,
.pool {
  position: absolute;
  overflow: visible;
  transition: top .2s ease-in-out, left .2s ease-in-out, background-color 0.2s cubic-bezier(0.4, 0, 0.6, 1), border .2ms cubic-bezier(0.4, 0, 0.6, 1);
}
</style>
