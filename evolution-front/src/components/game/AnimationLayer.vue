<template>
<div class="animationLayer" ref="layer">

  <div class="animated" ref="attacking" v-if="attacking">
    <v-icon large color="warning">mdi-paw</v-icon>
  </div>

  <div class="animated" ref="feeding" v-if="feeding">
    <v-icon large color="warning">mdi-cow</v-icon>
  </div>

  <v-overlay :value="overlay">
    <v-alert v-if="newStep" border="left" colored-border color="accent" elevation="2" class="pa-5">
      <h1>{{this.$t('game.step.new')}} {{stepLabel}}</h1>
    </v-alert>

    <v-alert v-if="yourTurn" border="left" colored-border color="primary" elevation="2" class="pa-5">
      <h1>{{this.$t('game.step.yourturn')}}</h1>
    </v-alert>
  </v-overlay>
</div>
</template>

<script>
import {
  gsap
} from "gsap";
import {
  mapGetters,
  mapActions
} from 'vuex';

import boundingBox from '@/mixins/boundingBox';

export default {
  name: 'AnimationLayer',
  mixins: [boundingBox],
  computed: {
    ...mapGetters({
      animation: 'animation/current'
    }),
    attacking() {
      return this.animation && this.animation.type === 'attack';
    },
    feeding() {
      return this.animation && this.animation.type === 'feed';
    },
    newStep() {
      return this.animation && this.animation.type === 'newStep';
    },
    yourTurn() {
      return this.animation && this.animation.type === 'yourTurn';
    },
    overlay() {
      return this.newStep || this.yourTurn;
    },
    stepLabel() {
      return this.animation ? this.$t(`game.step.${this.animation.step}`) : null;
    }
  },
  watch: {
    animation() {
      if (this.animation) {
        const type = this.animation.type;
        if (type === 'attack') {
          this.startAttack(this.animation);
        } else if (type === 'feed') {
          this.startFeed(this.animation);
        } else if (type === 'newStep') {
          this.doneAfter(1500);
        } else if (type === 'yourTurn') {
          this.doneAfter(1000);
        }
      }
    }
  },
  methods: {
    ...mapActions({
      animationDone: 'animation/done'
    }),
    animateRef(iconRefName, sourceBox, targetBox) {
      const iconBox = this.boundingBox(iconRefName);
      const ref = this.$refs[iconRefName];
      const layer = this.boundingBox('layer');

      const center = (box) => ({
        x: box.x + (box.width / 2) - layer.x - (iconBox.width / 2),
        y: box.y + (box.height / 2) - layer.y - (iconBox.height / 2)
      });

      const duration = 0.5;
      const half = duration / 2;

      const me = this;
      gsap.timeline({
          onComplete: () => me.animationDone()
        })
        .add('start', 0)
        .set(ref, {
          ...center(sourceBox),
          opacity: 0
        })
        .to(ref, {
          ...center(targetBox),
          ease: 'power2.out',
          duration
        }, 'start')
        .to(ref, {
          opacity: 1,
          ease: 'power4.inOut',
          duration: half
        }, 'start')
        .to(ref, {
          opacity: 0,
          ease: 'power4.inOut',
          duration: half
        });
    },
    startAttack(animation) {
      const target = animation.target;
      const attacker = animation.attacker;

      this.$nextTick(() => {
        const targetBox = this.$parent.specieBox(target);
        const attackerBox = this.$parent.specieBox(attacker);

        this.animateRef('attacking', attackerBox, targetBox);
      });
    },
    startFeed(animation) {
      const specie = animation.specie;

      this.$nextTick(() => {
        const foodPoolBox = this.$parent.foodPoolBox();
        const specieBox = this.$parent.specieBox(specie);

        this.animateRef('feeding', specieBox, foodPoolBox);
      });
    },
    doneAfter(timer) {
      setTimeout(() => this.animationDone(), timer);
    }
  }
}
</script>

<style scoped>
.animationLayer {
  pointer-events: none;
  width: 100%;
  height: 100%;
  position: absolute;
  top: 0px;
}

.animated {
  position: absolute;
  display: inline-block;
}

.newStep {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
