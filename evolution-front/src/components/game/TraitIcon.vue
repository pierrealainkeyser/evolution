<template >
<v-icon v-bind="$attrs" v-on="$listeners">{{traitIcon}}</v-icon>
</template>

  <script>
  export default{
    props:{
      trait: String,
    },
    computed:{
    traitIcon() {
      if ('CARNIVOROUS' === this.trait)
        return 'mdi-paw';


      if ('FAT_TISSUE' === this.trait)
        return  'mdi-pig';


      if ('PACK_HUNTING' === this.trait)
        return  'mdi-strategy';


      if ('AMBUSH' === this.trait)
          return 'mdi-music-note-off';


      if ('WARNING_CALL' === this.trait)
          return 'mdi-volume-high';


      if ('BURROWING' === this.trait)
          return 'mdi-rabbit';


      if ('COLLABORATIVE' === this.trait)
          return 'mdi-bee';


      if ('SCAVENGER' === this.trait)
          return 'mdi-bone';


      if ('CLIMBING' === this.trait)
          return 'mdi-pine-tree';



      if ('HARD_SHELL' === this.trait)
          return 'mdi-turtle';


      if ('HORNED' === this.trait)
          return 'mdi-unicorn-variant';


      if ('INTELLIGENT' === this.trait)
          return 'mdi-face-shimmer';


      if ('FORAGING' === this.trait)
          return 'mdi-excavator';

          return null;
        }
      }
}
</script>
