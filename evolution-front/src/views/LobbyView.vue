<template>
<v-container>

  <v-dialog v-model="openDialog" persistent>
    <v-card>
      <v-card-title>{{$t('lobby.newGame.title')}}</v-card-title>
      <v-card-text>
        <v-row dense>
          <v-col cols="12">
            <v-autocomplete v-model="selectedIds" :items="users" chips :label="$t('lobby.newGame.players')" item-text="label" item-value="name" multiple />
          </v-col>

          <v-col cols="12">
            <v-checkbox v-model="quickplay" :label="$t('lobby.newGame.quickplay')" />
          </v-col>
        </v-row>
      </v-card-text>
      <v-card-actions>
        <v-btn @click="start" :disabled="unStartable">{{$t('lobby.newGame.start')}}</v-btn>
        <v-btn @click="openDialog=false" outlined>{{$t('lobby.newGame.cancel')}}</v-btn>
      </v-card-actions>
    </v-card>
  </v-dialog>

  <v-row dense>
    <v-col cols="12">
      <v-btn @click="openDialog=true">{{$t('lobby.newGame.new')}}</v-btn>
    </v-col>
    <v-col cols="12">

      <v-simple-table>
        <thead>
          <tr>
            <td>{{$t('lobby.game.date')}}</td>
            <td>{{$t('lobby.game.players')}}</td>
            <td>{{$t('lobby.game.status')}}</td>
            <td>{{$t('lobby.game.results')}}</td>
            <td>{{$t('lobby.game.score')}}</td>
          </tr>
        </thead>
        <tbody>
          <tr v-for="g in games" :key="g.game" @click="toGame(g)">
            <td>{{formatDate(g.created)}}</td>
            <td>{{formatPlayers(g)}}</td>
            <td>
              <v-icon>{{g.terminated?'checkbox-marked-circle-outline':'mdi-motion-play-outline'}}</v-icon>
            </td>
            <td>
              <template v-if="g.terminated">
                    {{$t(alpha?'lobby.game.winner':'lobby.game.looser')}}
                  </template>
            </td>
            <td>
              <template v-if="g.terminated">
                    {{g.score}}
                  </template>
            </td>

          </tr>
        </tbody>
      </v-simple-table>
    </v-col>
  </v-row>


</v-container>
</template>

<script>
import {
  mapGetters
} from 'vuex';
import axios from '@/services/axios';
import moment from 'moment';


export default {
  name: 'LobbyView',
  data() {
    return {
      openDialog: false,
      quickplay: false,
      selectedIds: [],
    };
  },
  computed: {
    ...mapGetters({
      games: 'overview/games',
      users: 'overview/users',
      myself: 'user/myself'
    }),
    unStartable() {
      return this.selectedPlayers.length < 2;
    },
    selectedPlayers() {
      const users = [this.myself];
      this.selectedIds.forEach((id) => {
        const user = this.users.find(u => u.name === id);
        if (user)
          users.push(user);
      });
      return users;
    }
  },
  methods: {
    start() {
      const game = {
        players: this.selectedPlayers,
        quickplay: this.quickplay,
        traits: []
      }
      this.openDialog = false;
      axios.post('/game/bootstrap', game)
        .then(response => {
          this.toGame(response.data);
        });
    },
    toGame(g) {
      this.$router.push(`/play/${g.playerUUID}`);
    },
    formatPlayers(g) {
      return g.players.join(', ');
    },
    formatDate(date) {
      return moment(date).format('YYYY/MM/DD HH:mm:ss');
    },
  }
};
</script>
