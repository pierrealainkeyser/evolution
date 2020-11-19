<template>
<v-app>
  <Bar />
  <v-main>
    <v-fade-transition mode="out-in">
      <router-view />
    </v-fade-transition>
  </v-main>
</v-app>
</template>

<script>
import Bar from '@/components/system/Bar';

import {
  mapActions
} from 'vuex';

export default {
  name: 'App',
  components: {
    Bar
  },
  data: () => ({
    //
  }),
  methods: {
    ...mapActions({
      receive: 'io/receive',
      connect: 'io/connect',
      bindStore:'ws/bind'
    })
  },

  mounted() {
    //this.connect('1561654651');

    const input = {
      "draw": 2,
      "game": {
        "players": [{
          "index": 0,
          "player": {
            "id": "p1",
            "name": "Joueur 1"
          },
          "inHands": 3,
          "status": "PLAY_CARDS",
          "species": [{
            "id": "0p0",
            "size": 1,
            "food": 0,
            "fat": 0,
            "traits": [],
            "population": 1
          }]
        }, {
          "index": 1,
          "player": {
            "id": "p2",
            "name": "Joueur 2"
          },
          "inHands": 3,
          "status": "PLAY_CARDS",
          "species": [{
            "id": "1p1",
            "size": 1,
            "food": 0,
            "fat": 0,
            "traits": [],
            "population": 1
          }]
        }],
        "scoreBoards": null,
        "step": "PLAY_CARDS",
        "lastTurn": false,
        "foodPool": {
          "food": 0,
          "waiting": 2
        }
      },
      "user": {
        "myself": 0,
        "hand": [{
          "id": "#0",
          "food": 0,
          "trait": "CARNIVOROUS"
        }, {
          "id": "#2",
          "food": 0,
          "trait": "BURROWING"
        }, {
          "id": "#3",
          "food": 0,
          "trait": "DEFENSIVE_HERDING"
        }],
        "actions": null
      },
      "events": [{
        "type": "new-step",
        "step": "SELECT_FOOD"
      }, {
        "type": "player-card-dealed",
        "player": 0,
        "count": 4,
        "shuffle": false,
        "cards": [{
          "id": "#0",
          "food": 0,
          "trait": "CARNIVOROUS"
        }, {
          "id": "#1",
          "food": 0,
          "trait": "COOPERATION"
        }, {
          "id": "#2",
          "food": 0,
          "trait": "BURROWING"
        }, {
          "id": "#3",
          "food": 0,
          "trait": "DEFENSIVE_HERDING"
        }]
      }, {
        "type": "player-card-dealed",
        "player": 1,
        "count": 4,
        "shuffle": false
      }, {
        "type": "player-card-added-to-pool",
        "player": 0,
        "card": {
          "id": "#1",
          "food": 0,
          "trait": "COOPERATION"
        }
      }, {
        "type": "player-card-added-to-pool",
        "player": 1
      }, {
        "type": "new-step",
        "step": "PLAY_CARDS"
      }],
      "type": "complete"
    };
    var fal = false;
    if (fal)
      this.receive(input);
  },

  created() {
    this.$vuetify.theme.dark = true;
    this.bindStore();    
  }
};
</script>
