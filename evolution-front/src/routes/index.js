import LobbyView from '@/views/LobbyView';
import GameView from '@/views/GameView';
import LoginView from '@/views/LoginView';

export default [{
    path: '/',
    component: LobbyView
  },
  {
    path: '/login',
    component: LoginView
  },
  {
    path: '/play/:gameId',
    props: true,
    component: GameView
  }
];
