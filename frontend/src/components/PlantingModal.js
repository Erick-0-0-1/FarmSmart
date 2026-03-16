import React, { useState, useEffect } from 'react';
import { useLanguage } from '../context/LanguageContext';

function PlantingModal({ isOpen, onClose, onSubmit, planting, fields, varieties }) {
  const { t } = useLanguage();

  const [formData, setFormData] = useState({
    fieldId: '',
    riceVarietyId: '',
    plantingDate: new Date().toISOString().split('T')[0],
    status: 'PLANNING',
    notes: ''
  });

  useEffect(() => {
    if (planting) {
      setFormData({
        fieldId: planting.field?.id || '',
        riceVarietyId: planting.riceVariety?.id || '',
        plantingDate: planting.plantingDate?.split('T')[0] || new Date().toISOString().split('T')[0],
        status: planting.status || 'PLANNING',
        notes: planting.notes || ''
      });
    } else {
      setFormData({
        fieldId: '',
        riceVarietyId: '',
        plantingDate: new Date().toISOString().split('T')[0],
        status: 'PLANNING',
        notes: ''
      });
    }
  }, [planting]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(formData);
  };

  const getSelectedVariety = () => {
    return varieties.find(v => v.id === parseInt(formData.riceVarietyId));
  };

  const selectedVariety = getSelectedVariety();

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white dark:bg-gray-800 rounded-lg shadow-xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        {/* Header */}
        <div className="px-6 py-4 border-b border-gray-200 dark:border-gray-700 flex justify-between items-center sticky top-0 bg-white dark:bg-gray-800 z-10">
          <h2 className="text-2xl font-bold text-gray-900 dark:text-white">
            {planting ? '✏️ Edit Planting' : '🌱 New Planting'}
          </h2>
          <button
            onClick={onClose}
            className="text-gray-400 dark:text-gray-500 hover:text-gray-600 dark:hover:text-gray-300 text-2xl"
          >
            ×
          </button>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit} className="px-6 py-4">
          <div className="space-y-4">
            {/* Field Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Select Field *
              </label>
              <select
                name="fieldId"
                value={formData.fieldId}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              >
                <option value="">Choose a field...</option>
                {fields.map(field => (
                  <option key={field.id} value={field.id}>
                    {field.name} ({field.areaHectares} ha)
                  </option>
                ))}
              </select>
              {fields.length === 0 && (
                <p className="text-sm text-red-600 dark:text-red-400 mt-1">
                  ⚠️ You need to create a field first!
                </p>
              )}
            </div>

            {/* Rice Variety Selection */}
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Select Rice Variety *
              </label>
              <select
                name="riceVarietyId"
                value={formData.riceVarietyId}
                onChange={handleChange}
                required
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
              >
                <option value="">Choose a variety...</option>
                {varieties.map(variety => (
                  <option key={variety.id} value={variety.id}>
                    {variety.name} ({variety.maturityDays} days)
                  </option>
                ))}
              </select>
            </div>

            {/* Variety Info Card */}
            {selectedVariety && (
              <div className="bg-green-50 dark:bg-green-900/30 border border-green-200 dark:border-green-800 rounded-lg p-4">
                <h3 className="text-sm font-semibold text-green-900 dark:text-green-300 mb-2">
                  📋 {selectedVariety.name} Details
                </h3>
                <div className="grid grid-cols-2 gap-3 text-sm text-green-800 dark:text-green-400">
                  <div>
                    <strong>Maturity:</strong> {selectedVariety.maturityDays} days
                  </div>
                  <div>
                    <strong>Season:</strong> {selectedVariety.season}
                  </div>
                  <div>
                    <strong>Yield:</strong> {selectedVariety.yieldPotential} t/ha
                  </div>
                  <div>
                    <strong>Drought Tolerance:</strong> {selectedVariety.droughtTolerance}
                  </div>
                </div>
                {selectedVariety.description && (
                  <p className="text-xs text-green-700 dark:text-green-500 mt-2">
                    {selectedVariety.description}
                  </p>
                )}
              </div>
            )}

            {/* Planting Date and Status Grid */}
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Planting Date *
                </label>
                <input
                  type="date"
                  name="plantingDate"
                  value={formData.plantingDate}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                />
              </div>

              <div>
                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                  Status *
                </label>
                <select
                  name="status"
                  value={formData.status}
                  onChange={handleChange}
                  required
                  className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                >
                  <option value="PLANNING">📋 Planning</option>
                  <option value="PLANTED">🌱 Planted</option>
                  <option value="GROWING">🌾 Growing</option>
                  <option value="HARVESTED">✅ Harvested</option>
                  <option value="CANCELLED">❌ Cancelled</option>
                </select>
              </div>
            </div>

            {/* Expected Harvest Date Info */}
            {formData.plantingDate && selectedVariety && (
              <div className="bg-blue-50 dark:bg-blue-900/30 border border-blue-200 dark:border-blue-800 rounded-lg p-4">
                <p className="text-sm text-blue-900 dark:text-blue-300">
                  <strong>📅 Expected Harvest Date:</strong>{' '}
                  {new Date(
                    new Date(formData.plantingDate).getTime() +
                    selectedVariety.maturityDays * 24 * 60 * 60 * 1000
                  ).toLocaleDateString('en-US', {
                    month: 'long',
                    day: 'numeric',
                    year: 'numeric'
                  })}
                </p>
                <p className="text-xs text-blue-800 dark:text-blue-400 mt-1">
                  (Calculated based on {selectedVariety.maturityDays} days maturity period)
                </p>
              </div>
            )}

            {/* Notes */}
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
                Notes (Optional)
              </label>
              <textarea
                name="notes"
                value={formData.notes}
                onChange={handleChange}
                rows="3"
                className="w-full px-4 py-2 border border-gray-300 dark:border-gray-600 rounded-md focus:ring-2 focus:ring-green-500 focus:border-transparent bg-white dark:bg-gray-700 text-gray-900 dark:text-white"
                placeholder="Add any notes about this planting..."
              />
            </div>
          </div>

          {/* Actions */}
          <div className="flex gap-3 mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
            <button
              type="button"
              onClick={onClose}
              className="flex-1 px-4 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-md hover:bg-gray-50 dark:hover:bg-gray-700 transition font-medium"
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={fields.length === 0}
              className="flex-1 px-4 py-2 bg-green-600 dark:bg-green-700 text-white rounded-md hover:bg-green-700 dark:hover:bg-green-600 transition font-medium disabled:bg-gray-400 disabled:cursor-not-allowed"
            >
              {planting ? 'Update Planting' : 'Create Planting'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

export default PlantingModal;