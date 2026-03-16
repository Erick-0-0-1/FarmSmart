import api from './api';

// Generate fertilizer schedule for a planting
const generateSchedule = async (plantingId) => {
  try {
    const response = await api.post(`/fertilizer/generate/${plantingId}`);
    return response.data;
  } catch (error) {
    console.error('Error generating fertilizer schedule:', error);
    throw error;
  }
};

// Get all fertilizer applications for current user
const getAllApplications = async () => {
  try {
    const response = await api.get('/fertilizer/applications');
    return response.data;
  } catch (error) {
    console.error('Error fetching fertilizer applications:', error);
    return [];
  }
};

// Get fertilizer applications for a specific planting
const getApplicationsByPlanting = async (plantingId) => {
  try {
    const response = await api.get(`/fertilizer/planting/${plantingId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching applications for planting:', error);
    return [];
  }
};

// Get smart recommendation for a fertilizer application
const getRecommendation = async (applicationId) => {
  try {
    const response = await api.get(`/fertilizer/recommendation/${applicationId}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching recommendation:', error);
    throw error;
  }
};

// Mark fertilizer application as completed
const markAsCompleted = async (applicationId, completionData = {}) => {
  try {
    const response = await api.post(`/fertilizer/${applicationId}/complete`, {
      actualApplicationDate: completionData.actualApplicationDate || new Date().toISOString(),
      notes: completionData.notes || ''
    });
    return response.data;
  } catch (error) {
    console.error('Error marking application as completed:', error);
    throw error;
  }
};

// Postpone fertilizer application
const postponeApplication = async (applicationId, postponeData) => {
  try {
    const response = await api.post(`/fertilizer/${applicationId}/postpone`, {
      newScheduledDate: postponeData.newScheduledDate,
      reason: postponeData.reason || 'Postponed due to weather conditions'
    });
    return response.data;
  } catch (error) {
    console.error('Error postponing application:', error);
    throw error;
  }
};

// Skip fertilizer application
const skipApplication = async (applicationId, reason) => {
  try {
    const response = await api.post(`/fertilizer/${applicationId}/skip`, {
      reason: reason || 'Skipped application'
    });
    return response.data;
  } catch (error) {
    console.error('Error skipping application:', error);
    throw error;
  }
};

// Update fertilizer application
const updateApplication = async (applicationId, updateData) => {
  try {
    const response = await api.put(`/fertilizer/${applicationId}`, updateData);
    return response.data;
  } catch (error) {
    console.error('Error updating application:', error);
    throw error;
  }
};

// Delete fertilizer application
const deleteApplication = async (applicationId) => {
  try {
    await api.delete(`/fertilizer/${applicationId}`);
    return true;
  } catch (error) {
    console.error('Error deleting application:', error);
    throw error;
  }
};

export {
  generateSchedule,
  getAllApplications,
  getApplicationsByPlanting,
  getRecommendation,
  markAsCompleted,
  postponeApplication,
  skipApplication,
  updateApplication,
  deleteApplication
};