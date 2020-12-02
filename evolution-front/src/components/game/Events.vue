<template>
<v-fade-transition group tag="div" class="events">

  <i18n v-for="e in viewed" :key="e.index" :path="`game.events.${e.type}`" tag="p" class="mb-1">
    <template #player v-if="e.player">
      <v-chip small label>{{e.player.name}}</v-chip>
    </template>

    <template #cards v-if="e.type==='player-card-dealed'">
        {{$tc('game.cards',e.count)}}
    </template>

    <template #position v-if="e.type==='specie-added'">
        {{$t(`game.position.${e.position}`)}}
    </template>

    <template #trait v-if="e.type==='specie-trait-added'">
      <em v-if="e.card">
        {{$t(`game.trait.${e.card.trait}`)}}
      </em>
    </template>

    <template #specieName v-if="e.specieName">
      <strong @mouseenter="enter(e.specie)" @mouseleave="leave">{{e.specieName}}</strong>
    </template>

    <template #attackerName v-if="e.attackerName">
      <strong @mouseenter="enter(e.target)" @mouseleave="leave" >{{e.attackerName}}</strong>
    </template>

    <template #foodConsumption v-if="e.source">
        <template v-if="['MEAT','ATTACK'].includes(e.source)">{{$tc('game.eat.meat',e.fat+e.food)}}</template>
    <template v-else>{{$tc('game.eat.plant',e.fat+e.food)}}</template>
    </template>


    <template #pool-revealed v-if="e.type==='pool-revealed'">
        {{$tc(e.delta>=0?'game.pool-added':'game.pool-removed',e.delta)}}
    </template>
  </i18n>
</v-fade-transition>
</template>

<script>
import {
  mapActions,
  mapGetters
} from 'vuex';

export default {
  data() {
    return {
      viewedCount: 15
    };
  },
  computed: {
    ...mapGetters({
      events: 'gamestate/events',
      players: 'gamestate/players',
    }),
    viewed() {
      const events = [...this.events].flatMap((e, index) => {
        if (['player-card-dealed',
            'player-card-added-to-pool',
            'specie-trait-added',
            'specie-added',
            'player-passed'
          ].includes(e.type)) {
          const player = this.players[e.player];
          return [{
            ...e,
            index,
            player: {
              name: player.name
            }
          }];
        }

        if (['new-turn'].includes(e.type)) {
          const player = this.players[e.first];
          return [{
            ...e,
            index,
            player: {
              name: player.name
            }
          }];
        }

        if (['pool-revealed',
            'specie-attacked',
            'specie-food-eaten',
            'specie-extincted',
          ].includes(e.type)) {
          return [{
            ...e,
            index,
          }];
        }

        return [];
      });
      events.reverse();
      while (events.length > this.viewedCount)
        events.pop();
      return events;
    }
  },
  methods: {
    ...mapActions({
      enter: 'selection/enterHoverSpecie',
      leave: 'selection/leaveHoverSpecie'
    })
  }
};
</script>
