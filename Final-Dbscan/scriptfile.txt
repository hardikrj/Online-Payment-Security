(function () {
	jDBSCAN = function () {
		//Local instance vars.
		var eps;
		var time_eps;
		var minPts;
		var data = [];
		var clusters = [];
		var status = [];
		var graph = [];
		var distance = euclidean_distance;
		var time_distance = timestamp_distance;

		function timestamp_distance(point1, point2) {
			return Math.abs(point2.timestamp - point1.timestamp);
		}


		function euclidean_distance(point1, point2) {
			return Math.sqrt(Math.pow((point2.x - point1.x), 2) + Math.pow((point2.y - point1.y), 2));
		}

		
		//Core Algorithm Related
		function get_region_neighbours(point_idx) {
			var neighbours = [];
			var d = data[point_idx];

			for (var i = 0; i < data.length; i++) {
				if (point_idx === i) {
					continue;
				}
				if (time_eps &&  (time_distance(data[i], d) <= time_eps) && distance(data[i], d) <= eps) {
					neighbours.push(i);
				} else if (distance(data[i], d) <= eps) {
					neighbours.push(i);
				}
			}

			return neighbours;
		}

		function expand_cluster(point_idx, neighbours, cluster_idx) {
			clusters[cluster_idx - 1].push(point_idx); //add point to cluster
			status[point_idx] = cluster_idx;	//assign cluster id

			for (var i = 0; i < neighbours.length; i++) {
				var curr_point_idx = neighbours[i];
				if (status[curr_point_idx] === undefined) {
					status[curr_point_idx] = 0; //visited and marked as noise by default
					var curr_neighbours = get_region_neighbours(curr_point_idx);
					var curr_num_neighbours = curr_neighbours.length;
					if (curr_num_neighbours >= minPts) {
						expand_cluster(curr_point_idx, curr_neighbours, cluster_idx);
					}
				}

				if (status[curr_point_idx] < 1) { // not assigned to a cluster but visited (= 0)
					status[curr_point_idx] = cluster_idx;
					clusters[cluster_idx - 1].push(curr_point_idx);
				}
			}
		}

		var dbscan = function () {
			status = [];
			clusters = [];

			for (var i = 0; i < data.length; i++) {
				if (status[i] === undefined) {
					status[i] = 0; //visited and marked as noise by default
					var neighbours = get_region_neighbours(i);
					var num_neighbours = neighbours.length;
					if (num_neighbours < minPts) {
						status[i] = 0; //noise
					} else {
						clusters.push([]); //empty new cluster
						var cluster_idx = clusters.length;
						expand_cluster(i, neighbours, cluster_idx);
					}
				}
			}

			return status;
		};

		
		//Getters and setters
		dbscan.data = function (d) {
			if (arguments.length === 0) {
				return data;
			}
			if (Array.isArray(d)) {
				data = d;
			}

			return dbscan;
		};

		dbscan.eps = function (e) {
			if (arguments.length === 0) {
				return eps;
			}
			if (typeof e === "number") {
				eps = e;
			}

			return dbscan;
		};

		dbscan.timeEps = function (e) {
			if (arguments.length === 0) {
				return time_eps;
			}
			if (typeof e === "number") {
				time_eps = e;
			}

			return dbscan;
		};

		dbscan.minPts = function (p) {
			if (arguments.length === 0) {
				return minPts;
			}
			if (typeof p === "number") {
				minPts = p;
			}

			return dbscan;
		};

		dbscan.distance = function (fct) {
			if (arguments.length === 1) {
				if (typeof fct === 'string') {
					switch (fct) {
						case 'HAVERSINE':
							distance = haversine_distance;
							break;
						case 'EUCLIDEAN':
							distance = euclidean_distance;
							break;
						case 'MANHATTAN':
							distance = manhattan_distance;
							break;
						default:
							distance = euclidean_distance;
					}
				} else if (typeof fct === 'function') {
					distance = fct;
				}
			}

			return dbscan;
		};

		dbscan.timeDistance = function (fct) {
			if (arguments.length === 1) {
				time_distance = fct;
			}

			return dbscan;
		};

		return dbscan;
	}
})();