export default {
  methods: {
    boundingBox(ref) {
      const client = this.$refs[ref].getBoundingClientRect();
      return {
        x: client.x,
        y: client.y,
        width: client.width,
        height: client.height
      };
    },
  }
}
