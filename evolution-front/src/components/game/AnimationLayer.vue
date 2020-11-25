<template>
<div class="animationLayer" ref="layer">
  <div class="animated" ref="attacking" v-if="attacking">
    <v-icon large color="warning">mdi-paw</v-icon>
  </div>
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
  },
  watch: {
    animation() {
      if (this.animation) {
        if (this.animation.type === 'attack') {
          this.startAttack(this.animation);
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
          duration: 0.4
        }, 'start')
        .to(ref, {
          opacity: 1,
          ease: 'power4.inOut',
          duration: 0.2
        }, 'start')
        .to(ref, {
          opacity: 0,
          ease: 'power4.inOut',
          duration: 0.2
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
}

.animated {
  position: absolute;
  display: inline-block;
}
</style>
