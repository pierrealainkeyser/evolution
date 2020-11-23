function findSpecieAction(actions, rootGetters, specie = true) {
  const onlySpecieId = rootGetters['selection/specieId'];
  if (onlySpecieId) {
    return actions.find(p => onlySpecieId === (specie ? p.specie : p.target));
  }
  return null;
}

const getDefaultState = () => ({
  possibles: null,
  starteds: null,
  playing: null
});

function mapTraits(t) {
  return `${t.specie}-${t.trait}`;
}

function flatMapEffects(events) {
  return events.flatMap(event => {
    if ('specie-food-eaten' === event.type) {
      const effect = {
        specie: event.specie,
        deltaFat: event.fat,
        deltaFood: event.food
      };
      if (event.traits) {
        effect.traits = event.traits.map(mapTraits);
      }

      return [effect];
    } else if ('specie-population-reduced' === event.type) {
      const effect = {
        specie: event.specie,
        population: event.population
      };
      if (event.traits) {
        effect.traits = event.traits.map(mapTraits);
      }

      return [effect];
    }

    return [];
  });
}

function mapViolations(violations) {
  return violations ? violations.map(v => {
    const out = {};

    if (v.bypass) {
      out.disabled = mapTraits(v.bypass);
    }

    if (v.trait) {
      out.type = 'trait';
      out.trait = mapTraits(v.trait);
    }

    if ('size' === v.type) {
      out.type = 'size';
    }

    return out;
  }) : null;
}

const state = getDefaultState();

export default {
  namespaced: true,
  state,
  mutations: {
    setStarteds: (state, starteds) => {
      state.starteds = starteds;
    },
    resetState: (state) => {
      Object.assign(state, getDefaultState());
    },
    loadActions: (state, actions) => {
      if (!actions)
        return;

      state.possibles = actions.flatMap(a => {
        if ('feed' === a.type) {
          const effects = flatMapEffects(a.events);
          return [{
            type: a.type,
            specie: a.specie,
            effects,
            valid: true
          }];
        } else if ('intelligent-feed' === a.type) {
          const effects = flatMapEffects(a.events);
          return [{
            type: a.type,
            specie: a.specie,
            card: a.card,
            effects,
            valid: true
          }];
        } else if ('attack' === a.type) {
          const core = {
            type: a.type,
            specie: a.specie,
            target: a.target,
            valid: a.possible,
            violations: mapViolations(a.violations)
          };

          if (a.possible) {
            return a.outcomes.map(out => {
              return { ...core,
                effects: flatMapEffects(out.events)
              };
            });
          }

          return [core];
        }

        return [];
      });
    },

    resetAction: (state) => {
      state.possibles = null;
    }
  },
  actions: {
    mouseup({
      commit,
      dispatch,
      getters
    }) {
      const act = getters.activable;
      const performs = (act && act.valid) ? JSON.parse(JSON.stringify(act)) : null;
      commit('setStarteds', null);
      if (performs) {
        dispatch('io/emit', performs, {
          root: true
        });
      }
    },
    mousedown({
      state,
      commit,
      getters,
      rootGetters,
      rootState
    }) {
      if (!state.starteds) {
        const startOnSpecie = getters.startOnSpecie;
        const startOnCard = getters.startOnCard;


        if (startOnSpecie) {
          const starteds = state.possibles.filter(p => startOnSpecie === p.specie);
          commit('setStarteds', starteds);
        } else if (startOnCard) {
          if (getters.selectFood) {
            const starteds = [{
              type: 'select-food',
              card: startOnCard.id,
              valid: true
            }];
            commit('setStarteds', starteds);


          } else if (getters.playcard) {

            const myself = rootState.gamestate.myself;
            const species = rootGetters['gamestate/players'][myself].species;
            const starteds = [{
                type: 'add-new-specie',
                card: startOnCard.id,
                position: 'left',
                valid: true
              },
              {
                type: 'add-new-specie',
                card: startOnCard.id,
                position: 'right',
                valid: true
              }
            ];

            species.filter(s => s.size < 6)
              .forEach(s => {
                starteds.push({
                  type: 'increase-size',
                  card: startOnCard.id,
                  target: `SIZE-${s.id}`,
                  size: s.size + 1,
                  targetSpecie: s.id,
                  valid: true
                });
              });

            species.filter(s => s.population < 6)
              .forEach(s => {
                starteds.push({
                  type: 'increase-population',
                  card: startOnCard.id,
                  target: `POPULATION-${s.id}`,
                  population: s.population + 1,
                  targetSpecie: s.id,
                  valid: true
                });
              });

            species.forEach(s => {
              if (!s.traits.some(t => t.trait === startOnCard.trait)) {
                const len = s.traits.length;
                for (var i = 0; i < len; ++i) {
                  starteds.push({
                    type: 'replace-trait',
                    card: startOnCard.id,
                    position: i,
                    trait: startOnCard.trait,
                    target: `${s.id}-${s.traits[i].trait}`,
                    specie: s.id,
                    targetSpecie: s.id,
                    valid: true
                  });
                }

                if (len < 3) {
                  starteds.push({
                    type: 'add-trait',
                    card: startOnCard.id,
                    trait: startOnCard.trait,
                    position: len,
                    target: s.id,
                    targetSpecie: s.id,
                    valid: true
                  });
                }
              }
            });

            commit('setStarteds', starteds);
          }
        }
      }
    },
  },
  getters: {
    playcard: (state, getters, rootState, rootGetters) => {
      const myStatus = rootGetters['gamestate/myStatus'];
      return 'PLAY_CARDS' === myStatus;
    },

    feeding: (state, getters, rootState, rootGetters) => {
      const myStatus = rootGetters['gamestate/myStatus'];
      return 'FEEDING' === myStatus;
    },

    selectFood: (state, getters, rootState, rootGetters) => {
      const myStatus = rootGetters['gamestate/myStatus'];
      return 'SELECT_FOOD' === myStatus;
    },

    startable: (state, getters) => {
      return (!!getters.startOnSpecie) || (!!getters.startOnCard);
    },
    startOnSpecie: (state, getters, rootState, rootGetters) => {
      if (getters.feeding && !state.starteds && state.possibles) {
        const found = findSpecieAction(state.possibles, rootGetters, true);
        if (found)
          return found.specie;

      }
      return null;
    },
    startOnCard: (state, getters, rootState) => {
      if ((getters.selectFood || getters.playcard) && !state.starteds) {
        return rootState.selection.card;
      }
      return null;
    },

    currents: (state, getters) => {
      if (getters.activable)
        return [getters.activable];

      return state.playing;
    },

    activable: (state, getters, rootState, rootGetters) => {
      if (state.starteds) {
        if (getters.feeding) {
          if (getters.startedsType.includes('attack'))
            return findSpecieAction(state.starteds, rootGetters, false);

          if (getters.startedsType.includes('feed')) {
            if (rootGetters['selection/pool']) {
              return state.starteds.find(s => s.type === 'feed');
            }
          }

          if (getters.startedsType.includes('intelligent-feed')) {
            const cardId = rootGetters['selection/cardId'];
            if (cardId) {
              return state.starteds.find(s => s.type === 'intelligent-feed' && s.card === cardId);
            }

          }
        } else if (getters.playcard) {
          const onStat = rootState.selection.stat;
          if (onStat) {
            const specieId = rootGetters['selection/specieId'];
            const target = `${onStat.name}-${specieId}`;
            return state.starteds.find(s => s.target === target);
          }

          const onlySpecieId = rootGetters['selection/onlySpecieId'];
          if (onlySpecieId)
            return state.starteds.find(p => onlySpecieId === p.target);


          const traitId = rootGetters['selection/traitId'];
          if (traitId)
            return state.starteds.find(p => traitId === p.target);

          const newSpecie = rootGetters['selection/newSpecie'];
          if (newSpecie) {
            return state.starteds.find(s => s.type === 'add-new-specie' && s.position === newSpecie);
          }

        } else if (getters.selectFood) {
          if (rootGetters['selection/pool']) {
            return state.starteds.find(s => s.type === 'select-food');
          }
        }
      }

      return null;
    },

    startedsType: (state) => {
      const starteds = state.starteds;
      if (starteds) {
        const actions = [];

        for (var st in starteds) {
          const type = starteds[st].type;
          if (!actions.includes(type)) {
            actions.push(type);
          }
        }
        return actions;
      }
      return null;
    }
  }
}
