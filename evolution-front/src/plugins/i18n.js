import Vue from 'vue'
import VueI18n from 'vue-i18n'
import lang from '@/lang';

Vue.use(VueI18n);

export default new VueI18n({
  fallbackLocale: 'fr_FR',
  locale: (navigator.language || 'fr_FR').replace('-', '_'),
  silentTranslationWarn: true,
  messages:lang,
});
