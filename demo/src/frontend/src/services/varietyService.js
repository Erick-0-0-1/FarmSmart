import api from './api';

// Get all rice varieties
const getVarieties = async () => {
  try {
    const response = await api.get('/varieties');
    return response.data;
  } catch (error) {
    console.error('Error fetching varieties:', error);
    return [];
  }
};

// Get variety by ID
const getVarietyById = async (id) => {
  try {
    const response = await api.get(`/varieties/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching variety:', error);
    throw error;
  }
};

// Get recommended varieties by season
const getRecommendedVarieties = async (season) => {
  try {
    const response = await api.get('/varieties/recommended', {
      params: { season }
    });
    return response.data;
  } catch (error) {
    console.error('Error fetching recommended varieties:', error);
    return [];
  }
};

export { getVarieties, getVarietyById, getRecommendedVarieties };