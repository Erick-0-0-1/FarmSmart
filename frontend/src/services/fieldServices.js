import api from './api';

const getFields = async () => {
  try {
    const response = await api.get('/fields');
    return response.data;
  } catch (error) {
    console.error('Error fetching fields:', error);
    return [];
  }
};

const getFieldById = async (id) => {
  try {
    const response = await api.get(`/fields/${id}`);
    return response.data;
  } catch (error) {
    console.error('Error fetching field:', error);
    throw error;
  }
};

const createField = async (fieldData) => {
  try {
    const response = await api.post('/fields', fieldData);
    return response.data;
  } catch (error) {
    console.error('Error creating field:', error);
    throw error;
  }
};

const updateField = async (id, fieldData) => {
  try {
    const response = await api.put(`/fields/${id}`, fieldData);
    return response.data;
  } catch (error) {
    console.error('Error updating field:', error);
    throw error;
  }
};

const deleteField = async (id) => {
  try {
    await api.delete(`/fields/${id}`);
    return true;
  } catch (error) {
    console.error('Error deleting field:', error);
    throw error;
  }
};

export { getFields, getFieldById, createField, updateField, deleteField };
