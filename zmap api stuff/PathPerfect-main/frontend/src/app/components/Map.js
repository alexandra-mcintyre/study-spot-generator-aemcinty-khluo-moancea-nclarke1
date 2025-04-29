import React, { useEffect, useState } from "react";
import { MapContainer, TileLayer, Polyline, Marker, Popup, useMap } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import axios from "axios";
import { theAlgorithm } from './alg.js';
import L from "leaflet";

const icon = L.icon({
  iconUrl: "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/Google_Maps_pin.svg/1200px-Google_Maps_pin.svg.png",
  iconSize: [18, 30],
});

/**
 * This is the `Maps` component, which displays a map and calculates routes based on user location and a provided distance.
 *
 * @param {Object} props - The component's properties.
 * @param {Object} props.selectPosition - An object containing latitude and longitude for the selected position.
 * @returns {JSX.Element} The JSX element representing the `Maps` component.
 */
function Maps(props) {
  const { selectPosition } = props;
  const locationSelection = [selectPosition?.lat, selectPosition?.lon];
  const [currentPosition, setCurrentPosition] = useState(null);
  const [destPosition, setDestPosition] = useState(null);
  const [routeCoordinates, setRouteCoordinates] = useState(null);
  const [totalDist, setTotalDist] = useState(0.0);
  const api_key = "5b3ce3597851110001cf62488e878fd0f32e4d56819d0aad982c1b81";
  // gets dist
  const [distances, setDist] = useState([])
  let routeCoord = [];
  let sumDist = 0.0;
  let visited = new Set();
  let dist = 0;

  useEffect(() => {

    let requests = [];
    requests.push(
      axios.get('http://localhost:8080/dist')
    )
    
    Promise.all(requests)
        .then((responses) => {
          setDist(responses[0].data);
        })
        .catch((error) => {
          console.error("Error getting routes:", error);
        });
    
  }, []);

  if (distances.length != 0) {
    dist = parseFloat((distances[distances.length-1]['dist'])/69.0);
  }

  console.log(dist);
  

  useEffect(() => {
    if ("geolocation" in navigator) {
      // Geolocation is available
      navigator.geolocation.getCurrentPosition(
        function (position) {
          const latitude = position.coords.latitude;
          const longitude = position.coords.longitude;
          setCurrentPosition([latitude, longitude]);

          let destList = theAlgorithm([latitude, longitude], dist)[0];
          destList = destList.concat([[latitude, longitude]])
          const n = destList.length;

          // // Replace these with the destination coordinates
          // const destinationLatitude1 = 42.3729699; // Replace with destination latitude
          // const destinationLongitude1 = -71.1264053; // Replace with destination longitude
          // const dest1 = [destinationLatitude1, destinationLongitude1];
          // setDestPosition([destinationLatitude1, destinationLongitude1]);

          // const dest2 = [42.3739699, -71.1264053];
          // const dest3 = [42.3829699, -71.1264053];
          // const dest4 = [42.3929699, -71.1264053];

          // let destList = [[latitude, longitude], dest1, dest2, dest3, dest4];

          let requests = [];

          // Get a walking route from OpenRouteService
          for (let i = 1; i < n; i++) {
            let curr = destList[i - 1];
            let dest = destList[i];
            // console.log(new Date(), i);
            requests.push(
              axios.get(
                "https://api.openrouteservice.org/v2/directions/foot-walking?api_key=" +
                  api_key +
                  "&start=" +
                  `${curr[1]}` +
                  "," +
                  `${curr[0]}` +
                  "&end=" +
                  `${dest[1]}` +
                  "," +
                  `${dest[0]}`
              )
            );
          }

          // Wait for all axios requests to complete
          Promise.all(requests)
            .then((responses) => {
              responses.forEach((response, i) => {
                const route = response.data.features[0].geometry.coordinates;
                sumDist += response.data.features[0].properties.summary.distance;
                setTotalDist(sumDist);
                if (!visited.has(i + 1)) {
                  // console.log(new Date(), i + 1);
                  routeCoord = routeCoord.concat(route);
                }
                visited.add(i + 1);
                // console.log(i + 1, destList[i], destList[i + 1]);
              });

              // console.log("final route");
              // console.log(routeCoord);
              setRouteCoordinates(routeCoord);
            })
            .catch((error) => {
              console.error("Error getting routes:", error);
            });
          
        },
        function (error) {
          console.error("Error getting user location:", error);
        }
      );
    } else {
      console.error("Geolocation is not supported by your browser.");
    }
  }, [dist]);

  return (
    <div>
      {currentPosition ? (
        <MapContainer
          center={currentPosition}
          zoom={14}
          style={{ width: "100%", height: "60vh" }}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://api.maptiler.com/maps/basic-v2/256/{z}/{x}/{y}.png?key=5ikt1N4O70If8TMIc2F4"
          />
          {currentPosition && (
            <Marker position={currentPosition} icon={icon}>
              <Popup>
                A pretty CSS3 popup. <br /> Easily customizable.
              </Popup>
            </Marker>
          )}

          {routeCoordinates && (
            <Polyline positions={routeCoordinates.map((coord) => [coord[1], coord[0]])} color="blue" />
          )}
        </MapContainer>
      ) : (
        <p>Loading Path...</p>
      )}
      <div> {(totalDist) / 1609} miles </div>
    </div>
  );
}

export default Maps;
