import api from './api';

const ISABELA_LAT = 16.9754;
const ISABELA_LON = 121.8107;

const getCurrentWeather = async () => {
  try {
    const response = await api.get('/weather/current', {
      params: { lat: ISABELA_LAT, lon: ISABELA_LON }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching current weather:', error);
    throw error;
  }
};

const getForecast = async () => {
  try {
    const response = await api.get('/weather/forecast', {
      params: { lat: ISABELA_LAT, lon: ISABELA_LON }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching forecast:', error);
    throw error;
  }
};

export { getCurrentWeather, getForecast };