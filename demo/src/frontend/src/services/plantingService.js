import api from './api';

// Get all plantings for the current user
const getPlantings = async () => {
  try {
    const response = await api.get('/plantings');
    return response.data;
  } catch (error) {
    console.error('Error fetching plantings:', error);
    return [];
  }
};

// Get a single planting by ID
const getPlantingById = async (id) => {
  try {
    const response = await api.get(`/plantings/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching planting:', error);
    throw error;
  }
};

// Create a new planting
const createPlanting = async (plantingData) => {
  try {
    const response = await api.post('/plantings', plantingData);
    return response.data;
  } catch (error) {
    console.error('Error creating planting:', error);
    throw error;
  }
};

// Update an existing planting
const updatePlanting = async (id, plantingData) => {
  try {
    const response = await api.put(`/plantings/${id}`, plantingData);
    return response.data;
  } catch (error) {
    console.error('Error updating planting:', error);
    throw error;
  }
};

// Update planting status
const updatePlantingStatus = async (id, status) => {
  try {
    const response = await api.patch(`/plantings/${id}/status`, { status });
    return response.data;
  } catch (error) {
    console.error('Error updating planting status:', error);
    throw error;
  }
};

// Mark planting as harvested
const markAsHarvested = async (id, harvestDate = new Date().toISOString()) => {
  try {
    const response = await api.post(`/plantings/${id}/harvest`, {
      actualHarvestDate: harvestDate
    });
    return response.data;
  } catch (error) {
    console.error('Error marking planting as harvested:', error);
    throw error;
  }
};

// Delete a planting
const deletePlanting = async (id) => {
  try {
    await api.delete(`/plantings/${id}`);
    return true;
  } catch (error) {
    console.error('Error deleting planting:', error);
    throw error;
  }
};

export {
  getPlantings,
  getPlantingById,
  createPlanting,
  updatePlanting,
  updatePlantingStatus,
  markAsHarvested,
  deletePlanting
};