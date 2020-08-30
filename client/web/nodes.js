new Vue({
  el: "#nodes",
  data: {
    nodes: []
  },
  methods: {
    refresh: function () {
      axios.get(`http://localhost:8080/nodes`).then(response => (this.nodes = response.data))
    }
  },
  mounted: function() {
    this.refresh()
  }
});
