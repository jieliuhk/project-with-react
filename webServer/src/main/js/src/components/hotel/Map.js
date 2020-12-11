import React from 'react';
import mapboxgl from 'mapbox-gl';
class Map extends React.Component {
    constructor(props) {
        super(props);
        mapboxgl.accessToken = 'pk.eyJ1IjoiamllbGl1MTU1IiwiYSI6ImNrNHMxcXR6NzJ2NzAzZXE4cWlkeXBwZGkifQ.-gcrmR2f2Q0mAIjzjkTgsQ';
        this.state = {
            lng: props.center.lng,
            lat: props.center.lat,
            zoom: 10
        };
    };

    componentDidMount() {
        const map = new mapboxgl.Map({
            container: this.mapContainer,
            style: 'mapbox://styles/mapbox/streets-v11',
            center: [this.state.lng, this.state.lat],
            zoom: this.state.zoom
        });

        this.props.markers.map( marker => {
            const m = new mapboxgl.Marker()
                .setLngLat([marker.lng, marker.lat])
                .addTo(map)}
        );
    }

    render() {
        return(
            <div>
                <div ref={el => this.mapContainer = el} className="mapContainer" />
            </div>
        );
    }
}
export default Map;